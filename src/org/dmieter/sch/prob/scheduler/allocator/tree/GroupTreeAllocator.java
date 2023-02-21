package org.dmieter.sch.prob.scheduler.allocator.tree;

import org.dmieter.sch.prob.experiment.stat.NamedStats;
import org.dmieter.sch.prob.job.Job;
import org.dmieter.sch.prob.scheduler.allocator.*;
import org.dmieter.sch.prob.user.ResourceRequest;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.stream.Collectors;

public class GroupTreeAllocator extends AbstractGroupAllocator{

    public enum AllocationAlgorithm {KNAPSACK, GREEDY};
    public static AllocationAlgorithm intermediateAllocation = AllocationAlgorithm.GREEDY;
    public static AllocationAlgorithm finalAllocation = AllocationAlgorithm.KNAPSACK;

    public static NamedStats treeSize = new NamedStats("TREE");

    public static List<ResourceAvailability> allocateResources(
            Job job,
            List<ResourceAvailabilityGroup> resourceGroups,
            Integer startTime,
            Integer endTime) {

        return allocatePricedResources(
                job,
                estimateResourcesCost(resourceGroups, startTime, endTime),
                startTime,
                endTime);

    }

    public static List<ResourceAvailability> allocatePricedResources(
            Job job,
            List<ResourceAvailabilityGroup> resourceGroups,
            Integer startTime,
            Integer endTime) {

        PriorityQueue<Node> tree = new PriorityQueue<>(Comparator.comparing(n -> -n.upperEstinate));
        Node resultingNode = null;
        Node startingNode = new Node();

        if(performNodeOptimization(startingNode, job, resourceGroups, startTime, endTime, intermediateAllocation)) {
            tree.add(startingNode);
        } else {
            return null; // no solution found from starting node
        }

        int i = 0;
        // main cycle over the solution tree
        while(!tree.isEmpty()){
            treeSize.addValue("SIZE", ((Integer)tree.size()).doubleValue());
            Node nextNode = tree.poll();
            //System.out.println("Next node with upper estimate" + nextNode.upperEstinate);

            if(nextNode.splitGroup == null) {
                resultingNode = nextNode;  // solution!!
                treeSize.addValue("FINAL SIZE", ((Integer)tree.size()).doubleValue());
                //System.out.println("Node " + i + " is solution!");
                break;
            } else {
                // split to two nodes
                Node node1 = nextNode.copy();
                node1.includedGroups.add(nextNode.splitGroup);
                if(performNodeOptimization(node1, job, resourceGroups, startTime, endTime, intermediateAllocation)) {
                    //System.out.println("New node with upper estimate" + node1.upperEstinate);
                    tree.add(node1);
                }

                Node node2 = nextNode.copy();
                node2.excludedGroups.add(nextNode.splitGroup);
                if(performNodeOptimization(node2, job, resourceGroups, startTime, endTime, intermediateAllocation)) {
                    //System.out.println("New node with upper estimate" + node2.upperEstinate);
                    tree.add(node2);
                }
            }
        }

        // checking if final allocation should be performed
        if(resultingNode == null) {
            return null;
        } else if(intermediateAllocation != finalAllocation) {
            Node finalizedNode = resultingNode.copy();
            if (performNodeOptimization(finalizedNode, job, resourceGroups, startTime, endTime, finalAllocation)
                    && finalizedNode.splitGroup == null) {
                return finalizedNode.solution;
            } else {
                System.out.println("Final allocation with " + finalAllocation + " failed. Returning result allocated with " + intermediateAllocation);
            }
        }

        return resultingNode.solution;

        //return resultingNode == null ? null : resultingNode.solution;
    }


    public static boolean performNodeOptimization(Node node,
                                                              Job job,
                                                              List<ResourceAvailabilityGroup> resourceGroups,
                                                              Integer startTime,
                                                              Integer endTime,
                                                              AllocationAlgorithm allocationAlgorithm) {

        // here we  will store resources from included groups
        List<ResourceAvailability> initialSolution = new ArrayList<>();

        // recalculate resources availability based on included groups
        List<ResourceAvailability> preparedResources = prepareResources(node, job, resourceGroups, initialSolution);

        // update job constraints (res number, budget) taking into account included groups and initial solution
        Job preparedJob = prepareJob(node, job, initialSolution);

        List<ResourceAvailability> solution = null;
        if(preparedResources.size() >= preparedJob.getResourceRequest().getParallelNum()) {
            // solution for new prepared problem
            switch(allocationAlgorithm){
                case GREEDY: solution = GreedyMaxPLimitedAllocator.allocateResources(preparedJob, preparedResources, startTime, endTime); break;
                case KNAPSACK: solution = KnapsackMaxPAllocator.allocateResources(preparedJob, preparedResources, startTime, endTime); break;
                default: throw new IllegalStateException("Unknown Allocation algorithm specified: " + allocationAlgorithm);
            }

        }

        if(solution != null) {
            solution.addAll(initialSolution);
        } else {
            return false;
        }

        // populating node result
        node.solution = solution;
        node.upperEstinate = calculateTotalEstimatedProbability(solution);  // we should calculate estimated probability based on currently estimated respurces' Ps, not by their groups

        List<ResourceAvailabilityGroup> partialGroups = findPartialGroups(solution, resourceGroups, node);
        if(!partialGroups.isEmpty()) {
            ResourceAvailability promisingResource = findPromisingResource(solution, partialGroups);
            if (promisingResource != null) {
                node.splitGroup = promisingResource.getGroup();
            }
        }

        return true;
    }

    public static ResourceAvailability findPromisingResource(List<ResourceAvailability> solution,
                                                      List<ResourceAvailabilityGroup> partialGroups) {
        return solution.stream()
                .filter(r -> partialGroups.contains(r.getGroup()))
                .max(Comparator.comparing(GroupTreeAllocator::calcResourceUtility))
                .orElse(null);
    }

    public static Double calcResourceUtility(ResourceAvailability resource){
        return resource.getAvailabilityP()/resource.getResource().getDescription().price;
    }

    public static Double calculateTotalEstimatedProbability(List<ResourceAvailability> solution) {
        return solution.stream()
                .map(r -> r.getAvailabilityP())
                .reduce((p1,p2) -> p1*p2).get();
    }

    public static List<ResourceAvailabilityGroup> findPartialGroups(List<ResourceAvailability> solution,
                                                             List<ResourceAvailabilityGroup> resourceGroups,
                                                             Node node) {

        return resourceGroups.stream()
                .filter(g -> !node.excludedGroups.contains(g) && !node.includedGroups.contains(g))
                .filter(g -> !isGroupFullyUsed(solution, g))
                .filter(g -> isGroupUsed(solution, g))
                .collect(Collectors.toList());
    }

    public static boolean isGroupFullyUsed(List<ResourceAvailability> solution,
                                    ResourceAvailabilityGroup group) {

        Long resourcesUsedCnt = solution.stream()
                .filter(r -> r.group == group)
                .count();

        return  resourcesUsedCnt == solution.size() || resourcesUsedCnt == group.getResources().size();
    }

    public static boolean isGroupUsed(List<ResourceAvailability> solution,
                                           ResourceAvailabilityGroup group) {
        return solution.stream()
                .anyMatch(r -> r.group == group);
    }

    public static List<ResourceAvailability> prepareResources(Node node,
                                                       Job job,
                                                       List<ResourceAvailabilityGroup> resourceGroups,
                                                       List<ResourceAvailability> initialSolution) {

        List<ResourceAvailability> candidateResources = new ArrayList<>();

        /* 1. Handle included groups */
        node.includedGroups.forEach(g -> {

            // find resource with minimum price
            ResourceAvailability minCostResource = g.getResources().stream()
                    .min(Comparator.comparing(r -> r.getResource().getDescription().price)).get();
            minCostResource.setAvailabilityP(g.getAvailabilityP());

            // add it to the initial solution (group is included)
            initialSolution.add(minCostResource);

            // add al other related resources to candidates
            candidateResources.addAll(
                    g.getResources().stream()
                            .filter(r -> r != minCostResource)
                            .peek(r -> r.setAvailabilityP(1d))
                            .collect(Collectors.toList()));

        });

        /* 2. Handle other groups */
        Integer resourcesLeft = job.getResourceRequest().getParallelNum() - node.includedGroups.size();
        if (resourcesLeft > 0) {
            // for each group left (not excluded or included)
            resourceGroups.stream()
                    .filter(g -> !node.excludedGroups.contains(g) && !node.includedGroups.contains(g))
                    .forEach(g -> {
                        // calculate partial availability based on maximum possible number of resources selected from this group
                        // if selected all - then full availability should be accounted
                        Integer maxSelectedNumber = Math.min(g.getResources().size(), resourcesLeft);
                        Double partialAvailability = Math.pow(g.getAvailabilityP(), 1.0 / maxSelectedNumber);

                        // add all such resources to candidates
                        candidateResources.addAll(
                                g.getResources().stream()
                                        .peek(r -> r.setAvailabilityP(partialAvailability))
                                        .collect(Collectors.toList()));

                    });
        }

        return candidateResources;
    }

    public static Job prepareJob(Node node, Job baseJob, List<ResourceAvailability> initialSolution) {
        Job preparedJob = baseJob.copy();
        ResourceRequest request = preparedJob.getResourceRequest();

        Integer resourcesLeft = request.getParallelNum() - node.includedGroups.size();
        if(resourcesLeft > 0 && initialSolution.size() > 0) {
            request.setParallelNum(resourcesLeft);
            Long initialCost = Math.round(initialSolution.stream()
                    .map(ra -> (ResourceAvailabilityPriced) ra)
                    .map(r -> r.cost)
                    .reduce((c1, c2) -> c1 + c2).get());

            request.setBudget(request.getBudget() - initialCost.intValue());
        }

        return preparedJob;
    }


}
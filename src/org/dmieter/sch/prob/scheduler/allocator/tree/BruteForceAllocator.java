package org.dmieter.sch.prob.scheduler.allocator.tree;

import org.dmieter.sch.prob.job.Job;
import org.dmieter.sch.prob.scheduler.allocator.ResourceAvailability;
import org.dmieter.sch.prob.scheduler.allocator.ResourceAvailabilityGroup;
import org.dmieter.sch.prob.scheduler.allocator.ResourceAvailabilityPriced;

import java.util.*;
import java.util.stream.Collectors;

public class BruteForceAllocator extends AbstractGroupAllocator {

    public static List<ResourceAvailability> allocateResources(
            Job job,
            List<ResourceAvailabilityGroup> resourceGroups,
            Integer startTime,
            Integer endTime) {

        List<ResourceAvailabilityGroup> costedGroups = estimateResourcesCost(resourceGroups, startTime, endTime);
        List<ResourceAvailabilityPriced> resources = costedGroups.stream()
                .flatMap(g -> g.getResources().stream())
                .map(ra -> (ResourceAvailabilityPriced)ra)
                .collect(Collectors.toList());

        SolutionStats solutionStats = iterateLimited(job, new ArrayList<>(resources));

        if(solutionStats.isFeasible) {
            return solutionStats.solution.stream().map( r -> (ResourceAvailability)r).collect(Collectors.toList());
        } else {
            return null;
        }

    }

    protected static SolutionStats iterateLimited(Job job, ArrayList<ResourceAvailabilityPriced> resources) {
        Set<Integer> usedResources = new HashSet<>();

        return checkNextConfiguration(resources, usedResources, job);

    }

    protected static SolutionStats checkNextConfiguration(ArrayList<ResourceAvailabilityPriced> resources,
                                                    Set<Integer> usedResources, Job job) {



        // if this is the bottom level
        if(usedResources.size() == job.getResourceRequest().getParallelNum()) {
            //System.out.println(usedResources); // LOG
            ArrayList<ResourceAvailabilityPriced> solution = new ArrayList<>();
            for(Integer i = 0; i < resources.size(); i++) {
                if(usedResources.contains(i)){
                    solution.add(resources.get(i));
                }
            }
            SolutionStats currentStats = estimateSolution(job, solution);
            currentStats.solution = solution;
            return currentStats;

        // else we should iterate over lower levels
        } else {
            SolutionStats resultingSolution = new SolutionStats(false, -1d, 0d);
            Integer previousNumber = -1;
            for(int i = 0; i < resources.size(); i++) {
                if(!usedResources.contains(i)) {
                    usedResources.remove(previousNumber);
                    usedResources.add(i);
                    previousNumber = i;

                    SolutionStats currentStats = checkNextConfiguration(resources, usedResources, job);
                    // selecting feasible solution with max probability and minimum total cost as second criteria
                    Double epsilon = 0.000001;  // epsilon used to check if probability is the same
                    Double probabilityDiff = currentStats.probability - resultingSolution.probability;
                    if(currentStats.isFeasible &&
                            (probabilityDiff > epsilon // new solution is way better by P
                            || Math.abs(probabilityDiff) < epsilon && currentStats.totalCost < resultingSolution.totalCost)  // new solution better by cost
                    ){
                        resultingSolution = currentStats;
                    }
                }
                usedResources.remove(previousNumber);
            }

            return resultingSolution;
        }
    }

}

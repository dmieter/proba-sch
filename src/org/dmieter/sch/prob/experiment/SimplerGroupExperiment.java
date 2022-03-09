package org.dmieter.sch.prob.experiment;

import org.apache.commons.math3.geometry.euclidean.oned.Interval;
import org.dmieter.sch.prob.SchedulingController;
import org.dmieter.sch.prob.distribution.Distribution;
import org.dmieter.sch.prob.distribution.NormalEventDistribution;
import org.dmieter.sch.prob.distribution.QuazyUniformDistribution;
import org.dmieter.sch.prob.events.Event;
import org.dmieter.sch.prob.events.EventType;
import org.dmieter.sch.prob.experiment.stat.NamedStats;
import org.dmieter.sch.prob.generator.ResourceGenerator;
import org.dmieter.sch.prob.job.Job;
import org.dmieter.sch.prob.job.RegularJob;
import org.dmieter.sch.prob.resources.Resource;
import org.dmieter.sch.prob.resources.ResourceDomain;
import org.dmieter.sch.prob.resources.ResourcesAllocation;
import org.dmieter.sch.prob.scheduler.allocator.*;
import org.dmieter.sch.prob.scheduler.allocator.tree.AbstractGroupAllocator;
import org.dmieter.sch.prob.scheduler.allocator.tree.BruteForceAllocator;
import org.dmieter.sch.prob.scheduler.allocator.tree.GroupTreeAllocator;
import org.dmieter.sch.prob.scheduler.criteria.AvailableProbabilityCriterion;
import org.dmieter.sch.prob.scheduler.criteria.UserPreferenceModel;
import org.dmieter.sch.prob.user.ResourceRequest;
import project.math.distributions.GaussianFacade;
import project.math.distributions.GaussianSettings;

import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * @author dmieter
 */
public class SimplerGroupExperiment implements Experiment {
    
    private SchedulingController schedulingController;

    private final NamedStats bruteStats = new NamedStats("BRUTE");
    private final NamedStats treeStatsGreedy = new NamedStats("TREE Greedy");
    private final NamedStats treeStatsKnapsack = new NamedStats("TREE Knapsack");
    private final NamedStats compareStats = new NamedStats("COMPARISON");

    @Override
    public void run(int expNnum) {

        for (int i = 0; i < expNnum; i++) {
            System.out.println(i);
            runSingleExperiment();
        }
    }

    public void runSingleExperiment() {
        ResourceDomain domain = generateResources(15);
        int startTime = 0;
        int finishTime = 1;
        
        
        schedulingController = new SchedulingController(domain);
        List<ResourceAvailability> resources = generateUtilization(schedulingController, 0.1d /* LOAD SD */, startTime, finishTime);
        List<ResourceAvailabilityGroup> groups = groupifyResourcesRandom(resources, 5);

        //System.out.println(AbstractGroupAllocator.explainProblem(null, groups, null, startTime, finishTime));

        Job job = generateJobFlow(6).get(0);

        boolean success = true;

        Job jobTreeGreedy = job.copy();
        GroupTreeAllocator.intermediateAllocation = GroupTreeAllocator.IntermediateAllocation.GREEDY;
        Long timeStartTree = System.nanoTime();
        List<ResourceAvailability> resultTreeGreedy = GroupTreeAllocator.allocateResources(jobTreeGreedy, groups, startTime, finishTime);
        Long durationTreeGreedy = System.nanoTime() - timeStartTree;
        if(resultTreeGreedy == null) {
            success = false;
            treeStatsGreedy.addValue("Fails", 1d);
        } else {
            treeStatsGreedy.addValue("Fails", 0d);
        }

        Job jobTreeKnapsack = job.copy();
        GroupTreeAllocator.intermediateAllocation = GroupTreeAllocator.IntermediateAllocation.KNAPSACK;
        timeStartTree = System.nanoTime();
        List<ResourceAvailability> resultTreeKnapsack = GroupTreeAllocator.allocateResources(jobTreeKnapsack, groups, startTime, finishTime);
        Long durationTreeKnapsack = System.nanoTime() - timeStartTree;
        if(resultTreeKnapsack == null) {
            success = false;
            treeStatsKnapsack.addValue("Fails", 1d);
        } else {
            treeStatsKnapsack.addValue("Fails", 0d);
        }

        Job jobBrute = job.copy();
        Long timeStartBrute = System.nanoTime();
        List<ResourceAvailability> resultBrute = BruteForceAllocator.allocateResources(jobBrute, groups, startTime, finishTime);
        Long durationBrute = System.nanoTime() - timeStartBrute;
        if(resultBrute == null) {
            success = false;
            bruteStats.addValue("Fails", 1d);
        } else {
            bruteStats.addValue("Fails", 0d);
        }

        if(success){
            //System.out.println(AbstractGroupAllocator.explainProblem(job, groups, resultTree, startTime, finishTime));
            //System.out.println(AbstractGroupAllocator.explainProblem(job, groups, resultBrute, startTime, finishTime));

            AbstractGroupAllocator.SolutionStats resultTreeGreedyStats = AbstractGroupAllocator.estimateSolution(jobTreeGreedy, resultTreeGreedy, startTime, finishTime);
            treeStatsGreedy.addValue("P", resultTreeGreedyStats.probability);
            treeStatsGreedy.addValue("C", resultTreeGreedyStats.totalCost);
            treeStatsGreedy.addValue("Feasible", resultTreeGreedyStats.isFeasible? 1d : 0d);
            treeStatsGreedy.addValue("T", durationTreeGreedy/1000000d); // nano -> mls

            AbstractGroupAllocator.SolutionStats resultTreeKnapsackStats = AbstractGroupAllocator.estimateSolution(jobTreeKnapsack, resultTreeKnapsack, startTime, finishTime);
            treeStatsKnapsack.addValue("P", resultTreeKnapsackStats.probability);
            treeStatsKnapsack.addValue("C", resultTreeKnapsackStats.totalCost);
            treeStatsKnapsack.addValue("Feasible", resultTreeKnapsackStats.isFeasible? 1d : 0d);
            treeStatsKnapsack.addValue("T", durationTreeKnapsack/1000000d); // nano -> mls

            AbstractGroupAllocator.SolutionStats resultBruteStats = AbstractGroupAllocator.estimateSolution(jobBrute, resultBrute, startTime, finishTime);
            bruteStats.addValue("P", resultBruteStats.probability);
            bruteStats.addValue("C", resultBruteStats.totalCost);
            bruteStats.addValue("Feasible", resultBruteStats.isFeasible? 1d : 0d);
            bruteStats.addValue("T", durationBrute/1000000d); // nano -> mls

            compareStats.addValue("relT Greedy", durationTreeGreedy.doubleValue() / durationBrute.doubleValue());
            compareStats.addValue("relT Knapsack", durationTreeKnapsack.doubleValue() / durationBrute.doubleValue());

            if(resultBruteStats.probability != 0) {
                compareStats.addValue("relP Greedy", (resultBruteStats.probability - resultTreeGreedyStats.probability) / resultBruteStats.probability);
                compareStats.addValue("relP Knapsack", (resultBruteStats.probability - resultTreeKnapsackStats.probability) / resultBruteStats.probability);
            }

            if(resultBruteStats.totalCost != 0) {
                Double relC = (resultTreeGreedyStats.totalCost - resultBruteStats.totalCost) / resultBruteStats.totalCost;
                compareStats.addValue("relC Greedy", relC);
                if(relC < 0) { // smth wrong
                    System.out.println("Greedy is cheaper then Brute by " + relC);
                    System.out.println(AbstractGroupAllocator.explainProblem(job, groups, resultTreeGreedy, startTime, finishTime));
                    System.out.println(AbstractGroupAllocator.explainProblem(job, groups, resultBrute, startTime, finishTime));
                }

                relC = (resultTreeKnapsackStats.totalCost - resultBruteStats.totalCost) / resultBruteStats.totalCost;
                compareStats.addValue("relC Knapsack", relC);
                if(relC < 0) { // smth wrong
                    System.out.println("Knapsack is cheaper then Brute by " + relC);
                    System.out.println(AbstractGroupAllocator.explainProblem(job, groups, resultTreeKnapsack, startTime, finishTime));
                    System.out.println(AbstractGroupAllocator.explainProblem(job, groups, resultBrute, startTime, finishTime));
                }
            }
        }
        
    }

    private List<ResourceAvailabilityGroup> groupifyResourcesUniform(List<ResourceAvailability> resources, int groupsNum) {
        int GROUPS_NUM = groupsNum;
        int groupNum = 1;
        ArrayList<ResourceAvailabilityGroup> groups = new ArrayList<>();

        int i = 0;
        for(Iterator<ResourceAvailability> it = resources.iterator(); it.hasNext();) {
            ResourceAvailability resource = it.next();
            ResourceAvailabilityGroup group;

            if(i < groupNum * resources.size() / GROUPS_NUM) {

                if(groups.size() >= groupNum) {
                    group = groups.get(groupNum-1);
                } else {
                    group = new ResourceAvailabilityGroup(groupNum, resource.getAvailabilityP());
                    groups.add(group);
                }

            } else {
                group = new ResourceAvailabilityGroup(groupNum, resource.getAvailabilityP());
                groups.add(group);
                groupNum++;
            }

            group.getResources().add(resource);
            resource.setGroup(group);
            i++;

        }

        return groups;
    }

    private List<ResourceAvailabilityGroup> groupifyResourcesRandom(List<ResourceAvailability> resources, int groupsNum) {
        List<ResourceAvailabilityGroup> groups = new ArrayList<>();
        for(int i = 0; i < groupsNum; i++){
            groups.add(new ResourceAvailabilityGroup(i, 1d));
        }

        Random random = new Random();
        for(ResourceAvailability resource : resources) {
            int groupNum = random.nextInt(groupsNum);
            ResourceAvailabilityGroup group = groups.get(groupNum);

            group.getResources().add(resource);
            group.setAvailabilityP(resource.getAvailabilityP());
            resource.setGroup(group);
        }

        return groups;
    }

    @Override
    public String printResults() {
        return new StringBuilder()
                        .append(bruteStats.getData())
                        .append(treeStatsGreedy.getData())
                        .append(treeStatsKnapsack.getData())
//                        .append(knapsackStats.getData())
                        .append(compareStats.getData())
                        .append(compareStats.getDetailedData("relP Knapsack"))
                        .toString();
    }

    private ResourceDomain generateResources(int resNumber) {

        ResourceGenerator resGen = new ResourceGenerator();
        resGen.intMIPS = new Interval(1, 8);
        resGen.intRAM = new Interval(1, 8);
        resGen.intPrice = new Interval(1, 12);
        resGen.genPriceMutationIndex = new GaussianFacade(new GaussianSettings(0.7, 1, 1.3));
        resGen.genHardwareMutationIndex = new GaussianFacade(new GaussianSettings(0.6, 1, 1.2));

        return resGen.generateResourceDomain(resNumber);
    }

    private List<ResourceAvailability> generateUtilization(SchedulingController controller, Double load, 
                                                            int startTime, int endTime) {

        List<ResourceAvailability> resources = new ArrayList<>();
        
        int i = 0;
        for(Resource r : controller.getResourceDomain().getResources()){
            resources.add(generateUtilizationSD(i, r, load,startTime, endTime));
            i++;
        }
        
        return resources;
    }
    
    private ResourceAvailability generateUtilizationSD(int orderNum, Resource resource, Double sd, 
                                                            int startTime, int endTime) {
        
        // 1. calculating deviation from absolute availability
        Double load = 0d;
        if(sd > 0){
            Distribution d = new NormalEventDistribution(0d, sd);
            load = Math.abs(d.getSampleValue());
        }
        
        if(load > 1){
            load = 1d;
        }
        
        // 2. adding uniform event to the resource
        Event generalEvent = new Event(
                                        new QuazyUniformDistribution(load),
                                        startTime,
                                        endTime, 
                                        startTime, 
                                        EventType.GENERAL);
        resource.addEvent(generalEvent);
        
        return new ResourceAvailability(orderNum, resource, 1-load);
        
    }


    private List<Job> generateJobFlow(int parallelNum) {

        Integer volume = 0; // shouldn't be used in this experiment

        Integer budget = 10000;

        ResourceRequest request = new ResourceRequest(budget, parallelNum, volume, 1d);
        UserPreferenceModel preferences = new UserPreferenceModel();
        preferences.setCriterion(new AvailableProbabilityCriterion());
        preferences.setDeadline(1200);
        preferences.setMinAvailability(0.1);
        preferences.setCostBudget(budget);

        Job job = new RegularJob(request);
        job.setStartVariability(2d);
        job.setFinishVariability(2d);
        job.setPreferences(preferences);

        return Collections.singletonList(job);
    }


    public SchedulingController getSchedulingController() {
        return schedulingController;
    }
    
    private void createAllocation(Job job, List<ResourceAvailability> resources, int startTime, int finishTime){
        List<Resource> selectedResources = resources.stream()
                .map(r -> r.resource)
                .collect(Collectors.toList());
        
        ResourcesAllocation jobAllocation = new ResourcesAllocation();
        jobAllocation.setResources(selectedResources);
        jobAllocation.setStartTime(startTime);
        jobAllocation.setEndTime(finishTime);
        job.setResourcesAllocation(jobAllocation);
    }

}

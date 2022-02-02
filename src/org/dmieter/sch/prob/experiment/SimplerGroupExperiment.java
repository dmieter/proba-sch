package org.dmieter.sch.prob.experiment;

import org.apache.commons.math3.geometry.euclidean.oned.Interval;
import org.dmieter.sch.prob.SchedulingController;
import org.dmieter.sch.prob.distribution.Distribution;
import org.dmieter.sch.prob.distribution.NormalEventDistribution;
import org.dmieter.sch.prob.distribution.QuazyUniformDistribution;
import org.dmieter.sch.prob.events.Event;
import org.dmieter.sch.prob.events.EventType;
import org.dmieter.sch.prob.experiment.stat.NamedStats;
import org.dmieter.sch.prob.experiment.stat.ResourcesAllocationStats;
import org.dmieter.sch.prob.generator.ResourceGenerator;
import org.dmieter.sch.prob.job.Job;
import org.dmieter.sch.prob.job.RegularJob;
import org.dmieter.sch.prob.resources.Resource;
import org.dmieter.sch.prob.resources.ResourceDomain;
import org.dmieter.sch.prob.resources.ResourcesAllocation;
import org.dmieter.sch.prob.scheduler.allocator.*;
import org.dmieter.sch.prob.scheduler.allocator.tree.GroupTreeAllocator;
import org.dmieter.sch.prob.scheduler.criteria.AvailableProbabilityCriterion;
import org.dmieter.sch.prob.scheduler.criteria.SumCostCriterion;
import org.dmieter.sch.prob.scheduler.criteria.UserPreferenceModel;
import org.dmieter.sch.prob.user.ResourceRequest;
import project.math.distributions.GaussianFacade;
import project.math.distributions.GaussianSettings;
import project.math.utils.MathUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author dmieter
 */
public class SimplerGroupExperiment implements Experiment {
    
    private SchedulingController schedulingController;

    private final ResourcesAllocationStats simpleStats = new ResourcesAllocationStats("SIMPLE");
    private final ResourcesAllocationStats greedyStats = new ResourcesAllocationStats("GREEDY");
    private final ResourcesAllocationStats mincostStats = new ResourcesAllocationStats("MINCOST");
    private final ResourcesAllocationStats knapsackStats = new ResourcesAllocationStats("KNAPSACK");
    private final NamedStats compareStats = new NamedStats("COMPARISON");

    @Override
    public void run(int expNnum) {

        for (int i = 0; i < expNnum; i++) {
            System.out.println(i);
            runSingleExperiment();
        }
    }

    public void runSingleExperiment() {
        ResourceDomain domain = generateResources(20);
        int startTime = 0;
        int finishTime = 1;
        
        
        schedulingController = new SchedulingController(domain);
        List<ResourceAvailability> resources = generateUtilization(schedulingController, 0.1d /* LOAD SD */, startTime, finishTime);
        List<ResourceAvailabilityGroup> groups = groupifyResources(resources);

        System.out.println(GroupTreeAllocator.explainProblem(groups, null, startTime, finishTime));


        Job job = generateJobFlow().get(0);



        boolean success = true;

        List<ResourceAvailability> result = GroupTreeAllocator.allocateResources(job, groups, startTime, finishTime);
        success = success && result != null;

        if(success){
            System.out.println(GroupTreeAllocator.explainProblem(groups, result, startTime, finishTime));

//            createAllocation(job1, allocation1, startTime, finishTime);
//            createAllocation(job2, allocation2, startTime, finishTime);
//            createAllocation(job3, allocation3, startTime, finishTime);
//            createAllocation(job4, allocation4, startTime, finishTime);
//
//            greedyStats.processAllocation(job1, greedyT);
//            knapsackStats.processAllocation(job2, knapsackT);
//            simpleStats.processAllocation(job3, simpleT);
//            mincostStats.processAllocation(job4, mincostT);
//
//            SumCostCriterion costC = new SumCostCriterion();
//            AvailableProbabilityCriterion probC = new AvailableProbabilityCriterion();
//
//            Double greedyC = costC.getValue(job1.getResourcesAllocation());
//            Double knapsackC = costC.getValue(job2.getResourcesAllocation());
//            compareStats.addValue("C", (knapsackC-greedyC)/knapsackC);
//
//            Double greedyP = probC.getValue(job1.getResourcesAllocation());
//            Double knapsackP = probC.getValue(job2.getResourcesAllocation());
//            compareStats.addValue("P", greedyP-knapsackP);
        }
        
    }

    private List<ResourceAvailabilityGroup> groupifyResources(List<ResourceAvailability> resources) {
        int GROUPS_NUM = 3;
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

    @Override
    public String printResults() {
        return new StringBuilder()
                        .append(simpleStats.getData())
                        .append(mincostStats.getData())
                        .append(greedyStats.getData())
                        .append(knapsackStats.getData())
                        .append(compareStats.getData())
                        .append(compareStats.getDetailedData("P"))
                        .append("Greedy wins: " + GreedyMaxPLimitedAllocator.bestWin + " : " + GreedyMaxPLimitedAllocator.greedyWin + " : " + GreedyMaxPLimitedAllocator.mincostWin)
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
    
    @Deprecated
    private ResourceAvailability generateUtilization(int orderNum, Resource resource, Double load, 
                                                            int startTime, int endTime) {
        
        // 1. Calculating if current resource is utilized or not
        Double availability = 1d;  // base availabiity is 1
        if(MathUtils.getUniform(0d, 1d) < load){    // if this resource should be utilized
            availability = 0d;                      // then availability is 0
        }
        
        
        // 2. calculating deviation from absolute availability
        Distribution d = new NormalEventDistribution(0d, 0.1d);
        
        Double deviation = Math.abs(d.getSampleValue());
        if(deviation > 1){
            deviation = 1d;
        }
        
        // 3. applying devition to absolute availability
        if(availability > 0){
            availability -= deviation;
        } else {
            availability += deviation;
        }
        
        // 4. adding uniform event to the resource
        Event generalEvent = new Event(
                                        new QuazyUniformDistribution(1-availability), // this event represent resources allocation = 1 - availability
                                        startTime, 
                                        endTime, 
                                        startTime, 
                                        EventType.GENERAL);
        resource.addEvent(generalEvent);
        
        return new ResourceAvailability(orderNum, resource, availability);
    }

    private List<Job> generateJobFlow() {

        Integer parallelNum = 5;
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
    
    private List<ResourceAvailability> copyResources (List<ResourceAvailability> resources){
        return resources.stream()
                .map(r -> r.copy())
                .collect(Collectors.toList());
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

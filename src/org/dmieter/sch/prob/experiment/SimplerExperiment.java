package org.dmieter.sch.prob.experiment;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
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
import org.dmieter.sch.prob.generator.UtilizationGenerator;
import org.dmieter.sch.prob.job.Job;
import org.dmieter.sch.prob.job.RegularJob;
import org.dmieter.sch.prob.resources.Resource;
import org.dmieter.sch.prob.resources.ResourceDescription;
import org.dmieter.sch.prob.resources.ResourceDomain;
import org.dmieter.sch.prob.resources.ResourcesAllocation;
import org.dmieter.sch.prob.scheduler.AvaScheduler;
import org.dmieter.sch.prob.scheduler.AvaSchedulerSettings;
import org.dmieter.sch.prob.scheduler.Scheduler;
import org.dmieter.sch.prob.scheduler.SchedulerSettings;
import org.dmieter.sch.prob.scheduler.allocator.GreedyMaxPAllocator;
import org.dmieter.sch.prob.scheduler.allocator.GreedyMaxPLimitedAllocator;
import org.dmieter.sch.prob.scheduler.allocator.KnapsackMaxPAllocator;
import org.dmieter.sch.prob.scheduler.allocator.MinCostAllocator;
import org.dmieter.sch.prob.scheduler.allocator.ResourceAvailability;
import org.dmieter.sch.prob.scheduler.criteria.AvailableProbabilityCriterion;
import org.dmieter.sch.prob.scheduler.criteria.SumCostCriterion;
import org.dmieter.sch.prob.scheduler.criteria.UserPreferenceModel;
import org.dmieter.sch.prob.user.ResourceRequest;
import project.math.distributions.GaussianFacade;
import project.math.distributions.GaussianSettings;
import project.math.distributions.UniformFacade;
import project.math.utils.MathUtils;

/**
 *
 * @author dmieter
 */
public class SimplerExperiment implements Experiment {
    
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
        ResourceDomain domain = generateResources(80);
        int startTime = 0;
        int finishTime = 200;
        
        
        schedulingController = new SchedulingController(domain);
        List<ResourceAvailability> resources = generateUtilization(schedulingController, 0.5d, startTime, finishTime);

        Job job = generateJobFlow().get(0);
        
        boolean success = true;
        long greedyT, knapsackT, simpleT, mincostT;
        
        /* GREEDY LIMITED SCHEDULING */
        System.out.println("GREEDY LIMITED");
        List<ResourceAvailability> resources1 = copyResources(resources);
        Job job1 = job.copy();
                
        greedyT = System.nanoTime();
        List<ResourceAvailability> allocation1 = 
                GreedyMaxPLimitedAllocator.allocateResources(job1, resources1, startTime, finishTime);
        greedyT = System.nanoTime() - greedyT;
        
        if(allocation1 == null){
            greedyStats.logFailedExperiment();
            success = false;
        }
        
        /* KNAPSACK SCHEDULING */
        System.out.println("KNAPSACK");
        List<ResourceAvailability> resources2 = copyResources(resources);
        Job job2 = job.copy();
                
        knapsackT = System.nanoTime();
        List<ResourceAvailability> allocation2 = 
                KnapsackMaxPAllocator.allocateResources(job2, resources2, startTime, finishTime);
        knapsackT = System.nanoTime() - knapsackT;
        
        if(allocation2 == null){
            knapsackStats.logFailedExperiment();
            success = false;
        }
        
        /* SIMPLE SCHEDULING */
        System.out.println("SIMPLE");
        List<ResourceAvailability> resources3 = copyResources(resources);
        Job job3 = job.copy();
                
        simpleT = System.nanoTime();
        List<ResourceAvailability> allocation3 = 
                GreedyMaxPAllocator.allocateResources(job3, resources3, startTime, finishTime);
        simpleT = System.nanoTime() - simpleT;
        
        if(allocation3 == null){
            simpleStats.logFailedExperiment();
            success = false;
        }
        
        /* MINCOST SCHEDULING */
        System.out.println("MIN COST");
        List<ResourceAvailability> resources4 = copyResources(resources);
        Job job4 = job.copy();
                
        mincostT = System.nanoTime();
        List<ResourceAvailability> allocation4 = 
                MinCostAllocator.allocateResources(job4, resources4, startTime, finishTime);
        mincostT = System.nanoTime() - mincostT;
        
        if(allocation4 == null){
            mincostStats.logFailedExperiment();
            success = false;
        }
        
        if(success){
            createAllocation(job1, allocation1, startTime, finishTime);
            createAllocation(job2, allocation2, startTime, finishTime);
            createAllocation(job3, allocation3, startTime, finishTime);
            createAllocation(job4, allocation4, startTime, finishTime);
            
            greedyStats.processAllocation(job1, greedyT);
            knapsackStats.processAllocation(job2, knapsackT);
            simpleStats.processAllocation(job3, simpleT);
            mincostStats.processAllocation(job4, mincostT);
            
            SumCostCriterion costC = new SumCostCriterion();
            AvailableProbabilityCriterion probC = new AvailableProbabilityCriterion();
            
            Double greedyC = costC.getValue(job1.getResourcesAllocation());
            Double knapsackC = costC.getValue(job2.getResourcesAllocation());
            compareStats.addValue("C", (knapsackC-greedyC)/knapsackC);
            
            Double greedyP = probC.getValue(job1.getResourcesAllocation());
            Double knapsackP = probC.getValue(job2.getResourcesAllocation());
            compareStats.addValue("P", greedyP-knapsackP);
        }
        
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
            resources.add(generateUtilization(i, r, load,startTime, endTime));
            i++;
        }
        
        return resources;
    }
    
    private ResourceAvailability generateUtilization(int orderNum, Resource resource, Double load, 
                                                            int startTime, int endTime) {
        
        // 1. Calculating if current resource is utilized or not
        Double availability = 1d;  // base availabiity is 1
        if(MathUtils.getUniform(0d, 1d) < load){    // if this resource should be utilized
            availability = 0d;                      // then availability is 0
        }
        
        
        // 2. calculating deviation from absolute availability
        Distribution d = new NormalEventDistribution(0d, 0.2d);
        
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

        Integer parallelNum = 8;
        Integer volume = 0; // shouldn't be used in this experiment

        Integer budget = 5000;

        ResourceRequest request = new ResourceRequest(budget, parallelNum, volume, 1d);
        UserPreferenceModel preferences = new UserPreferenceModel();
        preferences.setCriterion(new AvailableProbabilityCriterion());
        preferences.setDeadline(1200);
        preferences.setMinAvailability(0.1);
        preferences.setCostBudget(100);

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

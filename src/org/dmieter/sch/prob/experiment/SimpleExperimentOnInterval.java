package org.dmieter.sch.prob.experiment;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
import org.dmieter.sch.prob.scheduler.AvaScheduler;
import org.dmieter.sch.prob.scheduler.AvaSchedulerOpt;
import org.dmieter.sch.prob.scheduler.AvaSchedulerSettings;
import org.dmieter.sch.prob.scheduler.Scheduler;
import org.dmieter.sch.prob.scheduler.SchedulerSettings;
import org.dmieter.sch.prob.scheduler.allocator.GreedyMaxPLimitedAllocator;
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
public class SimpleExperimentOnInterval implements Experiment {

    private Scheduler scheduler;
    private AvaSchedulerSettings settings;

    private SchedulingController schedulingController;

    private final ResourcesAllocationStats greedyStats = new ResourcesAllocationStats("GREEDY SCAN");
    private final ResourcesAllocationStats knapStats = new ResourcesAllocationStats("KNAPSACK SCAN");
    private final NamedStats compareStats = new NamedStats("COMPARISON");

    @Override
    public void run(int expNnum) {
        initScheduler();

        for (int i = 0; i < expNnum; i++) {
            System.out.println(i);
            runSingleExperiment();
        }
    }

    public void runSingleExperiment() {
        ResourceDomain domain = generateResources(64);

        schedulingController = new SchedulingController(domain);
        generateUtilizationForExperiment(schedulingController);

        Job job = generateJobFlow().get(0);

        Integer startTime = 0;

        boolean success = true;
        long greedyT, knapT;

        /* GREEDY SCHEDULING */
        System.out.println("GREEDY");
        ResourceDomain domainG = domain.copy();
        Job jobG = job.copy();
        scheduler.flush();
        settings.setSchedulingMode(AvaSchedulerSettings.SchMode.GREEDY_LIMITED);

        greedyT = System.nanoTime();
        scheduler.schedule(jobG, domainG, startTime, settings);
        greedyT = System.nanoTime() - greedyT;

        if (jobG.getResourcesAllocation() == null) {
            greedyStats.logFailedExperiment();
            success = false;
        }

        /* KNAPSACK SCHEDULING*/
        System.out.println("KNAPSACK");
        ResourceDomain domainK = domain.copy();
        Job jobK = job.copy();
        scheduler.flush();
        settings.setSchedulingMode(AvaSchedulerSettings.SchMode.KNAPSACK);

        knapT = System.nanoTime();
        //schedulerOpt.schedule(job1, domain1, startTime, settings);
        scheduler.schedule(jobK, domainK, startTime, settings);
        knapT = System.nanoTime() - knapT;
        
        if (jobK.getResourcesAllocation() == null) {
            knapStats.logFailedExperiment();
            success = false;
        }
        
        if (success) {
            greedyStats.processAllocation(jobG, greedyT);
            knapStats.processAllocation(jobK, knapT);
                        
            SumCostCriterion costC = new SumCostCriterion();
            AvailableProbabilityCriterion probC = new AvailableProbabilityCriterion();

            Double greedyC = costC.getValue(jobG.getResourcesAllocation());
            Double knapsackC = costC.getValue(jobK.getResourcesAllocation());
            compareStats.addValue("C", (knapsackC - greedyC) / knapsackC);

            AvailableProbabilityCriterion probCriteria = new AvailableProbabilityCriterion();
            Double greedyP = probCriteria.getValue(jobG.getResourcesAllocation());
            Double knapP = probCriteria.getValue(jobK.getResourcesAllocation());
            compareStats.addValue("P", Math.abs(-greedyP + knapP));
            compareStats.addValue("Start Time", Math.abs(jobG.getResourcesAllocation().getStartTime() - jobK.getResourcesAllocation().getStartTime())+0.0);
            compareStats.addValue("Finish Time", Math.abs(jobG.getResourcesAllocation().getEndTime() - jobK.getResourcesAllocation().getEndTime())+0.0);
            compareStats.addValue("Working Time", Math.abs(knapT - greedyT)/1000000000+0.0);
        }

        if (jobK.getResourcesAllocation() != null) {
            jobK.getResourcesAllocation().getStartEvent().setEventColor(Color.red);
            if (jobK.getResourcesAllocation().getExecutionEvent() != null) {
                jobK.getResourcesAllocation().getExecutionEvent().setEventColor(Color.green);
            }
            jobK.getResourcesAllocation().getFinishEvent().setEventColor(Color.red);
            schedulingController.scheduleJob(jobK);
        }
    }

    @Override
    public String printResults() {
        return new StringBuilder()
               
                .append(AvaScheduler.schedulingTimeline.getDetailedData(AvaSchedulerSettings.SchMode.GREEDY_LIMITED.name()))
                .append(greedyStats.getData())
                .append("============================================")
                .append(knapStats.getData())
                .append("============================================")
                .append(compareStats.getData())
                .toString();
    }

    private ResourceDomain generateResources(int resNumber) {

        ResourceGenerator resGen = new ResourceGenerator();
        resGen.intMIPS = new Interval(1, 10);
        resGen.intRAM = new Interval(1, 8);
        resGen.intPrice = new Interval(1, 20);
        resGen.genPriceMutationIndex = new GaussianFacade(new GaussianSettings(0.7, 1, 1.3));
        resGen.genHardwareMutationIndex = new GaussianFacade(new GaussianSettings(0.6, 1, 1.2));

        return resGen.generateResourceDomain(resNumber);
    }

    private void generateUtilizationForExperiment(SchedulingController controller) {
        generateUtilizationJobs(schedulingController);
        generateUtilizationGlobal(schedulingController, 0.05d /* LOAD SD */, -1000, 2500);
    }
    
    private void generateUtilizationJobs(SchedulingController controller) {

        UtilizationGenerator uGen = new UtilizationGenerator();
        uGen.intFinishVariability = new Interval(10, 40);
        uGen.intStartVariability = new Interval(20, 50);
        uGen.intJobLength = new Interval(200, 700);
        uGen.intLoad = new Interval(0.5, 0.5);
        uGen.generateUtilization(controller, new Interval(-1000, 2500));

    }
    
    private void generateUtilizationGlobal(SchedulingController controller, Double load, 
                                                            int startTime, int endTime) {

        int i = 0;
        for(Resource r : controller.getResourceDomain().getResources()){
            generateUtilizationSD(i, r, load,startTime, endTime);
            i++;
        }
        

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
        generalEvent.setEventColor(Color.MAGENTA);
        resource.addEvent(generalEvent);
        
        return new ResourceAvailability(orderNum, resource, 1-load);
        
    }

    private List<Job> generateJobFlow() {

        Integer parallelNum = 6;
        Double averageMips = 9d;
        Double averagePrice = 14d;
        Integer volume = 600;

        Integer budget = MathUtils.intNextUp(parallelNum * volume * averagePrice / averageMips);
        budget = 8500;
        System.out.println("Budget: " + budget);

        ResourceRequest request = new ResourceRequest(budget, parallelNum, volume, 1d);
        UserPreferenceModel preferences = new UserPreferenceModel();
        preferences.setCriterion(new AvailableProbabilityCriterion());
        preferences.setDeadline(800);
        preferences.setMinAvailability(0.2);
        //preferences.setCostBudget(100);

        Job job = new RegularJob(request);
        job.setStartVariability(2d);
        job.setFinishVariability(2d);
        job.setPreferences(preferences);

        return Collections.singletonList(job);
    }

    private void initScheduler() {
        scheduler = new AvaScheduler();
        settings = new AvaSchedulerSettings();
        settings.setScanDelta(1);
        settings.setOptimizationProblem(AvaSchedulerSettings.OptProblem.MAX_PROBABILITY);
        settings.setSchedulingMode(AvaSchedulerSettings.SchMode.GREEDY_LIMITED);
    }

    public SchedulingController getSchedulingController() {
        return schedulingController;
    }

}

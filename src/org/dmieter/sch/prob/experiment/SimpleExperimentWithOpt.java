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
public class SimpleExperimentWithOpt implements Experiment {

    private Scheduler scheduler;
    private Scheduler schedulerOpt;
    private AvaSchedulerSettings settings;

    private SchedulingController schedulingController;

    private final ResourcesAllocationStats fullScanStats = new ResourcesAllocationStats("FULL SCAN");
    private final ResourcesAllocationStats optStats = new ResourcesAllocationStats("OPT");
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
        ResourceDomain domain = generateResources(30);

        schedulingController = new SchedulingController(domain);
        generateUtilizationForExperiment(schedulingController);

        Job job = generateJobFlow().get(0);

        Integer startTime = 0;

        boolean success = true;
        long fullT, optT;

        /* FULLSCAN SCHEDULING */
        System.out.println("FULLSCAN");
        ResourceDomain domain1 = domain.copy();
        Job job1 = job.copy();
        scheduler.flush();
        settings.setSchedulingMode(AvaSchedulerSettings.SchMode.GREEDY_LIMITED);

        fullT = System.nanoTime();
        scheduler.schedule(job1, domain1, startTime, settings);
        fullT = System.nanoTime() - fullT;

        if (job1.getResourcesAllocation() == null) {
            fullScanStats.logFailedExperiment();
            success = false;
        }

        /* OPT SCHEDULING */
        System.out.println("OPT");
        ResourceDomain domain2 = domain.copy();
        Job job2 = job.copy();
        schedulerOpt.flush();
        //scheduler.flush();
        settings.setSchedulingMode(AvaSchedulerSettings.SchMode.GREEDY_LIMITED);

        optT = System.nanoTime();
        schedulerOpt.schedule(job2, domain2, startTime, settings);
        //scheduler.schedule(job2, domain2, startTime, settings);
        optT = System.nanoTime() - optT;

        if (job2.getResourcesAllocation() == null) {
            optStats.logFailedExperiment();
            success = false;
        }


        if (success) {
            fullScanStats.processAllocation(job1, fullT);
            optStats.processAllocation(job2, optT);
            
//            SumCostCriterion costC = new SumCostCriterion();
//            AvailableProbabilityCriterion probC = new AvailableProbabilityCriterion();
//
//            Double greedyC = costC.getValue(job1.getResourcesAllocation());
//            Double knapsackC = costC.getValue(job2.getResourcesAllocation());
//            compareStats.addValue("C", (knapsackC - greedyC) / knapsackC);

            AvailableProbabilityCriterion probC = new AvailableProbabilityCriterion();
            Double fullP = probC.getValue(job1.getResourcesAllocation());
            Double optP = probC.getValue(job2.getResourcesAllocation());
            compareStats.addValue("P", Math.abs(fullP - optP));
            compareStats.addValue("Start Time", Math.abs(job1.getResourcesAllocation().getStartTime() - job2.getResourcesAllocation().getStartTime())+0.0);
            compareStats.addValue("Finish Time", Math.abs(job1.getResourcesAllocation().getEndTime() - job2.getResourcesAllocation().getEndTime())+0.0);
            compareStats.addValue("Working Time", Math.abs(fullT - optT)/1000000000+0.0);
        }

        if (job2.getResourcesAllocation() != null) {
            job2.getResourcesAllocation().getStartEvent().setEventColor(Color.red);
            if (job2.getResourcesAllocation().getExecutionEvent() != null) {
                job2.getResourcesAllocation().getExecutionEvent().setEventColor(Color.green);
            }
            job2.getResourcesAllocation().getFinishEvent().setEventColor(Color.red);
            schedulingController.scheduleJob(job2);
        }
    }

    @Override
    public String printResults() {
        return new StringBuilder()
               
                .append(AvaScheduler.schedulingTimeline.getDetailedData(AvaSchedulerSettings.SchMode.GREEDY_LIMITED.name()))
                .append(fullScanStats.getData())
                .append(optStats.getData())
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
        generateUtilizationGlobal(schedulingController, 0.05d /* LOAD SD */, -1000, 1000);
    }
    
    private void generateUtilizationJobs(SchedulingController controller) {

        UtilizationGenerator uGen = new UtilizationGenerator();
        uGen.intFinishVariability = new Interval(10, 40);
        uGen.intStartVariability = new Interval(20, 50);
        uGen.intJobLength = new Interval(200, 700);
        uGen.intLoad = new Interval(0.4, 0.8);
        uGen.generateUtilization(controller, new Interval(-1000, 2500));

        //uGen.intResourceEventMean = new Interval(5000, 50000);
        //uGen.generateGlobalEvents(controller, new Interval(0, 5000), Color.PINK); // failure
        //uGen.intResourceEventMean = new Interval(0, 3000);
        //uGen.generateGlobalEvents(controller, new Interval(0, 2000), Color.CYAN); // maintenance

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

        Integer parallelNum = 4;
        Double averageMips = 9d;
        Double averagePrice = 14d;
        Integer volume = 400;

        Integer budget = MathUtils.intNextUp(parallelNum * volume * averagePrice / averageMips);
        budget = 4500;
        System.out.println("Budget: " + budget);

        ResourceRequest request = new ResourceRequest(budget, parallelNum, volume, 1d);
        UserPreferenceModel preferences = new UserPreferenceModel();
        preferences.setCriterion(new AvailableProbabilityCriterion());
        preferences.setDeadline(800);
        preferences.setMinAvailability(0.1);
        preferences.setCostBudget(100);

        Job job = new RegularJob(request);
        job.setStartVariability(2d);
        job.setFinishVariability(2d);
        job.setPreferences(preferences);

        return Collections.singletonList(job);
    }

    private void initScheduler() {
        scheduler = new AvaScheduler();
        schedulerOpt = new AvaSchedulerOpt();
        settings = new AvaSchedulerSettings();
        settings.setScanDelta(1);
        settings.setOptimizationProblem(AvaSchedulerSettings.OptProblem.MAX_PROBABILITY);
        settings.setSchedulingMode(AvaSchedulerSettings.SchMode.GREEDY_LIMITED);
    }

    public SchedulingController getSchedulingController() {
        return schedulingController;
    }

}

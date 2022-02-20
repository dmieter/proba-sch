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
    private AvaSchedulerOpt schedulerOpt;
    private AvaSchedulerSettings settings;

    private SchedulingController schedulingController;

    private final ResourcesAllocationStats fullScanStats = new ResourcesAllocationStats("FULL SCAN");
    private final ResourcesAllocationStats optStats1 = new ResourcesAllocationStats("OPT 1");
    private final ResourcesAllocationStats optStats10 = new ResourcesAllocationStats("OPT 5");
    private final ResourcesAllocationStats optStats30 = new ResourcesAllocationStats("OPT 10");
    private final ResourcesAllocationStats optStats60 = new ResourcesAllocationStats("OPT 20");
    private final ResourcesAllocationStats optStats100 = new ResourcesAllocationStats("OPT 50");
    private final ResourcesAllocationStats optStats200 = new ResourcesAllocationStats("OPT 100");
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
        long fullT, optT1, optT10, optT30, optT60, optT100, optT200;

        /* FULLSCAN SCHEDULING */
        System.out.println("FULLSCAN");
        ResourceDomain domainf = domain.copy();
        Job jobf = job.copy();
        scheduler.flush();
        settings.setSchedulingMode(AvaSchedulerSettings.SchMode.GREEDY_LIMITED);

        fullT = System.nanoTime();
        scheduler.schedule(jobf, domainf, startTime, settings);
        fullT = System.nanoTime() - fullT;

        if (jobf.getResourcesAllocation() == null) {
            fullScanStats.logFailedExperiment();
            success = false;
        }

        /* OPT SCHEDULING 1*/
        System.out.println("OPT 1");
        ResourceDomain domain1 = domain.copy();
        Job job1 = job.copy();
        //schedulerOpt.flush();
        scheduler.flush();
        schedulerOpt.startPoints = 1;
        settings.setSchedulingMode(AvaSchedulerSettings.SchMode.KNAPSACK);

        optT1 = System.nanoTime();
        //schedulerOpt.schedule(job1, domain1, startTime, settings);
        scheduler.schedule(job1, domain1, startTime, settings);
        optT1 = System.nanoTime() - optT1;
        
        if (job1.getResourcesAllocation() == null) {
            optStats1.logFailedExperiment();
            success = false;
        }
        
        /* OPT SCHEDULING 10*/
        System.out.println("OPT 5");
        ResourceDomain domain10 = domain.copy();
        Job job10 = job.copy();
        schedulerOpt.flush();
        schedulerOpt.startPoints = 5;
        settings.setSchedulingMode(AvaSchedulerSettings.SchMode.GREEDY_LIMITED);

        optT10 = System.nanoTime();
        schedulerOpt.schedule(job10, domain10, startTime, settings);
        optT10 = System.nanoTime() - optT10;
        
        if (job10.getResourcesAllocation() == null) {
            optStats10.logFailedExperiment();
            success = false;
        }
        
        /* OPT SCHEDULING 30*/
        System.out.println("OPT 10");
        ResourceDomain domain30 = domain.copy();
        Job job30 = job.copy();
        schedulerOpt.flush();
        schedulerOpt.startPoints = 10;
        settings.setSchedulingMode(AvaSchedulerSettings.SchMode.GREEDY_LIMITED);

        optT30 = System.nanoTime();
        schedulerOpt.schedule(job30, domain30, startTime, settings);
        optT30 = System.nanoTime() - optT30;
        
        if (job30.getResourcesAllocation() == null) {
            optStats30.logFailedExperiment();
            success = false;
        }
        
        /* OPT SCHEDULING 60*/
        System.out.println("OPT 20");
        ResourceDomain domain60 = domain.copy();
        Job job60 = job.copy();
        schedulerOpt.flush();
        schedulerOpt.startPoints = 20;
        settings.setSchedulingMode(AvaSchedulerSettings.SchMode.GREEDY_LIMITED);

        optT60 = System.nanoTime();
        schedulerOpt.schedule(job60, domain60, startTime, settings);
        optT60 = System.nanoTime() - optT60;
        
        if (job60.getResourcesAllocation() == null) {
            optStats60.logFailedExperiment();
            success = false;
        }
        
        /* OPT SCHEDULING 100*/
        System.out.println("OPT 50");
        ResourceDomain domain100 = domain.copy();
        Job job100 = job.copy();
        schedulerOpt.flush();
        schedulerOpt.startPoints = 50;
        settings.setSchedulingMode(AvaSchedulerSettings.SchMode.GREEDY_LIMITED);

        optT100 = System.nanoTime();
        schedulerOpt.schedule(job100, domain100, startTime, settings);
        optT100 = System.nanoTime() - optT100;
        
        if (job100.getResourcesAllocation() == null) {
            optStats100.logFailedExperiment();
            success = false;
        }
        
        /* OPT SCHEDULING 200*/
        System.out.println("OPT 100");
        ResourceDomain domain200 = domain.copy();
        Job job200 = job.copy();
        schedulerOpt.flush();
        schedulerOpt.startPoints = 100;
        settings.setSchedulingMode(AvaSchedulerSettings.SchMode.GREEDY_LIMITED);

        optT200 = System.nanoTime();
        schedulerOpt.schedule(job200, domain200, startTime, settings);
        optT200 = System.nanoTime() - optT200;
        
        if (job200.getResourcesAllocation() == null) {
            optStats200.logFailedExperiment();
            success = false;
        }


        if (success) {
            fullScanStats.processAllocation(jobf, fullT);
            optStats1.processAllocation(job1, optT1);
            optStats10.processAllocation(job10, optT10);
            optStats30.processAllocation(job30, optT30);
            optStats60.processAllocation(job60, optT60);
            optStats100.processAllocation(job100, optT100);
            optStats200.processAllocation(job200, optT200);
            
//            SumCostCriterion costC = new SumCostCriterion();
//            AvailableProbabilityCriterion probC = new AvailableProbabilityCriterion();
//
//            Double greedyC = costC.getValue(job1.getResourcesAllocation());
//            Double knapsackC = costC.getValue(job2.getResourcesAllocation());
//            compareStats.addValue("C", (knapsackC - greedyC) / knapsackC);

            //AvailableProbabilityCriterion probC = new AvailableProbabilityCriterion();
            //Double fullP = probC.getValue(jobf.getResourcesAllocation());
            //Double optP = probC.getValue(job2.getResourcesAllocation());
            //compareStats.addValue("P", Math.abs(fullP - optP));
            //compareStats.addValue("Start Time", Math.abs(job1.getResourcesAllocation().getStartTime() - job2.getResourcesAllocation().getStartTime())+0.0);
            //compareStats.addValue("Finish Time", Math.abs(job1.getResourcesAllocation().getEndTime() - job2.getResourcesAllocation().getEndTime())+0.0);
            //compareStats.addValue("Working Time", Math.abs(fullT - optT)/1000000000+0.0);
        }

        if (job30.getResourcesAllocation() != null) {
            job30.getResourcesAllocation().getStartEvent().setEventColor(Color.red);
            if (job30.getResourcesAllocation().getExecutionEvent() != null) {
                job30.getResourcesAllocation().getExecutionEvent().setEventColor(Color.green);
            }
            job30.getResourcesAllocation().getFinishEvent().setEventColor(Color.red);
            schedulingController.scheduleJob(job30);
        }
    }

    @Override
    public String printResults() {
        return new StringBuilder()
               
                .append(AvaScheduler.schedulingTimeline.getDetailedData(AvaSchedulerSettings.SchMode.GREEDY_LIMITED.name()))
                .append(fullScanStats.getData())
                .append("============================================")
                .append(optStats1.getData())
                .append("============================================")
                .append(optStats10.getData())
                .append("============================================")
                .append(optStats30.getData())
                .append("============================================")
                .append(optStats60.getData())
                .append("============================================")
                .append(optStats100.getData())
                .append("============================================")
                .append(optStats200.getData())
//                .append(compareStats.getData())
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

        Integer parallelNum = 6;
        Double averageMips = 9d;
        Double averagePrice = 14d;
        Integer volume = 600;

        Integer budget = MathUtils.intNextUp(parallelNum * volume * averagePrice / averageMips);
        budget = 9000;
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

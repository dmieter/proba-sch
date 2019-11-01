package org.dmieter.sch.prob.experiment;

import java.awt.Color;
import java.util.Collections;
import java.util.List;
import org.apache.commons.math3.geometry.euclidean.oned.Interval;
import org.dmieter.sch.prob.SchedulingController;
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
import org.dmieter.sch.prob.scheduler.AvaSchedulerSettings;
import org.dmieter.sch.prob.scheduler.Scheduler;
import org.dmieter.sch.prob.scheduler.SchedulerSettings;
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
public class SimpleExperiment implements Experiment {

    private Scheduler scheduler;
    private AvaSchedulerSettings settings;

    private SchedulingController schedulingController;
    
    private final ResourcesAllocationStats simpleStats = new ResourcesAllocationStats("SIMPLE");
    private final ResourcesAllocationStats greedyStats = new ResourcesAllocationStats("GREEDY");
    private final ResourcesAllocationStats knapsackStats = new ResourcesAllocationStats("KNAPSACK");
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
        ResourceDomain domain = generateResources(50);
        
        schedulingController = new SchedulingController(domain);
        generateUtilization(schedulingController);

        Job job = generateJobFlow().get(0);

        Integer startTime = 0;
                
        boolean success = true;
        long greedyT, knapsackT, simpleT;
        
        /* GREEDY SCHEDULING */
        ResourceDomain domain1 = domain.copy();
        Job job1 = job.copy();
        scheduler.flush();
        settings.setSchedulingMode(AvaSchedulerSettings.SchMode.GREEDY_LIMITED);
        
        greedyT = System.nanoTime();
        scheduler.schedule(job1, domain1, startTime, settings);
        greedyT = System.nanoTime() - greedyT;
        
        if(job1.getResourcesAllocation() == null){
            greedyStats.logFailedExperiment();
            success = false;
        }
        
        /* KNAPSACK SCHEDULING */
        ResourceDomain domain2 = domain.copy();
        Job job2 = job.copy();
        scheduler.flush();
        settings.setSchedulingMode(AvaSchedulerSettings.SchMode.KNAPSACK);
        
        knapsackT = System.nanoTime();
        scheduler.schedule(job2, domain2, startTime, settings);
        knapsackT = System.nanoTime() - knapsackT;
        
        if(job2.getResourcesAllocation() == null){
            knapsackStats.logFailedExperiment();
            success = false;
        }
        
        /* SIMPLE SCHEDULING */
        ResourceDomain domain3 = domain.copy();
        Job job3 = job.copy();
        scheduler.flush();
        settings.setSchedulingMode(AvaSchedulerSettings.SchMode.GREEDY_SIMPLE);
        
        simpleT = System.nanoTime();
        scheduler.schedule(job3, domain3, startTime, settings);
        simpleT = System.nanoTime() - simpleT;
        
        if(job3.getResourcesAllocation() == null){
            simpleStats.logFailedExperiment();
            success = false;
        }
        
        if(success){
            greedyStats.processAllocation(job1, greedyT);
            knapsackStats.processAllocation(job2, knapsackT);
            simpleStats.processAllocation(job3, simpleT);
            
            SumCostCriterion costC = new SumCostCriterion();
            AvailableProbabilityCriterion probC = new AvailableProbabilityCriterion();
            
            Double greedyC = costC.getValue(job1.getResourcesAllocation());
            Double knapsackC = costC.getValue(job2.getResourcesAllocation());
            compareStats.addValue("C", (knapsackC-greedyC)/knapsackC);
            
            Double greedyP = probC.getValue(job1.getResourcesAllocation());
            Double knapsackP = probC.getValue(job2.getResourcesAllocation());
            compareStats.addValue("P", greedyP-knapsackP);
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
                        .append(AvaScheduler.schedulingTimeline.getDetailedData(AvaSchedulerSettings.SchMode.GREEDY_SIMPLE.name()))
                        .append(AvaScheduler.schedulingTimeline.getDetailedData(AvaSchedulerSettings.SchMode.GREEDY_LIMITED.name()))
                        .append(AvaScheduler.schedulingTimeline.getDetailedData(AvaSchedulerSettings.SchMode.KNAPSACK.name()))
                        .append(simpleStats.getData())
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

    private void generateUtilization(SchedulingController controller) {

        UtilizationGenerator uGen = new UtilizationGenerator();
        uGen.intFinishVariability = new Interval(10, 50);
        uGen.intStartVariability = new Interval(1, 10);
        uGen.intJobLength = new Interval(50, 300);
        uGen.intLoad = new Interval(0.3, 0.8);
        uGen.generateUtilization(controller, new Interval(-300, 2500));
        
        //uGen.intResourceEventMean = new Interval(5000, 50000);
        //uGen.generateGlobalEvents(controller, new Interval(0, 5000), Color.PINK); // failure
        
        uGen.intResourceEventMean = new Interval(1000, 20000);
        uGen.generateGlobalEvents(controller, new Interval(0, 5000), Color.CYAN); // maintenance

    }

    private List<Job> generateJobFlow() {

        Integer parallelNum = 8;
        Double averageMips = 4.5d;
        Double averagePrice = 8d;
        Integer volume = 600;

        Integer budget = MathUtils.intNextUp(parallelNum * volume * averagePrice / averageMips);

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

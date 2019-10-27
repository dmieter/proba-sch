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
    
    private ResourcesAllocationStats greedyStats = new ResourcesAllocationStats("GREEDY");
    private ResourcesAllocationStats knapsackStats = new ResourcesAllocationStats("KNAPSACK");
    private NamedStats compareStats = new NamedStats("COMPARISON");

    @Override
    public void run(int expNnum) {
        initScheduler();

        for (int i = 0; i < expNnum; i++) {
            System.out.println(i);
            runSingleExperiment();
        }
    }

    public void runSingleExperiment() {
        ResourceDomain domain = generateResources(20);
        
        schedulingController = new SchedulingController(domain);
        generateUtilization(schedulingController);

        Job job = generateJobFlow().get(0);

        Integer startTime = 300;
        
        
        boolean success = true;
        long greedyT, knapsackT;
        
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
        settings.setSchedulingMode(AvaSchedulerSettings.SchMode.GREEDY_SIMPLE);
        
        knapsackT = System.nanoTime();
        scheduler.schedule(job2, domain2, startTime, settings);
        knapsackT = System.nanoTime() - knapsackT;
        
        if(job2.getResourcesAllocation() == null){
            knapsackStats.logFailedExperiment();
            success = false;
        }
        
        if(success){
            greedyStats.processAllocation(job1, greedyT);
            knapsackStats.processAllocation(job2, knapsackT);
            
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
        return new StringBuilder(greedyStats.getData())
                        .append(knapsackStats.getData())
                        .append(compareStats.getData())
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
        uGen.intFinishVariability = new Interval(10, 80);
        uGen.intStartVariability = new Interval(1, 20);
        uGen.intJobLength = new Interval(50, 200);
        uGen.intLoad = new Interval(0.1, 0.3);
        uGen.generateUtilization(controller, new Interval(0, 1200));

    }

    private List<Job> generateJobFlow() {

        Integer parallelNum = 7;
        Double averageMips = 4.5d;
        Double averagePrice = 7d;
        Integer volume = 600;

        Integer budget = MathUtils.intNextUp(parallelNum * volume * averagePrice / averageMips);

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
        settings = new AvaSchedulerSettings();
        settings.setScanDelta(1);
        settings.setOptimizationProblem(AvaSchedulerSettings.OptProblem.MAX_PROBABILITY);
        settings.setSchedulingMode(AvaSchedulerSettings.SchMode.GREEDY_LIMITED);
    }

    public SchedulingController getSchedulingController() {
        return schedulingController;
    }

}

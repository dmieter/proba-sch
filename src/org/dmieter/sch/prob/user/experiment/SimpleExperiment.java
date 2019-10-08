package org.dmieter.sch.prob.user.experiment;

import java.util.Collections;
import java.util.List;
import org.apache.commons.math3.geometry.euclidean.oned.Interval;
import org.dmieter.sch.prob.SchedulingController;
import org.dmieter.sch.prob.generator.ResourceGenerator;
import org.dmieter.sch.prob.generator.UtilizationGenerator;
import org.dmieter.sch.prob.job.Job;
import org.dmieter.sch.prob.job.RegularJob;
import org.dmieter.sch.prob.resources.Resource;
import org.dmieter.sch.prob.resources.ResourceDescription;
import org.dmieter.sch.prob.resources.ResourceDomain;
import org.dmieter.sch.prob.scheduler.AvaScheduler;
import org.dmieter.sch.prob.scheduler.Scheduler;
import org.dmieter.sch.prob.scheduler.SchedulerSettings;
import org.dmieter.sch.prob.user.ResourceRequest;
import project.math.distributions.GaussianFacade;
import project.math.distributions.GaussianSettings;
import project.math.distributions.UniformFacade;

/**
 *
 * @author dmieter
 */
public class SimpleExperiment implements Experiment {

    private Scheduler scheduler;
    private SchedulerSettings settings;
    
    private SchedulingController schedulingController;
    
    @Override
    public void run(int expNnum) {
        initScheduler();
        
        for(int i = 0; i < expNnum; i++){
            System.out.println(i);
            runSingleExperiment();
        }
    }
    
    public void runSingleExperiment(){
        ResourceDomain domain = generateResources(20);
        schedulingController = new SchedulingController(domain);
        generateUtilization(schedulingController);
        
        Job job = generateJobFlow().get(0);
        
        scheduler.flush();
        //scheduler.schedule(job, domain, 0, settings);
    }

    @Override
    public String printResults() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
    
    private void generateUtilization(SchedulingController controller){
        
        UtilizationGenerator uGen = new UtilizationGenerator();
        uGen.intFinishVariability = new Interval(5, 40);
        uGen.intStartVariability = new Interval(2, 10);
        uGen.intJobLength = new Interval(50, 200);
        uGen.intLoad = new Interval(0.3, 0.5);
        uGen.generateUtilization(controller, new Interval(0, 1000));
        
    }

    private List<Job> generateJobFlow() {
        ResourceRequest request = new ResourceRequest(100, 2, 1000, 1);
        Job job = new RegularJob(request);
        
        return Collections.singletonList(job);
    }

    private void initScheduler() {
        scheduler = new AvaScheduler();
        settings = new SchedulerSettings();
    }

    public SchedulingController getSchedulingController(){
        return schedulingController;
    }
    
}

package org.dmieter.sch.prob.user.experiment;

import java.util.Collections;
import java.util.List;
import org.dmieter.sch.prob.job.Job;
import org.dmieter.sch.prob.job.RegularJob;
import org.dmieter.sch.prob.resources.ResourceDomain;
import org.dmieter.sch.prob.scheduler.AvaScheduler;
import org.dmieter.sch.prob.scheduler.Scheduler;
import org.dmieter.sch.prob.scheduler.SchedulerSettings;
import org.dmieter.sch.prob.user.ResourceRequest;

/**
 *
 * @author dmieter
 */
public class SimpleExperiment implements Experiment {

    private Scheduler scheduler;
    private SchedulerSettings settings;
    
    
    @Override
    public void run(int expNnum) {
        initScheduler();
        
        for(int i = 0; i < expNnum; i++){
            System.out.println(i);
            runSingleExperiment();
        }
    }
    
    public void runSingleExperiment(){
        ResourceDomain domain = generateResources();
        Job job = generateJobFlow().get(0);
        
        scheduler.flush();
        scheduler.schedule(job, domain, settings);
    }

    @Override
    public String printResults() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private ResourceDomain generateResources() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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

}

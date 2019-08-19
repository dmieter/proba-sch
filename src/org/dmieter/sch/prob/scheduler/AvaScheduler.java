package org.dmieter.sch.prob.scheduler;

import org.dmieter.sch.prob.job.Job;
import org.dmieter.sch.prob.resources.ResourceDomain;

/**
 *
 * @author dmieter
 */
public class AvaScheduler implements Scheduler {

    public boolean schedule(Job job, ResourceDomain domain, SchedulerSettings settings) {
        return false;
    }

    @Override
    public void flush() {
        
    }
}

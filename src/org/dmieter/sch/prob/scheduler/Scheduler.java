
package org.dmieter.sch.prob.scheduler;

import org.dmieter.sch.prob.job.Job;
import org.dmieter.sch.prob.resources.ResourceDomain;

/**
 *
 * @author dmieter
 */
public interface Scheduler {
    public boolean schedule(Job job, ResourceDomain domain, 
                                int currentTime, SchedulerSettings settings);
    public void flush();
}

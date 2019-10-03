package org.dmieter.sch.prob.scheduler;

import java.util.List;
import org.dmieter.sch.prob.job.Job;
import org.dmieter.sch.prob.resources.Resource;
import org.dmieter.sch.prob.resources.ResourceDomain;
import org.dmieter.sch.prob.resources.ResourcesAllocation;

/**
 *
 * @author dmieter
 */
public class AvaScheduler implements Scheduler {

    AvaSchedulerSettings settings;

    public boolean schedule(Job job, ResourceDomain domain,
            int currentTime, SchedulerSettings settings) {

        if (settings instanceof AvaSchedulerSettings) {
            this.settings = (AvaSchedulerSettings) settings;
            return scheduleLocal(job, domain, currentTime);
        } else {
            throw new IllegalStateException("AvaScheduler requires AvaSchedulerSettings at input");
        }

    }

    protected boolean scheduleLocal(Job job, ResourceDomain domain, int currentTime) {

        Integer deadline = settings.getDeadline();

        for (int t = currentTime; t < settings.getDeadline(); t += settings.getScanDelta()) {
            Allocation curAllocation = findBestAllocation(domain, t);
        }

        return true;
    }

    private Allocation findBestAllocation(ResourceDomain domain, int t) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void flush() {

    }

    private class Allocation {

        int startTime;
        int endTime;
        Double criterionValue;
        List<Resource> resources;
    }
}

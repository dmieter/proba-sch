package org.dmieter.sch.prob.scheduler;

import java.util.HashMap;
import java.util.Map;
import org.dmieter.sch.prob.job.Job;
import org.dmieter.sch.prob.resources.ResourceDomain;

/**
 *
 * @author dmieter
 */
public class AvaSchedulerOpt extends AvaScheduler {

    int startPoints = 30;
    int delta = 10; // nothing special could be in 10 time units

    @Override
    protected boolean scheduleScan(Job job, ResourceDomain domain, int currentTime) {

        Integer deadline = job.getPreferences().getDeadline();

        Map<Integer, Allocation> startPoints = new HashMap<>();
        Map<Integer, Allocation> minPoints = new HashMap<>();
        Integer step = Math.round((deadline - currentTime) / this.startPoints);

        for (int t = currentTime; t < deadline; t += step) {

            Allocation nextAllocation = findBestAllocation(job, domain, t, deadline);

            if (nextAllocation != null) {
                startPoints.put(t, nextAllocation);
            }
        }

        return true;
    }

    protected boolean addNextMaximum(Integer startTime, Integer deadline, Job job, ResourceDomain domain,
            Map<Integer, Allocation> startPoints, Map<Integer, Allocation> minPoints) {

        Allocation leftAllocation = null;

        if (startPoints.containsKey(startTime)) {
            leftAllocation = startPoints.get(startTime);
        } else {
            leftAllocation = findBestAllocation(job, domain, startTime, deadline);
        }

        for (int t = startTime + delta; t < deadline; t += delta) {

            Allocation rightAllocation = findBestAllocation(job, domain, t, deadline);

            if (rightAllocation.criterionValue > leftAllocation.criterionValue) {
                leftAllocation = rightAllocation;
                continue;  // continue going to maximum
            } else {
                Integer middleTime = getMiddleTime(leftAllocation.startTime, rightAllocation.startTime);
                Allocation middleAllocation = findBestAllocation(job, domain, middleTime, deadline);

            }

        }

        return true;
    }

    protected boolean findMaximumInInterval(Allocation leftAllocation, Allocation middleAllocation, Allocation rightAllocation,
            Job job, ResourceDomain domain, Integer deadline, Map<Integer, Allocation> minPoints) {

        Integer leftMiddleTime = getMiddleTime(leftAllocation.startTime, middleAllocation.startTime);
        Allocation leftMiddleAllocation = findBestAllocation(job, domain, leftMiddleTime, deadline);

        Integer rightMiddleTime = getMiddleTime(middleAllocation.startTime, rightAllocation.startTime);
        Allocation rightMiddleAllocation = findBestAllocation(job, domain, rightMiddleTime, deadline);

    }

    private Integer getMiddleTime(Integer leftTime, Integer rightTime) {
        return leftTime + Math.round((rightTime - leftTime) / 2);
    }
}

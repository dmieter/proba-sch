package org.dmieter.sch.prob.scheduler.allocator.tree;

import org.dmieter.sch.prob.job.Job;
import org.dmieter.sch.prob.scheduler.allocator.ResourceAvailability;
import org.dmieter.sch.prob.scheduler.allocator.ResourceAvailabilityGroup;

import java.util.Collections;
import java.util.List;

public class BruteForceAllocator extends AbstractGroupAllocator {

    public static List<ResourceAvailability> allocateResources(
            Job job,
            List<ResourceAvailabilityGroup> resourceGroups,
            Integer startTime,
            Integer endTime) {

        return Collections.EMPTY_LIST;
    }

    private static boolean validateSolution(List<ResourceAvailability> solution) {
        return false;
    }
}

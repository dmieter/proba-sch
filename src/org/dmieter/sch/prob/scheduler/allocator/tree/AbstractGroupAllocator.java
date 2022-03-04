package org.dmieter.sch.prob.scheduler.allocator.tree;

import org.dmieter.sch.prob.scheduler.allocator.ResourceAvailabilityGroup;

import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractGroupAllocator {

    protected static List<ResourceAvailabilityGroup> estimateResourcesCost(
            List<ResourceAvailabilityGroup> groups, Integer startTime, Integer endTime) {

        return groups.stream()
                .map(group -> group.copyWithCostEstimate(startTime, endTime))
                .collect(Collectors.toList());
    }
}

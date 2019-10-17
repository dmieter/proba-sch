package org.dmieter.sch.prob.scheduler.criteria;

import java.util.List;
import org.dmieter.sch.prob.resources.Resource;
import org.dmieter.sch.prob.resources.ResourcesAllocation;

/**
 *
 * @author dmieter
 */
public class SumCostCriterion implements AllocationCriterion {

    @Override
    public double getValue(ResourcesAllocation allocation) {
        return getValue(allocation.getResources(), allocation.getStartTime(), allocation.getEndTime());
    }

    @Override
    public double getValue(List<Resource> resources, int startTime, int endTime) {
        return resources.stream()
                .map(r -> r.getDescription().price*(endTime-startTime))
                .reduce(0d, (a,b) -> (a+b));
    }
}

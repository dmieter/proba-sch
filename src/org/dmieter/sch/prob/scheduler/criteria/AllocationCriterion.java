
package org.dmieter.sch.prob.scheduler.criteria;

import java.util.List;
import org.dmieter.sch.prob.resources.Resource;
import org.dmieter.sch.prob.resources.ResourcesAllocation;

/**
 *
 * @author dmieter
 */
public interface AllocationCriterion {
    
    public double getValue(ResourcesAllocation allocation);
    
    public double getValue(List<Resource> resources, int startTime, int endTime);
}

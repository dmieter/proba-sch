package org.dmieter.sch.prob.scheduler.allocator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.dmieter.sch.prob.job.Job;
import org.dmieter.sch.prob.resources.Resource;

/**
 *
 * @author dmieter
 */
public class GreedyMaxPAllocator {

    public static List<ResourceAvailability> allocateResources(Job job, List<ResourceAvailability> feasibleResources
                                                               ,Integer startTime, Integer endTime) {
        
        // 1. sorting resources by availabilityP in decreasing order
        List<ResourceAvailability> resources = new ArrayList<>(feasibleResources);
        Collections.sort(resources, 
                (r1,r2) -> r2.getAvailabilityP().compareTo(r1.getAvailabilityP()));  
        
        // 2. retrieving first n resources
        return resources.subList(0, job.getResourceRequest().getParallelNum());
    }
}

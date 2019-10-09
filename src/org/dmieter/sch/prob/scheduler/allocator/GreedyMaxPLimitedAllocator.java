package org.dmieter.sch.prob.scheduler.allocator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.dmieter.algorithm.opt.knapsack.Item;
import java.util.stream.Collectors;
import lombok.val;
import org.dmieter.sch.prob.job.Job;
import org.dmieter.sch.prob.job.JobController;
import org.dmieter.sch.prob.resources.Resource;

/**
 *
 * @author dmieter
 */
public class GreedyMaxPLimitedAllocator {

    public static List<ResourceAvailability> allocateResources(Job job, List<ResourceAvailability> feasibleResources) {
        
        List<Item> items = feasibleResources.stream()
                .map(r -> new Item(r.getOrderNum(),
                                    JobController.estimateExecutionTime(job, r.getResource()),
                                    r.getAvailabilityP()))
                .collect(Collectors.toList());
        
        Collections.sort(items, 
                (i1,i2) -> Double.valueOf(i1.getValue()/i1.getWeight()).compareTo(
                            Double.valueOf(i2.getValue()/i2.getWeight())));

        // 1. sorting resources by availabilityP in decreasing order
        List<ResourceAvailability> resources = new ArrayList<>(feasibleResources);
        Collections.sort(resources, 
                (r1,r2) -> r2.getAvailabilityP().compareTo(r1.getAvailabilityP()));  
        
        // 2. retrieving first n resources
        return resources.subList(0, job.getResourceRequest().getParallelNum());
    }
}

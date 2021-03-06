package org.dmieter.sch.prob.scheduler.allocator;

import java.util.Collections;
import java.util.List;
import com.dmieter.algorithm.opt.knapsack.Item;
import org.dmieter.sch.prob.job.Job;

/**
 *
 * @author dmieter
 */
public class MinCostAllocator extends OptAllocator {

    public static List<ResourceAvailability> allocateResources(Job job, List<ResourceAvailability> feasibleResources
                                                                ,Integer startTime, Integer endTime) {
        
        // 0. Prepare optimization items
        List<Item> items = prepareOptimizationItems(job, feasibleResources, startTime, endTime);
        
        
        // getting n cheapest items
        Collections.sort(items, 
                (i1,i2) -> Integer.valueOf(i1.getWeight()).compareTo(i2.getWeight()));
        
        List<Item> candidateSolution = items.subList(0, job.getResourceRequest().getParallelNum());
        if(checkWeightLimitRequirement(candidateSolution, job.getResourceRequest().getBudget())){
            return mapResultSolution(candidateSolution);
        } 
        
        // in case no feasible solution obtained
        return null;
    }
}

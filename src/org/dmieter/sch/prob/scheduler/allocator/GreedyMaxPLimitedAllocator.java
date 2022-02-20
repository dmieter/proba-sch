package org.dmieter.sch.prob.scheduler.allocator;

import java.util.Collections;
import java.util.List;
import com.dmieter.algorithm.opt.knapsack.Item;
import org.dmieter.sch.prob.job.Job;

/**
 *
 * @author dmieter
 */
public class GreedyMaxPLimitedAllocator extends OptAllocator {

    public static Integer bestWin = 0;
    public static Double bestLen = 0d;
    public static Integer greedyWin = 0;
    public static Double greedyLen = 0d;
    public static Integer mincostWin = 0;
    public static Double mincostLen = 0d;
    
    public static List<ResourceAvailability> allocateResources(Job job, List<ResourceAvailability> feasibleResources
                                                                ,Integer startTime, Integer endTime) {
        
        // 0. Prepare optimization items
        List<Item> items = prepareOptimizationItems(job, feasibleResources, startTime, endTime);
        
        
        // 1. First try to get n most probable resources
        Collections.sort(items, 
                (i1,i2) -> Double.valueOf(i2.getValue()).compareTo(i1.getValue()));
        
        List<Item> candidateSolution = items.subList(0, job.getResourceRequest().getParallelNum());
        if(checkWeightLimitRequirement(candidateSolution, job.getResourceRequest().getBudget())){
            bestWin++;
            bestLen = (bestLen*(bestWin-1) + endTime - startTime)/bestWin;
            return mapResultSolution(candidateSolution);
        }
        
        // 2. Second try to get n most effective
        Collections.sort(items, 
                (i1,i2) -> Double.valueOf(i2.getValue()/i2.getWeight())
                                 .compareTo(i1.getValue()/i1.getWeight()));
        
        candidateSolution = items.subList(0, job.getResourceRequest().getParallelNum());
        if(checkWeightLimitRequirement(candidateSolution, job.getResourceRequest().getBudget())){
            greedyWin++;
            greedyLen = (greedyLen*(greedyWin-1) + endTime - startTime)/greedyWin;
            return mapResultSolution(candidateSolution);
        }

        // 3. Third try to get n cheapest items
        Collections.sort(items, 
                (i1,i2) -> Integer.valueOf(i1.getWeight()).compareTo(i2.getWeight()));
        
        candidateSolution = items.subList(0, job.getResourceRequest().getParallelNum());
        if(checkWeightLimitRequirement(candidateSolution, job.getResourceRequest().getBudget())){
            mincostWin++;
            mincostLen = (mincostLen*(mincostWin-1) + endTime - startTime)/mincostWin;
            return mapResultSolution(candidateSolution);
        } 
        
        // in case no feasible solution obtained
        return null;
    }
}

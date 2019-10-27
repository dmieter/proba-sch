package org.dmieter.sch.prob.scheduler.allocator;

import com.dmieter.algorithm.opt.knapsack.Item;
import com.dmieter.algorithm.opt.knapsack.knapsack01.multiweights.FixedItemsNumberKnapsackProblem;
import com.dmieter.algorithm.opt.knapsack.knapsack01.multiweights.IKnapsack01MultiWeightsSolver;
import com.dmieter.algorithm.opt.knapsack.knapsack01.multiweights.multiplicative.FixedItemsMultiplicativeSolver;
import java.util.List;
import org.dmieter.sch.prob.job.Job;

/**
 *
 * @author dmieter
 */
public class KnapsackMaxPAllocator extends OptAllocator {

    public static List<ResourceAvailability> allocateResources(Job job, List<ResourceAvailability> feasibleResources
                                                                ,Integer startTime, Integer endTime) {

        // 1. Prepare optimization items
        List<Item> items = prepareOptimizationItems(job, feasibleResources, startTime, endTime);
        
        // 2. Prepare optimization problem
        FixedItemsNumberKnapsackProblem problem = new FixedItemsNumberKnapsackProblem();
        problem.setItems(items);
        problem.setMaxWeight(job.getResourceRequest().getBudget());
        problem.setItemsRequiredNumber(job.getResourceRequest().getParallelNum());
        
        IKnapsack01MultiWeightsSolver solver = new FixedItemsMultiplicativeSolver();
        
        if(solver.solve(problem)){
            return mapResultSolution(problem.getSelectedItems());
        }
        
        // in case no feasible solution obtained
        return null;
    }
}

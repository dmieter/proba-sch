
package org.dmieter.sch.prob.scheduler.allocator;

import com.dmieter.algorithm.opt.knapsack.Item;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.dmieter.sch.prob.job.Job;
import project.math.utils.MathUtils;

/**
 *
 * @author emelyanov
 */
public class OptAllocator {

    protected static boolean checkWeightLimitRequirement(List<Item> solution, Integer weightLimit){
        Integer totalWeight = solution.stream()
                .map(item -> item.getWeight())
                .reduce(0, (t, u) -> (t + u));
        
        return weightLimit >= totalWeight;
    }
    
    protected static List<Item> prepareOptimizationItems(Job job, List<ResourceAvailability> resources
                                                            ,Integer startTime, Integer endTime){
        List<Item> items = new ArrayList<>();
        for(ResourceAvailability resource : resources){
            Item item = new Item(resource.getOrderNum(),
                                 MathUtils.intNextUp(resource.getResource().estimateUsageCost(startTime, endTime)), 
                                 resource.getAvailabilityP());
            item.setRefObject(resource);
            items.add(item);
        }
        
        return items;
    }
    
    protected static List<ResourceAvailability> mapResultSolution(List<Item> solution, List<ResourceAvailability> resources){
        Set<Integer> resultIDs = new HashSet<>();
        solution.stream().forEach(item -> resultIDs.add(item.id));
        
        return resources.stream()
                .filter(r -> resultIDs.contains(r.orderNum))
                .collect(Collectors.toList());
    }
    
    protected static List<ResourceAvailability> mapResultSolution(List<Item> solution){
        return solution.stream()
                .map(item -> (ResourceAvailability)item.getRefObject())
                .collect(Collectors.toList());
    }

}

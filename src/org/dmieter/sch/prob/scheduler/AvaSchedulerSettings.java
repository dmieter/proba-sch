package org.dmieter.sch.prob.scheduler;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author dmieter
 */

@Getter
@Setter
public class AvaSchedulerSettings extends SchedulerSettings{
    
    public enum OptProblem {
        MAX_PROBABILITY,    // maximizing execution probability before deadline
        FIRST_PROBABLE      // minimizing start/finish time while providing required execution probability
    }
    
    public enum SchMode {
        MIN_COST,
        GREEDY_SIMPLE,
        GREEDY_LIMITED,
        KNAPSACK
    }
    
    private OptProblem optimizationProblem = OptProblem.MAX_PROBABILITY;
    
    private SchMode schedulingMode = SchMode.GREEDY_SIMPLE;

}

package org.dmieter.sch.prob.scheduler.criteria;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author dmieter
 */
@Getter
@Setter
public class UserPreferenceModel {

    private Integer costBudget;

    private Integer deadline;

    private AllocationCriterion criterion;

    private Double minAvailability;
    
    public UserPreferenceModel copy(){
        UserPreferenceModel copy = new UserPreferenceModel();
        copy.costBudget = costBudget;
        copy.deadline = deadline;
        copy.minAvailability = minAvailability;
        copy.criterion = criterion;
        
        return copy;
    }
}

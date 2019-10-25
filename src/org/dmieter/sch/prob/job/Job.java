package org.dmieter.sch.prob.job;

import lombok.Getter;
import lombok.Setter;
import org.dmieter.sch.prob.user.ResourceRequest;
import org.dmieter.sch.prob.resources.ResourcesAllocation;
import org.dmieter.sch.prob.scheduler.criteria.UserPreferenceModel;

/**
 *
 * @author emelyanov
 */

@Getter
@Setter
public class Job {

    protected String name;
    
    protected ResourceRequest resourceRequest;

    protected ResourcesAllocation resourcesAllocation;

    protected Double startVariability = 0d;
    protected Double finishVariability = 0d;
    
    private UserPreferenceModel preferences;
    
    public Job(){
        
    }
    
    public Job(String name){
        this.name = name;
    }
    
    public Job(ResourceRequest request){
        this.resourceRequest = request;
    }

    public Job copy(){
        Job copy = new Job(name);
        copy.startVariability = startVariability;
        copy.finishVariability = finishVariability;
        copy.preferences = preferences.copy();
        copy.resourceRequest = resourceRequest.copy();
        // we don't copy allocations!!!
        
        return copy;
    }
    
}

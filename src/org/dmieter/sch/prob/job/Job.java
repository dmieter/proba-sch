package org.dmieter.sch.prob.job;

import lombok.Getter;
import lombok.Setter;
import org.dmieter.sch.prob.user.ResourceRequest;
import org.dmieter.sch.prob.resources.ResourcesAllocation;

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

    protected Double startVariability;
    protected Double finishVariability;
    
    public Job(){
        
    }
    
    public Job(String name){
        this.name = name;
    }
    
    public Job(ResourceRequest request){
        this.resourceRequest = request;
    }

}

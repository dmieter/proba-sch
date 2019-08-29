package org.dmieter.sch.prob.resources;

import java.util.List;
import lombok.Getter;

/**
 *
 * @author dmieter
 */

@Getter
public class ResourceDomain {
    protected List<Resource> resources;
    
    public ResourceDomain(){
        
    }
    
    public ResourceDomain(List<Resource> resources){
        this.resources = resources;
    }
}

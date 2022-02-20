package org.dmieter.sch.prob.resources;

import java.util.List;
import java.util.stream.Collectors;
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
    
    public ResourceDomain copy(){
        List<Resource> copyResources = resources.stream()
                .map(r -> r.copy())
                .collect(Collectors.toList());
        
        return new ResourceDomain(copyResources);
    }
}

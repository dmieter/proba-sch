package org.dmieter.sch.prob.scheduler.allocator;

import lombok.Getter;
import lombok.Setter;
import org.dmieter.sch.prob.resources.Resource;

/**
 *
 * @author dmieter
 */

@Getter
@Setter
public class ResourceAvailability extends AvailabilityEntity {

    public Resource resource;
    public ResourceAvailabilityGroup group;

    public ResourceAvailability(Integer orderNum, Resource resource, Double availabilityP) {
        super(orderNum, availabilityP);
        this.resource = resource;
    }
    
    public ResourceAvailability copy(){
        return new ResourceAvailability(orderNum, resource.copy(), availabilityP);
    }
}

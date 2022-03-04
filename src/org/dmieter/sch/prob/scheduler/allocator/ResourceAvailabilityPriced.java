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
public class ResourceAvailabilityPriced extends ResourceAvailability {

    public Double cost;

    public ResourceAvailabilityPriced(Integer orderNum, Resource resource, Double availabilityP, Double cost) {
        super(orderNum, resource, availabilityP);
        this.cost = cost;
    }

    public ResourceAvailabilityPriced(ResourceAvailability ra, Double cost) {
        super(ra.orderNum, ra.resource, ra.availabilityP);
        this.cost = cost;
    }
    
    public ResourceAvailabilityPriced copy(){
        return new ResourceAvailabilityPriced(orderNum, resource.copy(), availabilityP, cost);
    }
}

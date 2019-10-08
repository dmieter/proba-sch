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
public class ResourceAvailability {

    public Resource resource;
    public Double availabilityP;

    public ResourceAvailability(Resource resource, Double availabilityP) {
        this.resource = resource;
        this.availabilityP = availabilityP;
    }
}

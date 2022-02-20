package org.dmieter.sch.prob.scheduler.allocator;

import lombok.Getter;
import lombok.Setter;
import org.dmieter.sch.prob.resources.Resource;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ResourceAvailabilityGroup extends AvailabilityEntity {

    protected List<ResourceAvailability> resources;

    public ResourceAvailabilityGroup(Integer orderNum, Double availabilityP) {
        super(orderNum, availabilityP);
        resources = new ArrayList<>();
    }
}

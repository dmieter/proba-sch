package org.dmieter.sch.prob.scheduler.allocator;

import lombok.Getter;
import lombok.Setter;
import org.dmieter.sch.prob.resources.Resource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class ResourceAvailabilityGroup extends AvailabilityEntity {

    protected List<ResourceAvailability> resources;

    public ResourceAvailabilityGroup(Integer orderNum, Double availabilityP) {
        super(orderNum, availabilityP);
        resources = new ArrayList<>();
    }

    public ResourceAvailabilityGroup copyWithCostEstimate(Integer startTime, Integer endTime) {
        ResourceAvailabilityGroup newGroup = new ResourceAvailabilityGroup(orderNum, availabilityP);
        newGroup.setResources(resources.stream()
        .map(ra -> new ResourceAvailabilityPriced(ra, ra.resource.estimateUsageCost(startTime, endTime)))
        .collect(Collectors.toList()));

        newGroup.getResources().stream().forEach(rap -> {
            rap.group = newGroup;
        });

        return newGroup;
    }

}

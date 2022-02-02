package org.dmieter.sch.prob.scheduler.allocator;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AvailabilityEntity {

    public Integer orderNum;
    public Double availabilityP;

    public AvailabilityEntity(Integer orderNum, Double availabilityP) {
        this.orderNum = orderNum;
        this.availabilityP = availabilityP;
    }

    public AvailabilityEntity copy(){
        return new AvailabilityEntity(orderNum, availabilityP);
    }
}

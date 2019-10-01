package org.dmieter.sch.prob.scheduler.criteria;

import java.util.List;
import java.util.stream.Stream;
import org.dmieter.sch.prob.events.Event;
import org.dmieter.sch.prob.resources.Resource;
import org.dmieter.sch.prob.resources.ResourcesAllocation;

/**
 *
 * @author dmieter
 */
public class AvailableProbabilityCriterion implements AllocationCriterion {

    @Override
    public double getValue(ResourcesAllocation allocation) {
        return getValue(allocation.getResources(), allocation.getStartTime(), allocation.getEndTime());
    }

    @Override
    public double getValue(List<Resource> resources, int startTime, int endTime) {
        Double availableProb = 1d;
        for(Resource r: resources){
           List<Event> events =  r.getActiveEvents(startTime, endTime);
           if(!events.isEmpty()){
           Double minAvailabilityP = events.stream()
                   .map(e -> getMinAvailabilityP(e, startTime, endTime))
                   .min((a, b) -> a.compareTo(b)).get();
           availableProb *= minAvailabilityP;
           }
        }
        
        return availableProb;
    }

    private Double getMinAvailabilityP(Event e, int startTime, int endTime) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}

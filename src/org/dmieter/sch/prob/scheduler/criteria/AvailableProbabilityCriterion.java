package org.dmieter.sch.prob.scheduler.criteria;

import java.util.List;
import org.dmieter.sch.prob.ProbabilityUtils;
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
           Double resourceAvailable = ProbabilityUtils.getAvailabilityProbability(events, startTime, endTime);
           availableProb *= resourceAvailable;
        }
        
        return availableProb;
    }
}

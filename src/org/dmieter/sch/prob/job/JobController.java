package org.dmieter.sch.prob.job;

import org.dmieter.sch.prob.distribution.Distribution;
import org.dmieter.sch.prob.distribution.NormalEventDistribution;
import org.dmieter.sch.prob.events.Event;
import org.dmieter.sch.prob.events.EventType;
import org.dmieter.sch.prob.resources.Resource;
import org.dmieter.sch.prob.resources.ResourcesAllocation;

/**
 *
 * @author dmieter
 */
public class JobController {
    
    public static Event generateStartEvent(Job job, ResourcesAllocation allocation, 
                                            Integer startTime, Integer endTime){
        
        Distribution distribution = new NormalEventDistribution(startTime.doubleValue(), 
                                                                job.startVariability);
        Event startEvent = new Event(distribution,
                                        startTime, 
                                        startTime + 10, 
                                        EventType.ALLOCATING_RESOURCE);
    }
}

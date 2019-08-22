package org.dmieter.sch.prob.job;

import org.dmieter.sch.prob.distribution.Distribution;
import org.dmieter.sch.prob.distribution.NormalEventDistribution;
import org.dmieter.sch.prob.distribution.QuazyUniformDistribution;
import org.dmieter.sch.prob.events.Event;
import org.dmieter.sch.prob.events.EventType;
import org.dmieter.sch.prob.resources.ResourcesAllocation;
import project.math.utils.MathUtils;

/**
 *
 * @author dmieter
 */
public class JobController {
    
    private static final Double SD_INTERVAL_COEFFICIENT = 3d;
    
    public static void generateEvents(Job job, ResourcesAllocation allocation){
        
        Event startEvent = generateJobStartEvent(job, allocation);
        Event finishEvent = generateJobFinishEvent(job, allocation);
        
        if(startEvent.getEndTime() > finishEvent.getStartTime()){
            // start time and end time variabilities should be ok with job length
            Integer middleTime = MathUtils.intNextUp(allocation.getStartTime() 
                                    + (allocation.getEndTime() - allocation.getStartTime()) 
                                        * (job.getStartVariability()/job.getFinishVariability()));
            startEvent.setEndTime(middleTime);
            finishEvent.setStartTime(middleTime);
            
        } else{
            Event executionEvent = generateJobExecutionEvent(job, allocation, 
                                                    startEvent.getEndTime(), 
                                                    finishEvent.getStartTime());
            allocation.setExecutionEvent(executionEvent);
        }
        
        allocation.setStartEvent(startEvent);
        allocation.setFinishEvent(finishEvent);
        
    }
    
    public static Event generateJobStartEvent(Job job, ResourcesAllocation allocation){
        Distribution distribution = new NormalEventDistribution(allocation.getStartTime().doubleValue(), 
                                                                job.startVariability);
        
        // we can't start before scheduled time
        Integer leftTime = allocation.getStartTime();  
        
        // N epsilon interval for process to start
        Integer rightTime = MathUtils.intNextUp(allocation.getStartTime() + SD_INTERVAL_COEFFICIENT * job.startVariability);
        
        
        return new Event(distribution, leftTime, rightTime, EventType.ALLOCATING_RESOURCE);
    }
    
    public static Event generateJobFinishEvent(Job job, ResourcesAllocation allocation){
        Distribution distribution = new NormalEventDistribution(allocation.getStartTime().doubleValue(), 
                                                                job.startVariability);

        // N epsilon interval for process to finish                                                       
        Integer halfIntervalLength = MathUtils.intNextUp(SD_INTERVAL_COEFFICIENT * job.finishVariability);
        Integer leftTime = allocation.getEndTime() - halfIntervalLength;  
        Integer rightTime = allocation.getEndTime() + halfIntervalLength;
        
        return new Event(distribution, leftTime, rightTime, EventType.RELEASING_RESOURCE);
    }
    
    public static Event generateJobExecutionEvent(Job job, ResourcesAllocation allocation, 
                                                    Integer eventStartTime, Integer eventEndTime){
        return new Event(new QuazyUniformDistribution(1d), eventStartTime, eventEndTime, EventType.GENERAL);
    }
}

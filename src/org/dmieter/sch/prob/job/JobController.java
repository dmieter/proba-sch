package org.dmieter.sch.prob.job;

import org.dmieter.sch.prob.distribution.Distribution;
import org.dmieter.sch.prob.distribution.LongTailNormalEventDistribution;
import org.dmieter.sch.prob.distribution.NormalEventDistribution;
import org.dmieter.sch.prob.distribution.QuazyUniformDistribution;
import org.dmieter.sch.prob.events.Event;
import org.dmieter.sch.prob.events.EventType;
import org.dmieter.sch.prob.resources.Resource;
import org.dmieter.sch.prob.resources.ResourcesAllocation;
import project.math.utils.MathUtils;

/**
 *
 * @author dmieter
 */
public class JobController {
    
    private static final Double SD_INTERVAL_COEFFICIENT = 3d;
    
    public static void generateEvents(Job job){
        
        if(job.getResourcesAllocation() == null){
            return;
        }
        
        Event startEvent = generateJobStartEvent(job);
        Event finishEvent = generateJobFinishEvent(job);
        
        // for more beautiful jobs generation
        while(startEvent.getEndTime() > finishEvent.getStartTime()){
            job.setStartVariability(job.getStartVariability()*0.95);
            job.setFinishVariability(job.getFinishVariability()*0.95);
            startEvent = generateJobStartEvent(job);
            finishEvent = generateJobFinishEvent(job);
        }
        
        if(startEvent.getEndTime() > finishEvent.getStartTime()){
            // start time and end time variabilities should be ok with job length
            Integer middleTime = MathUtils.intNextUp(startEvent.getEventTime()
                                    + (finishEvent.getEventTime() - startEvent.getEventTime()) 
                                        * (job.getStartVariability()/(2*job.getFinishVariability())));
            startEvent.setEndTime(middleTime);
            finishEvent.setStartTime(middleTime);
            
        } else{
            Event executionEvent = generateJobExecutionEvent(job, 
                                                            startEvent.getEndTime(), 
                                                            finishEvent.getStartTime());
            job.getResourcesAllocation().setExecutionEvent(executionEvent);
        }
        
        job.getResourcesAllocation().setStartEvent(startEvent);
        job.getResourcesAllocation().setFinishEvent(finishEvent);
        
    }
    
    public static Event generateJobStartEvent(Job job){
        
        if(job.startVariability <= 0d){
            return generateDummyGeneralEvent(job.getResourcesAllocation().getStartTime());
        }
        
        Integer halfIntervalLength = MathUtils.intNextUp(SD_INTERVAL_COEFFICIENT * job.startVariability);

        // we can't start before scheduled time
        Integer leftTime = job.getResourcesAllocation().getStartTime();  
        
        // N epsilon interval for process to start
        Integer rightTime = job.getResourcesAllocation().getStartTime() + 2*halfIntervalLength;
        
        Distribution distribution = new NormalEventDistribution(job.getResourcesAllocation().getStartTime().doubleValue() + halfIntervalLength, 
                                                                job.startVariability);
        
        return new Event(distribution, leftTime, rightTime, job.getResourcesAllocation().getStartTime() + halfIntervalLength, EventType.ALLOCATING_RESOURCE);
    }
    
    public static Event generateJobFinishEvent(Job job){
        
        if(job.finishVariability <= 1d){
            return generateDummyGeneralEvent(job.getResourcesAllocation().getEndTime());
        }
        
        Distribution distribution = new LongTailNormalEventDistribution(job.getResourcesAllocation().getEndTime().doubleValue(), 
                                                                job.finishVariability);

        // N epsilon interval for process to finish                                                       
        Integer halfIntervalLength = MathUtils.intNextUp(SD_INTERVAL_COEFFICIENT * job.finishVariability);
        Integer leftTime = job.getResourcesAllocation().getEndTime() - halfIntervalLength;  
        Integer rightTime = job.getResourcesAllocation().getEndTime() + halfIntervalLength*20;
        
        return new Event(distribution, leftTime, rightTime, job.getResourcesAllocation().getEndTime(), EventType.RELEASING_RESOURCE);
    }
    
    public static Event generateJobExecutionEvent(Job job, Integer eventStartTime, Integer eventEndTime){
        return new Event(new QuazyUniformDistribution(1d), eventStartTime, eventEndTime, eventStartTime, EventType.GENERAL);
    }
    
    public static Integer estimateExecutionTime(Job job, Resource resource){
        return MathUtils.intNextUp(job.getResourceRequest().getVolume()/resource.getDescription().mips);
    }

    private static Event generateDummyGeneralEvent(int eventTime) {
        return new Event(new QuazyUniformDistribution(1d), eventTime, eventTime, eventTime, EventType.GENERAL);
    }
}

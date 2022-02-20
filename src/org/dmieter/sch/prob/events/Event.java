package org.dmieter.sch.prob.events;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.dmieter.sch.prob.distribution.Distribution;
import org.dmieter.sch.prob.resources.Resource;
import project.math.utils.MathUtils;

/**
 *
 * @author emelyanov
 */

@Getter
@Setter
public class Event {

    protected EventStatus status = EventStatus.ACTIVE;
    protected EventType type;

    protected Integer startTime = Integer.MIN_VALUE;
    protected Integer endTime  = Integer.MAX_VALUE;
    protected Integer eventTime;

    protected Distribution distribution;

    protected List<Resource> affectedResources = new ArrayList<>();
    
    protected Color eventColor;

    public Event(Distribution distribution, Integer startTime, Integer endTime, Integer eventTime, EventType type) {
        this.distribution = distribution;
        this.startTime = startTime;
        this.endTime = endTime;
        this.eventTime = eventTime;
        this.type = type;
    }

    public Event(Distribution distribution, Integer startTime, Integer endTime, Integer eventTime,
                                                            EventType type, Resource resource) {
        this.distribution = distribution;
        this.startTime = startTime;
        this.endTime = endTime;
        this.eventTime = eventTime;
        this.type = type;

        affectedResources.add(resource);
    }
    
    public Event copy(){
        Event copy = new Event(distribution.copy(),  // should we copy distribution as well? 
                                    startTime, 
                                    endTime,
                                    eventTime, 
                                    type);
        
        copy.status = status;
        copy.eventColor = eventColor;
        
        return copy;
    }
    
    public Double getEventFinishedP(Integer time){
        if(time < startTime){
            return 0d;  // event not started and thus not finished
        } else if(time > endTime){
            return 1d;  // event is surely finished after end time
        } else{
            return distribution.getProbability(time);
        }
    }
    
    public Double getResourcesAvailableP(Integer time){
        Double eventFinishedP = getEventFinishedP(time);
        
        if(type == EventType.RELEASING_RESOURCE){
            return eventFinishedP; // resource releasing P == resource availability P
        } else{
            return 1 - eventFinishedP; // resource allocation P == 1 - resource availability P
        }
    }
    
    public Double getResourcesAllocatedP(Integer time){
        Double eventFinishedP = getEventFinishedP(time);
        
        if(type == EventType.RELEASING_RESOURCE){
            return 1 - eventFinishedP; // resource releasing P == 1 - resource allocated P
        } else{
            return eventFinishedP; // resource allocation P == resource allocated P
        }
    }
    
    public boolean isActive(){
        return status == EventStatus.ACTIVE;
    }

    protected boolean ifCorrectTime(Integer t) {
        if (t >= startTime && t <= endTime) {
            return true;
        } else {
            return false;
        }
    }

    public void addAffectedResource(Resource resource) {
        affectedResources.add(resource);
    }
    
    public Integer collapseEvent(){
        Integer realizationTime = MathUtils.intNextUp(distribution.getSampleValue());
        if(realizationTime > endTime){
            eventTime = endTime;
        } else if(realizationTime < startTime){
            eventTime = startTime;
        }
        
        return eventTime;
    }

    public static int compareByTime(Event e1, Event e2){
        return e1.eventTime.compareTo(e2.eventTime);
    }
    
}

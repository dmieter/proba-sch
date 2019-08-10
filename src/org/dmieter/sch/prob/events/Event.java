package org.dmieter.sch.prob.events;

import java.util.ArrayList;
import java.util.List;
import org.dmieter.sch.prob.distribution.Distribution;
import org.dmieter.sch.prob.entity.Resource;

/**
 *
 * @author emelyanov
 */
public class Event {

    protected EventStatus status = EventStatus.ACTIVE;
    protected EventType type;

    protected Integer startTime = Integer.MIN_VALUE;
    protected Integer endTime  = Integer.MAX_VALUE;

    protected Distribution distribution;

    protected List<Resource> affectedResources = new ArrayList<>();

    public Event(Distribution distribution, Integer startTime, Integer endTime, EventType type) {
        this.distribution = distribution;
        this.startTime = startTime;
        this.endTime = endTime;
        this.type = type;
    }

    public Event(Distribution distribution, Integer startTime, Integer endTime, 
                                                            EventType type, Resource resource) {
        this.distribution = distribution;
        this.startTime = startTime;
        this.endTime = endTime;
        this.type = type;

        affectedResources.add(resource);
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

    protected boolean ifCorrectTime(Integer t) {
        if (t >= startTime && t <= endTime) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * @return the startTime
     */
    public int getStartTime() {
        return startTime;
    }

    /**
     * @param startTime the startTime to set
     */
    public void setStartTime(Integer startTime) {
        this.startTime = startTime;
    }

    /**
     * @return the endTime
     */
    public Integer getEndTime() {
        return endTime;
    }

    /**
     * @param endTime the endTime to set
     */
    public void setEndTime(Integer endTime) {
        this.endTime = endTime;
    }

    /**
     * @return the status
     */
    public EventStatus getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(EventStatus status) {
        this.status = status;
    }

    /**
     * @return the affectedResources
     */
    public List<Resource> getAffectedResources() {
        return affectedResources;
    }

    /**
     * @param affectedResources the affectedResources to set
     */
    public void setAffectedResources(List<Resource> affectedResources) {
        this.affectedResources = affectedResources;
    }

    public void addAffectedResource(Resource resource) {
        affectedResources.add(resource);
    }
}

package org.dmieter.sch.prob;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Queue;
import org.dmieter.sch.prob.resources.ResourceDomain;
import org.dmieter.sch.prob.events.Event;
import org.dmieter.sch.prob.events.EventType;
import org.dmieter.sch.prob.job.Job;
import org.dmieter.sch.prob.resources.Resource;
import org.dmieter.sch.prob.resources.ResourcesAllocation;

/**
 *
 * @author dmieter
 */
public class SchedulingController {
    
    protected Integer time;
    
    protected Queue<Event> events = new PriorityQueue<>(Comparator.comparing(Event::getStartTime));
    protected List<Event> finishedEvents = new LinkedList<>();
    
    protected ResourceDomain resourceDomain;
    
    public SchedulingController(ResourceDomain resourceDomain){
        this.resourceDomain = resourceDomain;
        time = 0;
    }
    
    public void scheduleJob(Job job){
        if(job.getResourcesAllocation() == null){
            return;
        }
        
        resourceDomain.getResources().stream()
                .filter(r -> job.getResourcesAllocation().getResources().contains(r))
                .forEach(r -> allocateResource(job.getResourcesAllocation(), r));

    }

    public void allocateResource(ResourcesAllocation allocation, Resource resource) {
        
        Event startEvent = allocation.getStartEvent();
        Event executionEvent = allocation.getExecutionEvent();
        Event finishEvent = allocation.getFinishEvent();
        
        events.add(startEvent);
        resource.addEvent(startEvent);
        
        if(executionEvent != null){
            events.add(executionEvent);
            resource.addEvent(executionEvent);
        }
        
        events.add(finishEvent);
        resource.addEvent(finishEvent);
    }
    
    public void processNewResourceEvent(Resource resource, Event event){
        events.add(event);
        resource.addEvent(event);
    }
    
    public static Optional<Event> getNextEvent(Integer startTime, Resource resource, EventType eventType){
        return getNextEvent(startTime, resource.getActiveEvents(startTime, Integer.MAX_VALUE), eventType);
    }
    
    public static Optional<Event> getNextEvent(Integer startTime, List<Event> events, EventType eventType){
        return events.stream().sequential()
                .filter(e -> e.getEventTime() >= startTime)
                .filter(e -> eventType.equals(e.getType()))
                .findFirst();
    }
    
    
    public static Optional<Event> getNextEvent(Integer startTime, Resource resource){
        return getNextEvent(startTime, resource.getActiveEvents(startTime, Integer.MAX_VALUE));
    }
    
    public static Optional<Event> getNextEvent(Integer startTime, List<Event> events){
        return events.stream().sequential()
                .filter(e -> e.getEventTime() >= startTime)
                .findFirst();
    }

    public ResourceDomain getResourceDomain(){
        return resourceDomain;
    }

}

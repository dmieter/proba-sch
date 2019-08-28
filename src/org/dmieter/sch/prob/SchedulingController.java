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

/**
 *
 * @author dmieter
 */
public class SchedulingController {
    protected Integer time;
    
    protected Queue<Event> events = new PriorityQueue<>(Comparator.comparing(Event::getStartTime));
    protected List<Event> finishedEvents = new LinkedList<>();
    
    protected ResourceDomain resourceDomain;
    
    protected void applyJob(Job job){
        if(job.getResourcesAllocation() == null){
            return;
        }
        

    }

    private void applyJobOnResource(Job job, Resource resource) {

    }
    
    public static Optional<Event> getNextEvent(Integer startTime, Resource resource, EventType eventType){
        return resource.getActiveEvents(startTime, Integer.MAX_VALUE).stream().sequential()
                .filter(e -> e.getEventTime() >= startTime)
                .filter(e -> eventType.equals(e.getType()))
                .findFirst();
    }

}

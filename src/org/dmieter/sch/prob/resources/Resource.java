package org.dmieter.sch.prob.resources;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.TreeSet;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import org.dmieter.sch.prob.events.Event;

/**
 *
 * @author emelyanov
 */
public class Resource {
    protected Long id;
    protected ResourceDescription description;
    protected Collection<Event> events = new ArrayList<>();
    
    public Resource(Long id, ResourceDescription description){
        this.id = id;
        this.description = description;
    }
    
    public void addEvent(Event event){
        getEvents().add(event);
    }
    
    public Collection<Event> getActiveEvents(Integer startTime, Integer endTime){
        return getEvents().stream()
                .filter(e -> (e.getStartTime() <= endTime && e.getEndTime() >= startTime))
                .filter(e -> e.isActive())
                .collect(Collectors.toList());
    }

    
    
    /**
     * @return the description
     */
    public ResourceDescription getDescription() {
        return description;
    }

    /**
     * @return the events
     */
    public Collection<Event> getEvents() {
        return events;
    }
    
}

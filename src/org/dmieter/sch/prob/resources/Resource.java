package org.dmieter.sch.prob.resources;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import org.dmieter.sch.prob.events.Event;

/**
 *
 * @author emelyanov
 */
@Getter
public class Resource {
    protected Long id;
    protected ResourceDescription description;
    protected List<Event> events = new ArrayList<>();
    
    public Resource(Long id, ResourceDescription description){
        this.id = id;
        this.description = description;
    }
    
    public void addEvent(Event event){
        events.add(event);
        Collections.sort(events, Comparator.comparing(Event::getEventTime));
    }
    
    public List<Event> getActiveEvents(Integer startTime, Integer endTime){
        return getEvents().stream()
                .filter(e -> (e.getStartTime() <= endTime && e.getEndTime() >= startTime))
                .filter(e -> e.isActive())
                .sorted(Comparator.comparing(Event::getEventTime))
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
    private Collection<Event> getEvents() {
        return events;
    }
    
}

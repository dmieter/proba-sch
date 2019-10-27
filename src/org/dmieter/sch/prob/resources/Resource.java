package org.dmieter.sch.prob.resources;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
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
    
    public Double estimateUsageCost(int startTime, int endTime){
        return description.price*(endTime-startTime);
    }
    
    public Resource copy(){
        Resource copy = new Resource(id, description.copy());
        List<Event> copyEvents = events.stream()
                .map(e -> e.copy())
                .collect(Collectors.toList());
        
        copy.events = copyEvents;
        
        return copy;
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

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + Objects.hashCode(this.id);
        return hash;
    }
    
    @Override
    public boolean equals(Object obj){
        if(obj instanceof Resource){
            return id.equals(((Resource)obj).id);
        }else{
            return false;
        }
    }
    
}

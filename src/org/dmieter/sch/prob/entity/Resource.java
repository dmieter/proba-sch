package org.dmieter.sch.prob.entity;

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
    protected Collection<Event> events = new ArrayList<>();
    
    public void addEvent(Event event){
       events.add(event);
    }
    
    public Collection<Event> getActiveEvents(Integer startTime, Integer endTime){
        return events.stream()
                .filter(e -> (e.getStartTime() <= endTime && e.getEndTime() >= startTime))
                .filter(e -> e.isActive())
                .collect(Collectors.toList());
    }
    
}

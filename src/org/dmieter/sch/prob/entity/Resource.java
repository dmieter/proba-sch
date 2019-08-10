package org.dmieter.sch.prob.entity;

import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import org.dmieter.sch.prob.events.Event;

/**
 *
 * @author emelyanov
 */
public class Resource {
    protected Queue<Event> events = new PriorityQueue<>(Comparator.comparing(Event::getStartTime));
    
    public void addEvent(Event event){
       events.add(event);
    }
    
}

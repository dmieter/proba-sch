package org.dmieter.sch.prob;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import org.dmieter.sch.prob.entity.ResourceDomain;
import org.dmieter.sch.prob.events.Event;

/**
 *
 * @author dmieter
 */
public class SchedulingController {
    protected Integer time;
    
    protected Queue<Event> events = new PriorityQueue<>(Comparator.comparing(Event::getStartTime));
    protected List<Event> finishedEvents = new LinkedList<>();
    
    protected ResourceDomain resourceDomain;
}

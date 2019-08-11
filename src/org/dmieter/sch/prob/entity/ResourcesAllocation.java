package org.dmieter.sch.prob.entity;

import java.util.LinkedList;
import java.util.List;
import org.dmieter.sch.prob.events.Event;

/**
 *
 * @author dmieter
 */
public class ResourcesAllocation {
    private Integer startTime;
    private Integer endTime;
    
    private List<Resource> resources = new LinkedList<>();
    
    private Event startEvent;
    private Event executionEvent;
    private Event finishEvent;
    
}

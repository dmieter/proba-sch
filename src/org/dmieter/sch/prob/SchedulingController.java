package org.dmieter.sch.prob;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import org.dmieter.sch.prob.resources.ResourceDomain;
import org.dmieter.sch.prob.events.Event;
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
        
        Event startEvent = new 
        
        //job.getResourcesAllocation().getResources().stream()
                .forEach(resource -> applyJobOnResource(job, resource));
    }

    private void applyJobOnResource(Job job, Resource resource) {
        job.g
        resource.
    }
}

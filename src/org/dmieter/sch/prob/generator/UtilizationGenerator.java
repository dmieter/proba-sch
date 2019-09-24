package org.dmieter.sch.prob.generator;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.apache.commons.math3.geometry.euclidean.oned.Interval;
import org.dmieter.sch.prob.SchedulingController;
import org.dmieter.sch.prob.events.Event;
import org.dmieter.sch.prob.events.EventType;
import org.dmieter.sch.prob.job.Job;
import org.dmieter.sch.prob.job.JobController;
import org.dmieter.sch.prob.resources.Resource;
import org.dmieter.sch.prob.resources.ResourceDomain;
import org.dmieter.sch.prob.resources.ResourcesAllocation;
import project.math.distributions.DistributionGenerator;
import project.math.distributions.UniformFacade;
import project.math.utils.MathUtils;

/**
 *
 * @author emelyanov
 */
public class UtilizationGenerator extends Generator {
    
    public Interval intLoad;
    public Interval intJobLength;
    public Interval intStartVariability;
    public Interval intFinishVariability;
    
    private SchedulingController schedulingController;
    
    public void generateUtilization(SchedulingController controller, Interval timeInterval) {
        
        this.schedulingController = controller;
        
        schedulingController.getResourceDomain().getResources().stream()//.parallel()
                .forEach(resource -> generateResourceUtilization(resource, timeInterval));
    }
    
    private void generateResourceUtilization(Resource resource, Interval timeInterval) {
        Double load = getUniformFromInterval(intLoad);
        Double sumJobsLength = 0d;
        
        int cnt = 0;
        while (sumJobsLength / timeInterval.getSize() < load) {
            sumJobsLength += generateAndAssignJob(resource, timeInterval);
            if (cnt++ > 100) {
                System.err.println("Can't reach " + load + " utilization for " + resource.getId());
                break;
            }
        }
        
    }
    
    private int generateAndAssignJob(Resource resource, Interval timeInterval) {
        
        int startTime = getUniformIntFromInterval(timeInterval);
        int jobLength = getUniformIntFromInterval(intJobLength);
        
        List<Event> events = resource.getActiveEvents(startTime, Integer.MAX_VALUE);
        if (events.isEmpty()) {
            return assignJob(resource, startTime, jobLength);
        }

        // 1. Check if we can start job at initial start time
        Optional<Event> nextEvent = SchedulingController.getNextEvent(startTime, events);
        if (!nextEvent.isPresent() || nextEvent.get().getEventTime() < startTime + jobLength) {
            // no intersections with events when starting at startTime
            return assignJob(resource, startTime, jobLength);
        }

        // 2. if not - find next finish event and start event and check distance between them
        while (startTime + jobLength < timeInterval.getSup()) {   // while we can schedule in interval
            Optional<Event> nextFinishEvent = SchedulingController.getNextEvent(startTime, events, EventType.RELEASING_RESOURCE);
            startTime = nextFinishEvent.get().getEventTime() + 1; // we can start just after next finish
            Optional<Event> nextStartEvent = SchedulingController.getNextEvent(startTime, events, EventType.ALLOCATING_RESOURCE);
            
            if (!nextStartEvent.isPresent() || nextStartEvent.get().getEventTime() < startTime + jobLength) {
                // enough time between current finish and next start
                return assignJob(resource, startTime, jobLength);
            } // else next cycle and next finish event procesing
        }
        
        return 0;
    }
    
    private int assignJob(Resource resource, int startTime, int jobLength) {
        
        // creating dummy utilization job
        Job job = new Job("local");
        job.setStartVariability(getUniformFromInterval(intStartVariability));
        job.setFinishVariability(getUniformFromInterval(intFinishVariability));
        
        // fill allocation on current resource
        ResourcesAllocation allocation = new ResourcesAllocation();
        allocation.setStartTime(startTime);
        allocation.setEndTime(startTime + jobLength);
        allocation.setResources(Collections.singletonList(resource));
        job.setResourcesAllocation(allocation);
        
        // generate events and assign to resource
        JobController.generateEvents(job);
        schedulingController.allocateResource(allocation, resource);
        
        return jobLength;
    }
    
}

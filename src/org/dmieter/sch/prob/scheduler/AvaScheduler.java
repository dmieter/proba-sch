package org.dmieter.sch.prob.scheduler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import org.dmieter.sch.prob.ProbabilityUtils;
import org.dmieter.sch.prob.job.Job;
import org.dmieter.sch.prob.job.JobController;
import org.dmieter.sch.prob.resources.Resource;
import org.dmieter.sch.prob.resources.ResourceDomain;
import org.dmieter.sch.prob.resources.ResourcesAllocation;
import org.dmieter.sch.prob.scheduler.allocator.Allocation;
import org.dmieter.sch.prob.scheduler.allocator.ResourceAvailability;

/**
 *
 * @author dmieter
 */
public class AvaScheduler implements Scheduler {

    AvaSchedulerSettings settings;

    @Override
    public void flush() {

    }
    
    public boolean schedule(Job job, ResourceDomain domain,
            int currentTime, SchedulerSettings settings) {

        if (settings instanceof AvaSchedulerSettings) {
            this.settings = (AvaSchedulerSettings) settings;
            return scheduleScan(job, domain, currentTime);
        } else {
            throw new IllegalStateException("AvaScheduler requires AvaSchedulerSettings at input");
        }

    }

    protected boolean scheduleScan(Job job, ResourceDomain domain, int currentTime) {
        
        Integer deadline = job.getPreferences().getDeadline();

        Double bestCriterionValue = Double.NEGATIVE_INFINITY;
        Allocation bestAllocation = null;
        
        for (int t = currentTime; t < deadline; t += settings.getScanDelta()) {
            Allocation curAllocation = findBestAllocation(job, domain, t, deadline);
            if(curAllocation.criterionValue > bestCriterionValue){
                bestAllocation = curAllocation;
                bestCriterionValue = curAllocation.criterionValue;
            }
        }
        
        if(bestAllocation != null){
            prepareJobAllocation(job, bestAllocation);
            return true;
        } else{
            return false;
        }
    }

    private Allocation findBestAllocation(Job job, ResourceDomain domain, int t, int deadline) {
        TreeMap<Integer, List<Resource>> performanceOptions
                                = preparePerformanceOptions(domain, job);
        
        List<Resource> availableResources = new ArrayList<>();
        
        for(Map.Entry<Integer, List<Resource>> entry : performanceOptions.entrySet()){
            availableResources.addAll(entry.getValue());
            //Allocation curAllocation = findBestAllocation(job, availableResources, t, entry.getKey())
        }
        
        return null;
    }

    private Allocation findBestAllocation(Job job, List<Resource> availableResources, int startTime, Integer length) {
        int endTime = startTime + length;
        
        List<ResourceAvailability> feasibleResources = availableResources.stream()
                .map(r -> new ResourceAvailability(
                        r, 
                        ProbabilityUtils.getAvailabilityProbability(r, startTime, endTime)))
                .filter(r -> 
                        (job.getPreferences().getMinAvailability() == null 
                        || r.availabilityP <= job.getPreferences().getMinAvailability()))
                .collect(Collectors.toList());
                
        if(job.getResourceRequest().getParallelNum() > feasibleResources.size()){
            return null;
        }
        
        if(settings.getSchedulingMode() == AvaSchedulerSettings.SchMode.GREEDY_SIMPLE){
            
        }
    }
    


    private void prepareJobAllocation(Job job, Allocation allocation) {
        ResourcesAllocation jobAllocation = new ResourcesAllocation();
        jobAllocation.setResources(allocation.resources);
        jobAllocation.setStartTime(allocation.startTime);
        jobAllocation.setEndTime(allocation.endTime);
        job.setResourcesAllocation(jobAllocation);
        JobController.generateEvents(job);
    }

    private TreeMap<Integer, List<Resource>> 
                            preparePerformanceOptions(ResourceDomain domain, Job job) {
                                
        Map<Integer, List<Resource>> performanceOptions = new HashMap<>(); 
        
        for(Resource r : domain.getResources()){
            Integer estimatedLength = JobController.estimateExecutionTime(job, r);
            if(performanceOptions.containsKey(estimatedLength)){
                performanceOptions.get(estimatedLength).add(r);
            }else{
                List<Resource> resources = new ArrayList<>();
                resources.add(r);
                performanceOptions.put(estimatedLength, resources);
            }
        }
        
        return new TreeMap<>(performanceOptions);
    }
}

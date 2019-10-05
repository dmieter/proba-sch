package org.dmieter.sch.prob.scheduler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.dmieter.sch.prob.job.Job;
import org.dmieter.sch.prob.job.JobController;
import org.dmieter.sch.prob.resources.Resource;
import org.dmieter.sch.prob.resources.ResourceDomain;
import org.dmieter.sch.prob.resources.ResourcesAllocation;

/**
 *
 * @author dmieter
 */
public class AvaScheduler implements Scheduler {

    AvaSchedulerSettings settings;

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
        
        for(Map.Entry<Integer, List<Resource>> entry : performanceOptions){
            availableResources.addAll(entry.getValue());
            Allocation curAllocation = findBestAllocation(job, availableResources, t, entry.getKey())
        }
    }

    @Override
    public void flush() {

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

    protected class Allocation {

        int startTime;
        int endTime;
        Double criterionValue;
        List<Resource> resources;
    }
}

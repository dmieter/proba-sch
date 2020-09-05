package org.dmieter.sch.prob.scheduler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import org.dmieter.sch.prob.ProbabilityUtils;
import org.dmieter.sch.prob.experiment.stat.NamedStats;
import org.dmieter.sch.prob.job.Job;
import org.dmieter.sch.prob.job.JobController;
import org.dmieter.sch.prob.resources.Resource;
import org.dmieter.sch.prob.resources.ResourceDomain;
import org.dmieter.sch.prob.resources.ResourcesAllocation;
import org.dmieter.sch.prob.scheduler.allocator.GreedyMaxPAllocator;
import org.dmieter.sch.prob.scheduler.allocator.GreedyMaxPLimitedAllocator;
import org.dmieter.sch.prob.scheduler.allocator.KnapsackMaxPAllocator;
import org.dmieter.sch.prob.scheduler.allocator.MinCostAllocator;
import org.dmieter.sch.prob.scheduler.allocator.ResourceAvailability;
import org.dmieter.sch.prob.scheduler.criteria.AvailableProbabilityCriterion;

/**
 *
 * @author dmieter
 */
public class AvaScheduler implements Scheduler {

    AvailableProbabilityCriterion criterionP = new AvailableProbabilityCriterion();
    AvaSchedulerSettings settings;

    private static boolean logTimelineStats = false;
    public static NamedStats schedulingTimeline = new NamedStats("SCHEDULING_SCAN_STATS");

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

        // searching for best allocation in time
        for (int t = currentTime; t < deadline; t += settings.getScanDelta()) {

            if (t % 50 == 0) {
                System.out.println("Time scanned: " + t);
            }

            Allocation curAllocation = findBestAllocation(job, domain, t, deadline);
            logSchedulingResults(curAllocation);

            if (curAllocation != null
                    && curAllocation.criterionValue > bestCriterionValue) {

                bestAllocation = curAllocation;
                bestCriterionValue = curAllocation.criterionValue;

                if (settings.getOptimizationProblem() == AvaSchedulerSettings.OptProblem.FIRST_PROBABLE) {
                    break; // we've just found the first probable allocation, break
                }
            }
        }

        if (bestAllocation != null) {
            System.out.println("Resulting probability: " + bestAllocation.criterionValue);
            prepareJobAllocation(job, bestAllocation);
            return true;
        } else {
            return false;
        }
    }

    protected Allocation findBestAllocation(Job job, ResourceDomain domain, int startTime, int deadline) {
        TreeMap<Integer, List<Resource>> performanceOptions
                = preparePerformanceOptions(domain, job);

        Double bestCriterionValue = Double.NEGATIVE_INFINITY;
        Allocation bestLocalAllocation = null;

        List<Resource> availableResources = new ArrayList<>();

        // searching for best allocation from availableResources at startTime
        for (Map.Entry<Integer, List<Resource>> entry : performanceOptions.entrySet()) {
            availableResources.addAll(entry.getValue());
            Allocation curAllocation = findBestAllocation(job, availableResources, startTime, entry.getKey());

            if (curAllocation != null
                    && curAllocation.criterionValue > bestCriterionValue) {

                bestLocalAllocation = curAllocation;
                bestCriterionValue = curAllocation.criterionValue;
            }
        }

        return bestLocalAllocation;
    }
  
    protected Allocation findBestAllocation(Job job, List<Resource> availableResources, int startTime, Integer length) {
        int endTime = startTime + length;

        // Each resource may be selected to run job (no slots), 
        // however some nodes may have high utilization probability on the considered interval
        // 0. So we perform a filtering step
        AtomicInteger counter = new AtomicInteger(0);
        List<ResourceAvailability> feasibleResources = availableResources.stream()
                .map(r -> new ResourceAvailability(
                counter.getAndIncrement(),
                r,
                ProbabilityUtils.getAvailabilityProbability(r, startTime, endTime)))
                .filter(r
                        -> (job.getPreferences().getMinAvailability() == null
                || r.availabilityP >= job.getPreferences().getMinAvailability()))
                .collect(Collectors.toList());

        if (job.getResourceRequest().getParallelNum() > feasibleResources.size()) {
            return null;  // not enough feasible nodes
        }

        
        //System.out.println(feasibleResources.size()-job.getResourceRequest().getParallelNum());
        
        // 1. Retrieveing resources allocation with maximum availability P
        List<ResourceAvailability> selectedResources = null;
        switch (settings.getSchedulingMode()) {
            case MIN_COST:
                selectedResources = MinCostAllocator.allocateResources(job, feasibleResources, startTime, endTime);
                break;
            
            case GREEDY_SIMPLE:
                selectedResources = GreedyMaxPAllocator.allocateResources(job, feasibleResources, startTime, endTime);
                break;

            case GREEDY_LIMITED:
                selectedResources = GreedyMaxPLimitedAllocator.allocateResources(job, feasibleResources, startTime, endTime);
                break;

            case KNAPSACK:
                selectedResources = KnapsackMaxPAllocator.allocateResources(job, feasibleResources, startTime, endTime);
                break;

            default:
                throw new UnsupportedOperationException(settings.getSchedulingMode() + " isn't supported by AvaScheduler");
        }

        //2. Prepare and check if we can use this allocation
        if (selectedResources != null) {
            List<Resource> candidateResources = selectedResources.stream()
                    .map(r -> r.getResource())
                    .collect(Collectors.toList());

            Double totalAvailabilityP = criterionP.getValue(candidateResources, startTime, endTime);

            if (job.getPreferences().getMinAvailability() != null
                    && totalAvailabilityP < job.getPreferences().getMinAvailability()) {
                return null; // selected resources don't satisfy MinAvailability requirement
            }

            // 3. Creating allocation
            Allocation allocation = new Allocation();
            allocation.resources = candidateResources;
            allocation.criterionValue = job.getPreferences().getCriterion().getValue(candidateResources, startTime, endTime);
            allocation.startTime = startTime;
            allocation.endTime = endTime;

            return allocation;
        }

        return null;
    }

    protected void prepareJobAllocation(Job job, Allocation allocation) {
        ResourcesAllocation jobAllocation = new ResourcesAllocation();
        jobAllocation.setResources(allocation.resources);
        jobAllocation.setStartTime(allocation.startTime);
        jobAllocation.setEndTime(allocation.endTime);
        job.setResourcesAllocation(jobAllocation);
        JobController.generateEvents(job);
    }

    protected TreeMap<Integer, List<Resource>>
            preparePerformanceOptions(ResourceDomain domain, Job job) {

        Map<Integer, List<Resource>> performanceOptions = new HashMap<>();

        for (Resource r : domain.getResources()) {
            Integer estimatedLength = JobController.estimateExecutionTime(job, r);
            if (performanceOptions.containsKey(estimatedLength)) {
                performanceOptions.get(estimatedLength).add(r);
            } else {
                List<Resource> resources = new ArrayList<>();
                resources.add(r);
                performanceOptions.put(estimatedLength, resources);
            }
        }

        return new TreeMap<>(performanceOptions);
    }

    protected void logSchedulingResults(Allocation allocation) {
        if (logTimelineStats) {
            if (allocation == null || allocation.criterionValue == null) {
                AvaScheduler.schedulingTimeline.addValue(settings.getSchedulingMode().name(), 0d);
            } else {
                AvaScheduler.schedulingTimeline.addValue(settings.getSchedulingMode().name(), allocation.criterionValue);
            }
        }
    }

    protected class Allocation {

        int startTime;
        int endTime;
        Double criterionValue;
        List<Resource> resources;
        
        public Double getCriterionValue(){
            return criterionValue;
        }
    }
}

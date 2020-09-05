package org.dmieter.sch.prob.scheduler;

import java.util.HashMap;
import java.util.Map;
import org.dmieter.sch.prob.job.Job;
import org.dmieter.sch.prob.resources.ResourceDomain;

/**
 *
 * @author dmieter
 */
public class AvaSchedulerOpt extends AvaScheduler {

    int startPoints = 30;
    int delta = 10; // nothing special could be in 10 time units

    @Override
    protected boolean scheduleScan(Job job, ResourceDomain domain, int currentTime) {

        Integer deadline = job.getPreferences().getDeadline();

        Map<Integer, Allocation> startPoints = new HashMap<>();
        Map<Integer, Allocation> knownPoints = new HashMap<>();
        Map<Integer, Allocation> maxPoints = new HashMap<>();
        Integer step = Math.round((deadline - currentTime) / this.startPoints);

        for (int t = currentTime; t < deadline; t += step) {

            Allocation nextAllocation = findBestAllocation(job, domain, t, deadline);

            if (nextAllocation != null) {
                startPoints.put(t, nextAllocation);
            }
        }
        
        knownPoints.putAll(startPoints);
        
        for(Map.Entry<Integer, Allocation> searchTime : startPoints.entrySet()){
            findNextMaximum(searchTime.getKey(), deadline, job, domain, knownPoints, maxPoints, 50);
        }

        if(maxPoints.isEmpty()){
            return false;
        }
        
        Allocation maxAllocation = null;
        
        System.out.println("Resulting probability: " + maxAllocation.criterionValue);
        prepareJobAllocation(job, maxAllocation);
        return true;
    }

    protected Allocation findNextMaximum(Integer startTime, Integer deadline, Job job, ResourceDomain domain,
            Map<Integer, Allocation> knownPoints, Map<Integer, Allocation> maxPoints, int searchDiameter) {

        Allocation leftAllocation = getAllocationByTime(startTime, deadline, job, domain, knownPoints);

        int direction = getMaxDirection(startTime, deadline, job, domain, knownPoints);
        
        if(direction == 0){ // we have local maximum at startTime
            maxPoints.put(startTime, leftAllocation);
            return leftAllocation;
        }

        // search
        for (int t = delta; t <= searchDiameter; t += delta) {

            Allocation rightAllocation = findBestAllocation(job, domain, startTime + direction*t, deadline);

            if (rightAllocation.criterionValue > leftAllocation.criterionValue) {
                leftAllocation = rightAllocation;
                continue;  // continue going to maximum
            } else {
                Allocation maxAllocation = findMaximumInInterval(leftAllocation, rightAllocation, job, domain, deadline, knownPoints);
                if(maxAllocation == null) {
                    throw new IllegalStateException("Unable to find middle maximum");
                }
                knownPoints.put(maxAllocation.startTime, maxAllocation);
                maxPoints.put(maxAllocation.startTime, maxAllocation);
                
                return true;
            }

        }

        return false;
    }
    
    protected int getMaxDirection(int middlePoint, Integer deadline, Job job, ResourceDomain domain, Map<Integer, Allocation> knownPoints){
        
        Allocation middleAllocation = getAllocationByTime(middlePoint, deadline, job, domain, knownPoints);
        Allocation leftAllocation = getAllocationByTime(middlePoint - 1, deadline, job, domain, knownPoints);
        Allocation rightAllocation = getAllocationByTime(middlePoint + 1, deadline, job, domain, knownPoints);
        
        if(rightAllocation.criterionValue > middleAllocation.criterionValue){
            if(rightAllocation.criterionValue >= leftAllocation.criterionValue){
                return 1; // go to right
            } else {
                return -1; // go to left
            }
        } else if (leftAllocation.criterionValue > middleAllocation.criterionValue){
            return -1; // go to left
        } else {
            return 0; // middle is max
        }
        
    }
    
    protected Allocation getAllocationByTime(int timePoint, Integer deadline, Job job, ResourceDomain domain, Map<Integer, Allocation> knownPoints) {
        if(knownPoints.containsKey(timePoint)){
            return knownPoints.get(timePoint);
        } else {
            Allocation newAllocation = findBestAllocation(job, domain, timePoint, deadline);
            knownPoints.put(timePoint, newAllocation);
            return newAllocation;
        }
    }

    protected Allocation findMaximumInInterval(Allocation leftAllocation, Allocation rightAllocation,
            Job job, ResourceDomain domain, Integer deadline, Map<Integer, Allocation> knownPoints) {

        int middleTime = getMiddleTime(leftAllocation.startTime, rightAllocation.startTime);
        Allocation middleAllocation = getAllocationByTime(middleTime, deadline, job, domain, knownPoints);
        
        int direction = getMaxDirection(middleTime, deadline, job, domain, knownPoints);
        
        switch (direction) {
            case 0:
                return middleAllocation;
            case 1:
                return findMaximumInInterval(middleAllocation, rightAllocation, job, domain, deadline, knownPoints);
            case -1:
                return findMaximumInInterval(leftAllocation, middleAllocation, job, domain, deadline, knownPoints);
            default:
                throw new IllegalStateException("Unexpected direction: " + direction);
        }
        

    }

    private Integer getMiddleTime(Integer leftTime, Integer rightTime) {
        return leftTime + Math.round((rightTime - leftTime) / 2);
    }
}

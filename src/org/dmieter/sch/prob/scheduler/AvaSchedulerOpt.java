package org.dmieter.sch.prob.scheduler;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import org.dmieter.sch.prob.job.Job;
import org.dmieter.sch.prob.resources.ResourceDomain;
import project.math.utils.MathUtils;

/**
 *
 * @author dmieter
 */
public class AvaSchedulerOpt extends AvaScheduler {

    public int startPoints = 30;
    int delta = 10; // nothing special could be in 10 time units

    @Override
    protected boolean scheduleScan(Job job, ResourceDomain domain, int currentTime) {

        Integer deadline = job.getPreferences().getDeadline();

        Map<Integer, Allocation> startPoints = new HashMap<>();
        Map<Integer, Allocation> knownPoints = new HashMap<>();
        Map<Integer, Allocation> maxPoints = new TreeMap<>();
        int searchRadius = MathUtils.intNextUp((deadline - currentTime) / (this.startPoints * 1.5));

        for (int i = 0; i < this.startPoints; i++) {
            int t = MathUtils.getUniform(currentTime, deadline);
            Allocation nextAllocation = findBestAllocation(job, domain, t, deadline);

            if (nextAllocation != null) {
                startPoints.put(t, nextAllocation);
            }
        }

        knownPoints.putAll(startPoints);

        for (Map.Entry<Integer, Allocation> searchTime : startPoints.entrySet()) {
            findNextMaximum(searchTime.getKey(), deadline, job, domain, knownPoints, maxPoints, searchRadius);
        }

        if (maxPoints.isEmpty()) {
            return false;
        }

        // retrieveing maximum from all local maximum points        
        Allocation maxAllocation = maxPoints.values().iterator().next();
        System.out.println("Local maximums:");
        for (Map.Entry<Integer, Allocation> result : maxPoints.entrySet()) {
            System.out.println(result.getKey() + ": " + result.getValue().criterionValue);
            if (result.getValue().criterionValue > maxAllocation.criterionValue) {
                maxAllocation = result.getValue();
            }
        }
        System.out.println("Number of known points: " + knownPoints.size());

        System.out.println("Resulting probability: " + maxAllocation.criterionValue + " at " + maxAllocation.startTime);

        if (maxAllocation.criterionValue > Double.MIN_VALUE) {

            prepareJobAllocation(job, maxAllocation);
            return true;
        } else {
            return true;
        }

    }

    protected boolean findNextMaximum(Integer startTime, Integer deadline, Job job, ResourceDomain domain,
            Map<Integer, Allocation> knownPoints, Map<Integer, Allocation> maxPoints, int searchRadius) {

        Allocation leftAllocation = getAllocationByTime(startTime, deadline, job, domain, knownPoints);

        int direction = getMaxDirection(startTime, deadline, job, domain, knownPoints);

        if (direction == 0) { // we have local maximum at startTime
            maxPoints.put(startTime, leftAllocation);
            return true;
        }

        // search
        for (int t = delta; t <= searchRadius; t += delta) {

            Allocation rightAllocation = getAllocationByTime(startTime + direction * t, deadline, job, domain, knownPoints);

            if (rightAllocation.criterionValue > leftAllocation.criterionValue) {
                leftAllocation = rightAllocation;
                continue;  // continue going to maximum
            } else {
                Allocation maxAllocation = null;

                // we need to provide real left and right allocations as an interval
                if (direction > 0) {
                    maxAllocation = findMaximumInInterval(leftAllocation, rightAllocation, job, domain, deadline, knownPoints);
                } else {
                    maxAllocation = findMaximumInInterval(rightAllocation, leftAllocation, job, domain, deadline, knownPoints);
                }

                if (maxAllocation == null) {
                    throw new IllegalStateException("Unable to find middle maximum");
                }
                maxPoints.put(maxAllocation.startTime, maxAllocation);

                return true;
            }
        }

        // if there is no maximum in the interval - return interval edge as maximum
        maxPoints.put(leftAllocation.startTime, leftAllocation);
        return true;
    }

    protected int getMaxDirection(int middlePoint, Integer deadline, Job job, ResourceDomain domain, Map<Integer, Allocation> knownPoints) {

        Allocation middleAllocation = getAllocationByTime(middlePoint, deadline, job, domain, knownPoints);
        Allocation leftAllocation = getAllocationByTime(middlePoint - 1, deadline, job, domain, knownPoints);
        Allocation rightAllocation = getAllocationByTime(middlePoint + 1, deadline, job, domain, knownPoints);

        if (rightAllocation.criterionValue > middleAllocation.criterionValue) {
            if (rightAllocation.criterionValue >= leftAllocation.criterionValue) {
                return 1; // go to right
            } else {
                return -1; // go to left
            }
        } else if (leftAllocation.criterionValue > middleAllocation.criterionValue) {
            return -1; // go to left
        } else {
            return 0; // middle is max
        }

    }

    protected Allocation getAllocationByTime(int timePoint, Integer deadline, Job job, ResourceDomain domain, Map<Integer, Allocation> knownPoints) {
        if (knownPoints.containsKey(timePoint)) {
            return knownPoints.get(timePoint);
        } else {
            Allocation newAllocation = findBestAllocation(job, domain, timePoint, deadline);
            if (newAllocation == null) {
                newAllocation = new Allocation();
                newAllocation.criterionValue = Double.NEGATIVE_INFINITY;
            }
            knownPoints.put(timePoint, newAllocation);
            return newAllocation;
        }
    }

    protected Allocation findMaximumInInterval(Allocation leftAllocation, Allocation rightAllocation,
            Job job, ResourceDomain domain, Integer deadline, Map<Integer, Allocation> knownPoints) {

        System.out.println("findMaximumInInterval " + leftAllocation.startTime + " - " + rightAllocation.startTime);

        // check if search interval is really small
        if (Math.abs(rightAllocation.startTime - leftAllocation.startTime) <= 1) {
            if (leftAllocation.criterionValue >= rightAllocation.criterionValue) {
                return leftAllocation;
            } else {
                return rightAllocation;
            }
        }

        int middleTime = getMiddleTime(leftAllocation.startTime, rightAllocation.startTime);
        //System.out.println(middleTime + ": " + leftAllocation.startTime + " and " + rightAllocation.startTime);
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
        System.out.println(leftTime + " - " + rightTime + " : " + (leftTime + Math.round((rightTime - leftTime) / 2)));
        return leftTime + Math.round((rightTime - leftTime) / 2);
    }
}

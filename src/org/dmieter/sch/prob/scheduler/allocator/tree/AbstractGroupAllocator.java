package org.dmieter.sch.prob.scheduler.allocator.tree;

import org.dmieter.sch.prob.job.Job;
import org.dmieter.sch.prob.scheduler.allocator.ResourceAvailability;
import org.dmieter.sch.prob.scheduler.allocator.ResourceAvailabilityGroup;
import org.dmieter.sch.prob.scheduler.allocator.ResourceAvailabilityPriced;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class AbstractGroupAllocator {

    protected static List<ResourceAvailabilityGroup> estimateResourcesCost(
            List<ResourceAvailabilityGroup> groups, Integer startTime, Integer endTime) {

        return groups.stream()
                .map(group -> group.copyWithCostEstimate(startTime, endTime))
                .collect(Collectors.toList());
    }

    public static String explainProblem(Job job, List<ResourceAvailabilityGroup> resourceGroups, List<ResourceAvailability> solution, Integer startTime, Integer endTime) {
        StringBuilder explanation = new StringBuilder("Input:");
        if(job != null) {
            explanation.append("\nJob Cost Limit: ").append(job.getResourceRequest().getBudget());
        }

        for(ResourceAvailabilityGroup g : resourceGroups) {
            explanation.append(String.format("\nGroup %s P: %f of %d resources:", g.getOrderNum(), g.getAvailabilityP(), g.getResources().size()));
            for(ResourceAvailability r : g.getResources()) {
                explanation.append("\n\t")
                        .append(String.format("Resource %s cost %f", r.orderNum, r.getResource().estimateUsageCost(startTime, endTime)));
            }
        }

        if(solution != null) {
            Double totalCost = 0d;

            explanation.append("\nOutput:");
            for (ResourceAvailability r : solution) {
                Double resourceCost = r.getResource().estimateUsageCost(startTime, endTime);
                totalCost += resourceCost;
                explanation.append("\n").append(String.format("Resource %s (%s) cost %f P: %f", r.orderNum, r.group.orderNum, resourceCost, r.getAvailabilityP()));
            }
            explanation.append("\nTotal P:").append(calculateTotalProbability(solution));
            explanation.append("\nTotal C:").append(totalCost).append("/").append(job.getResourceRequest().getBudget());
        }

        return explanation.append("\n").toString();
    }

    public static Double calculateTotalProbability(List<ResourceAvailability> solution) {
        Set<ResourceAvailabilityGroup> distinctGroups = solution.stream()
                .map(r -> r.group)
                .collect(Collectors.toSet());

        return distinctGroups.stream()
                .map(g -> g.getAvailabilityP())
                .reduce((p1,p2) -> p1*p2).get();
    }

    public static SolutionStats estimateSolution(Job job, List<ResourceAvailability> solution, Integer startTime, Integer endTime) {

        return estimateSolution(job,
                solution.stream()
                .map(ra -> new ResourceAvailabilityPriced(ra, ra.group, ra.resource.estimateUsageCost(startTime, endTime)))
                .collect(Collectors.toList())
        );
    }

    protected static SolutionStats estimateSolution(Job job, List<ResourceAvailabilityPriced> solution) {

        SolutionStats result = new SolutionStats(false, 1d, 0d);
        for(ResourceAvailabilityPriced ra : solution) {
            result.totalCost += ra.getCost();
        }

        result.probability = calculateTotalProbability(solution.stream().map(r -> (ResourceAvailability)r).collect(Collectors.toList()));

        result.isFeasible = result.totalCost <= job.getResourceRequest().getBudget();

        return result;
    }

    public static class SolutionStats {

        public Boolean isFeasible;
        public Double probability;
        public Double totalCost;

        public List<ResourceAvailabilityPriced> solution;

        public SolutionStats(Boolean isFeasible, Double probability, Double totalCost) {
            this.isFeasible = isFeasible;
            this.probability = probability;
            this.totalCost = totalCost;
        }
    }

}

package org.dmieter.sch.prob.scheduler.allocator.tree;

import com.dmieter.algorithm.opt.knapsack.data.DPEntity;
import com.dmieter.algorithm.opt.knapsack.knapsack01.multiweights.FixedItemsNumberKnapsackProblem;
import com.dmieter.algorithm.opt.knapsack.knapsack01.multiweights.IntervalItemsNumberKnapsackProblem;
import org.dmieter.sch.prob.job.Job;
import org.dmieter.sch.prob.scheduler.allocator.ResourceAvailability;
import org.dmieter.sch.prob.scheduler.allocator.ResourceAvailabilityGroup;
import org.dmieter.sch.prob.scheduler.allocator.ResourceAvailabilityPriced;

import java.util.*;
import java.util.stream.Collectors;

public class GroupKnapsackAllocator extends AbstractGroupAllocator {

    public static List<ResourceAvailability> allocateResources(
            Job job,
            List<ResourceAvailabilityGroup> resourceGroups,
            Integer startTime,
            Integer endTime) {

        return allocatePricedResources(
                job,
                estimateResourcesCost(resourceGroups, startTime, endTime),
                startTime,
                endTime);
    }

    public static List<ResourceAvailability> allocatePricedResources (
            Job job,
            List<ResourceAvailabilityGroup> resourceGroups,
            Integer startTime,
            Integer endTime) {

        int n = resourceGroups.size();
        GroupItem dp[][][] = new GroupItem[job.getResourceRequest().getBudget() + 1][n + 1][job.getResourceRequest().getParallelNum() + 1];

        initDPTable(dp, job, n);

        /* running forward induction */
        List<ResourceAvailability> resultingSolution = forwardInduction(dp, job, resourceGroups);
        return resultingSolution;

    }


    protected static List<ResourceAvailability> forwardInduction(GroupItem[][][] dp, Job job, List<ResourceAvailabilityGroup> resourceGroups) {

        int j = 0;
        int maxWeight = job.getResourceRequest().getBudget();
        int itemsRequiredNumber = job.getResourceRequest().getParallelNum();
        /* going through all resource groups items as in common 01 problem starting j from 1 (as 0 contains edge value) */
        for (ResourceAvailabilityGroup resourceGroup : resourceGroups) {
            // 0. Start processing next group item
            j++;

            // 1. copy all previous tier (group item) results to current tier (after that we are left to just update/improve some of them if needed)
            for (int w = 1; w <= maxWeight; w++) {
                for (int k = 1; k <= itemsRequiredNumber; k++) {
                    if(dp[w][j - 1][k] != null) {
                        dp[w][j][k] = new GroupItem(dp[w][j - 1][k]);
                    }
                }
            }

            // 2. getting and iterate over (k -> (v,w)) variants from groupitem subproblem
            List<GroupItem> itemVariants = generateResourceGroupVariants(resourceGroup, itemsRequiredNumber, maxWeight);
            if (itemVariants == null || itemVariants.isEmpty()) {
                continue;   //to next group item, as this group item doesnt provide any variants
            }

            for(GroupItem itemVariant : itemVariants) {

                /* going through all possible weights as in common 01 problem */
                for (int w = itemVariant.weight; w <= maxWeight; w++) {

                    for (int k = itemVariant.amount; k <= itemsRequiredNumber; k++) {

                        GroupItem curEntity1 = dp[w][j][k];  // we already copied it from item j-1 and possibly updated for previous k values
                        GroupItem prevEntity2 = dp[w - itemVariant.weight][j - 1][k - itemVariant.amount]; // check

                        /* if both are null - then there's no solution for current w, n, k, for example, k > 2 for first item
                        the same holds for all bigger ks, so may be break here */
                        if (curEntity1 == null && prevEntity2 == null) {
                            continue; // break, not possible to update for current item
                        }

                        double curItemValue = Double.NEGATIVE_INFINITY;
                        if (curEntity1 != null) {
                            curItemValue = curEntity1.value;
                        }

                        double addedItemValue = Double.NEGATIVE_INFINITY;
                        if (prevEntity2 != null) {
                            addedItemValue = prevEntity2.value * itemVariant.value;
                        }

                        if (curItemValue >= addedItemValue) {
                            // do nothing
                        } else {
                            dp[w][j][k] = new GroupItem(prevEntity2, itemVariant);   // merging two group items into one
                            /* here j-1 - reference to item number inside items list
                                                                                    to know what group item to use during the backward run at the current stage*/
                        }

                    }

                }
            }
        }

        /* solution value */
        GroupItem solution = dp[maxWeight][resourceGroups.size()][itemsRequiredNumber];
        return solution != null ? solution.resources : null;
    }

    protected Double getMaxValue(DPEntity[][][] dp, FixedItemsNumberKnapsackProblem problem, int n) {

        IntervalItemsNumberKnapsackProblem intervalProblem = (IntervalItemsNumberKnapsackProblem) problem;

        Double maxValue = null;
        for (int itemsNumber = intervalProblem.getMinItemsNumber(); itemsNumber <= intervalProblem.getMaxItemsNumber(); itemsNumber++) {

            if (dp[problem.getMaxWeight()][n][itemsNumber] != null) {
                Double value = dp[problem.getMaxWeight()][n][itemsNumber].value;
                if (maxValue == null || maxValue < value) {
                    maxValue = value;
                }
            }
        }

        return maxValue;

    }

    protected static void initDPTable(GroupItem[][][] dp, Job job, int n) {

        /* standard init with zeroes */
        for (int w = 0; w <= job.getResourceRequest().getBudget(); w++) {
            for (int j = 0; j <= n; j++) {
                dp[w][j][0] = new GroupItem(0, 0, 1d, Collections.EMPTY_LIST);  // zero out init conditions, starting value is 1
            }
        }
    }

    /** we can return any amount of resources providing the same total availablity */
    protected static List<GroupItem> generateResourceGroupVariants(ResourceAvailabilityGroup resourceGroup, Integer maxItems, Integer maxCost) {
        List<ResourceAvailabilityPriced> sortedResources = resourceGroup.getResources().stream()
                .map(ResourceAvailabilityPriced.class::cast)
                .sorted(Comparator.comparing(ResourceAvailabilityPriced::getCost))
                .collect(Collectors.toList());

        List<GroupItem> variants = new ArrayList<>();

        List<ResourceAvailability> addedResources = new ArrayList<>();
        Integer sumCost = 0;
        for(ResourceAvailabilityPriced resource : sortedResources) {
            addedResources.add(resource);
            sumCost += resource.getCost().intValue();
            if(addedResources.size() <= maxItems && sumCost <= maxCost) {
                variants.add(new GroupItem(addedResources.size(), sumCost, resource.getGroup().getAvailabilityP(), addedResources));
            } else {
                break;
            }
        }

        return variants;

    }

    static class GroupItem {
        ResourceAvailabilityGroup relatedGroup;
        public Integer amount;
        public Integer weight;
        public Double value;
        List<ResourceAvailability> resources;

        public GroupItem(Integer itemAmount, Integer itemWeight, Double itemValue, List<ResourceAvailability> resources) {
            amount = itemAmount;
            weight = itemWeight;
            value = itemValue;

            this.resources = new ArrayList<>(resources);
        }

        public GroupItem(GroupItem g) {
            amount = g.amount;
            weight = g.weight;
            value = g.value;
            resources = new ArrayList<>(g.resources);
        }

        public GroupItem(GroupItem g1, GroupItem g2) {
            amount = g1.amount + g2.amount;
            weight = g1.weight + g2.weight;
            value = g1.value * g2.value;
            resources = new ArrayList<>(g1.resources);
            resources.addAll(g2.resources);
        }
    }

}

package org.dmieter.sch.prob.scheduler.allocator.tree;

import org.dmieter.sch.prob.scheduler.allocator.ResourceAvailability;
import org.dmieter.sch.prob.scheduler.allocator.ResourceAvailabilityGroup;

import java.util.ArrayList;
import java.util.List;

public class Node {
    public List<ResourceAvailabilityGroup> excludedGroups = new ArrayList<>();
    public List<ResourceAvailabilityGroup> includedGroups = new ArrayList<>();
    public List<ResourceAvailability> solution = new ArrayList<>();

    public ResourceAvailabilityGroup splitGroup;

    public Double upperEstinate = Double.NEGATIVE_INFINITY;

    public Node copy() {
        Node newNode = new Node();
        newNode.includedGroups.addAll(includedGroups);
        newNode.excludedGroups.addAll(excludedGroups);

        return newNode;
    }
}

package org.dmieter.sch.prob.scheduler.allocator;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.dmieter.sch.prob.resources.Resource;

/**
 *
 * @author dmieter
 */

public class Allocation {

    public int startTime;
    public int endTime;
    public Double criterionValue;
    public List<Resource> resources;
}

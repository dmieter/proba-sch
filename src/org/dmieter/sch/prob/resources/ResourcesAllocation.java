package org.dmieter.sch.prob.resources;

import java.util.LinkedList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.dmieter.sch.prob.events.Event;

/**
 *
 * @author dmieter
 */

@Getter
@Setter
public class ResourcesAllocation {
    private Integer startTime;
    private Integer endTime;
    
    private List<Resource> resources = new LinkedList<>();
    
}

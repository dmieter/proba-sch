
package org.dmieter.sch.prob.generator;

import java.util.Optional;
import org.apache.commons.math3.geometry.euclidean.oned.Interval;
import org.dmieter.sch.prob.events.Event;
import org.dmieter.sch.prob.events.EventType;
import org.dmieter.sch.prob.resources.Resource;
import org.dmieter.sch.prob.resources.ResourceDomain;
import org.dmieter.sch.prob.resources.ResourcesAllocation;
import project.math.distributions.DistributionGenerator;
import project.math.distributions.UniformFacade;
import project.math.utils.MathUtils;

/**
 *
 * @author emelyanov
 */
public class UtilizationGenerator extends Generator {
    
    public Interval intLoad;
    public Interval intJobLength;
    public Interval intStartVariability;
    public Interval intFinishVariability;
    
    public void generateUtilization(ResourceDomain domain, Interval timeInterval){
        
    }
    
    private void generateResourceUtilization(Resource resource, Interval timeInterval){
        Double load = getUniformFromInterval(intLoad);
        
        
        Double sumJobsLength = 0d;
        
        
        final int projStartTime = getUniformIntFromInterval(timeInterval);
        int startTime = projStartTime;
        int jobLength = getUniformIntFromInterval(intJobLength);
        int finishTime = projStartTime + jobLength;
        
        // searching for the nearest previous start/finish event
        Optional<Event> prevEvent = resource.getActiveEvents(startTime, finishTime)Events().stream()
                .filter(e -> e.getEventTime() <= projStartTime)
                .filter(e -> EventType.RELEASING_RESOURCE.equals(e.getType()) || EventType.ALLOCATING_RESOURCE.equals(e.getType()))
                .max((e1,e2) -> e1.getEventTime().compareTo(e2.getEventTime()));
        
       
        
        // searching for the nearest to startTime resources allocation
        
    }
    

}

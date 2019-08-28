
package org.dmieter.sch.prob.generator;

import java.util.List;
import java.util.Optional;
import org.apache.commons.math3.geometry.euclidean.oned.Interval;
import org.dmieter.sch.prob.SchedulingController;
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
        
        int cnt = 0;
        while (sumJobsLength / timeInterval.getSize() < load) {
            sumJobsLength += generateAndAssignJob(resource, timeInterval);
            if (cnt++ > 100) {
                System.err.println("Can't reach " + load + " utilization for " + resource.getId());
                break;
            }
        }
        
    }
    
    private int generateAndAssignJob(Resource resource, Interval timeInterval){
        
        int startTime = getUniformIntFromInterval(timeInterval);
        int jobLength = getUniformIntFromInterval(intJobLength);
        
        List<Event> events = resource.getActiveEvents(startTime, Integer.MAX_VALUE);
        if(events.isEmpty()){
            return assignJob(resource, startTime, jobLength);
        }

        // 1. Check if we can start job at initial start time
            Optional<Event> nextFinishEvent = SchedulingController.getNextEvent(startTime, resource, EventType.GENERAL);
            
        // 2. if not - in while find next finish event and start event and check distance between them
    }
    
    private int assignJob(Resource resource, int startTime, int jobLength){
        return jobLength;
    }
    

}


package org.dmieter.sch.prob;

import java.util.List;
import org.dmieter.sch.prob.events.Event;
import org.dmieter.sch.prob.resources.Resource;
import project.math.utils.MathUtils;

/**
 *
 * @author emelyanov
 */
public class ProbabilityUtils {
    
    public static Double getAvailabilityProbability(Resource resource, int startTime, int endTime){
        List<Event> events = resource.getActiveEvents(startTime, endTime);
        return getAvailabilityProbability(events, startTime, endTime);
    }
    
    public static Double getAvailabilityProbability(List<Event> events, int startTime, int endTime){
        
        if(events.isEmpty()){
            // no events -> resources are available with P = 1
            return 1d;
        }

        // multiplying probability that resources are available from each event
        return events.stream().
            map(e -> getAvailabilityProbability(e, startTime, endTime))
            .reduce(1d, (a,b)->(a*b));
    }
    
    public static Double getAvailabilityProbability(Event event, int startTime, int endTime){
        //we calculate min availability probability for interval ends (because distributions are monotone)
        return Math.min(event.getResourcesAvailableP(startTime), event.getResourcesAvailableP(endTime));
    }
}

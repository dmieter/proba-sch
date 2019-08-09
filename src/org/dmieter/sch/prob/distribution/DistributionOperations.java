package org.dmieter.sch.prob.distribution;

import java.util.List;

/**
 *
 * @author emelyanov
 */
public class DistributionOperations {
    public static Float getAvailabilityP(Distribution d, Integer startTime, Integer endTime){
        // should return min value on the interval (as such distributions are monotone - only edge values should be checked)
        throw new UnsupportedOperationException();
    }
    
    public static Float getAggregatedAvailabilityP(List<Distribution> distributions, Integer startTime, Integer endTime){
        
        // just multiplying events availability probabilities
        return distributions.stream()
                .map(d -> getAvailabilityP(d, startTime, endTime))
                .reduce(1f, (accumulatorP, localP) -> accumulatorP * localP);
        
    }
}

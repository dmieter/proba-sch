package org.dmieter.sch.prob.generator;

import org.apache.commons.math3.geometry.euclidean.oned.Interval;
import project.math.utils.MathUtils;

/**
 *
 * @author emelyanov
 */
public class Generator {

    protected Double getMutatedIntervalValue(Interval interval, double baseFactor, double mutationFactor) {
        return interval.getInf() + interval.getSize() * baseFactor * mutationFactor;
    }
        
    protected Double getUniformFromInterval(Interval interval){
        return MathUtils.getUniform(interval.getInf(), interval.getSup());
    }
    
    protected Integer getUniformIntFromInterval(Interval interval){
        return MathUtils.getUniform(Double.valueOf(interval.getInf()).intValue(), Double.valueOf(interval.getSup()).intValue());
    }
}

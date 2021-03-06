package org.dmieter.sch.prob.distribution;

import org.apache.commons.math3.distribution.NormalDistribution;

/**
 *
 * @author emelyanov
 */
public class NormalEventDistribution extends Distribution {

    private NormalDistribution distribution;
    protected Double mean;
    protected Double sd;

    public NormalEventDistribution(Double mean, Double sd) {
        distribution = new NormalDistribution(mean, sd);
        this.mean = mean;
        this.sd = sd;
    }

    protected void updateDistribution(){
        distribution = new NormalDistribution(mean, sd);
    }
    
    @Override
    public Double getProbability(Integer t) {
        return distribution.cumulativeProbability(t);
    }
    
    @Override
    public Double getSampleValue() {
        return distribution.sample();
    }
    
    @Override
    public void setMean(Double mean) {
        this.mean = mean;
        updateDistribution();
    }
    
    @Override
    public void shiftMean(Double shiftValue) {
        this.mean += shiftValue;
        updateDistribution();
    }
    
    @Override
    public void updateVariability(Double coef) {
        this.sd *= coef;
        updateDistribution();
    }

    /**
     * @return the mean
     */
    public Double getMean() {
        return mean;
    }

    /**
     * @return the sd
     */
    public Double getSd() {
        return sd;
    }

    @Override
    public Distribution copy() {
        return new NormalEventDistribution(mean, sd);
    }

}

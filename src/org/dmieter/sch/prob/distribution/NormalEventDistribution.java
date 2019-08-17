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
    }

    @Override
    public Double getProbability(Integer t) {
        return distribution.cumulativeProbability(t);
    }

    @Override
    public void setMean(Double mean) {
        this.mean = mean;
        distribution = new NormalDistribution(mean, sd);
    }
    
    @Override
    public void shiftMean(Double shiftValue) {
        this.mean += shiftValue;
        distribution = new NormalDistribution(mean, sd);
    }
    
    @Override
    public void updateVariability(Double coef) {
        this.sd *= coef;
        distribution = new NormalDistribution(mean, sd);
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
}

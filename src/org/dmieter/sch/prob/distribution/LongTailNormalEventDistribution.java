package org.dmieter.sch.prob.distribution;

import org.apache.commons.math3.distribution.LogNormalDistribution;

/**
 *
 * @author dmieter
 */
public class LongTailNormalEventDistribution extends NormalEventDistribution {

    /*
 Parameters: X is log-normally distributed if its natural logarithm log(X) is normally distributed. The probability distribution function of X is given by (for x > 0)

exp(-0.5 * ((ln(x) - m) / s)^2) / (s * sqrt(2 * pi) * x)

m is the scale parameter: this is the mean of the normally distributed natural logarithm of this distribution,
s is the shape parameter: this is the standard deviation of the normally distributed natural logarithm of this distribution.
    
     */
    private LogNormalDistribution distribution;

    public LongTailNormalEventDistribution(Double mean, Double sd) {
        super(mean, sd);
        updateDistribution();
    }

    protected void updateDistribution() {
        distribution = new LogNormalDistribution(1, Math.log(sd));
    }

    @Override
    public Double getProbability(Integer t) {
        if(t <= mean) {
            return super.getProbability(t);
        } else {
            return distribution.cumulativeProbability(t - mean + 2);
        }
    }

    @Override
    public Double getSampleValue() {
        return distribution.sample() + mean - 1;
    }

    @Override
    public Distribution copy() {

        return new LongTailNormalEventDistribution(mean, sd);

    }

}

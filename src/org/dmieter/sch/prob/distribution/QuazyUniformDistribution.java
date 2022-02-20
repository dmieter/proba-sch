package org.dmieter.sch.prob.distribution;

/**
 *
 * @author emelyanov
 */
public class QuazyUniformDistribution extends Distribution {

    private Double uniformLevel = 0d;

    public QuazyUniformDistribution(Double level) {
        uniformLevel = level;
    }

    // returns probaility P that Event is after time t
    @Override
    public Double getProbability(Integer t) {
        return uniformLevel;
    }

    @Override
    public Double getSampleValue() {
        return Double.NEGATIVE_INFINITY;
    }

    @Override
    public void setMean(Double mean) {
        // do nothing
    }

    @Override
    public void updateVariability(Double coef) {
        // do nothing
    }

    @Override
    public void shiftMean(Double shiftValue) {
        // do nothing
    }

    @Override
    public Distribution copy() {
        return new QuazyUniformDistribution(uniformLevel);
    }
}

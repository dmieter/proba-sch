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
}

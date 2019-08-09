
package org.dmieter.sch.prob.distribution;

/**
 *
 * @author emelyanov
 */
public class QuazyUniformDistribution extends Distribution {
    
    private Float uniformLevel = 0f;
    private 
    
    public QuazyUniformDistribution(Float level){
        uniformLevel = level;
    }
    
    // returns probaility P that Event is after time t
    public Float getProbability(Integer t){
        return uniformLevel;
    }
}

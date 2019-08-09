
package org.dmieter.sch.prob.distribution;

/**
 *
 * @author emelyanov
 */
public class QuazyUniformDistribution extends Distribution {
    
    private Double uniformLevel = 0d;
    
    public QuazyUniformDistribution(Double level){
        uniformLevel = level;
    }
    
    public QuazyUniformDistribution(Double level, Integer startTime, Integer endTime){
        uniformLevel = level;
        this.startTime = startTime;
        this.endTime = endTime;
    }
    
    // returns probaility P that Event is after time t
    @Override
    public Double getProbability(Integer t){
        if(ifCorrectTime(t)){
            return uniformLevel;
        }else{
            return 0d;
        }
    }
}

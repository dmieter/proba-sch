
package org.dmieter.sch.prob.distribution;

/**
 *
 * @author emelyanov
 */

public abstract class Distribution {
    
    public Distribution(){
        
    }
    
    public abstract void setMean(Double mean);
    public abstract void shiftMean(Double shiftValue);
    public abstract void updateVariability(Double coef);
    
    // returns probaility P that Event is finished before time t
    abstract public Double getProbability(Integer t);

    
}

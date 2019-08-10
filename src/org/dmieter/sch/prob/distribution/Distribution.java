
package org.dmieter.sch.prob.distribution;

/**
 *
 * @author emelyanov
 */

public abstract class Distribution {
    
    public Distribution(){
        
    }
    
    // returns probaility P that Event is finished before time t
    abstract public Double getProbability(Integer t);

    
}

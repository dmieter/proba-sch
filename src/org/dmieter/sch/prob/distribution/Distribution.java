
package org.dmieter.sch.prob.distribution;

/**
 *
 * @author emelyanov
 */
public abstract class Distribution {
    
    // returns probaility P that Event is after time t
    abstract public Float getProbability(Integer t);
}

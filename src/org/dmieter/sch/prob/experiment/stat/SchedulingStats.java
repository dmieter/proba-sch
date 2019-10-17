
package org.dmieter.sch.prob.experiment.stat;

/**
 *
 * @author emelyanov
 */
public abstract class SchedulingStats {
    protected NamedStats stats;
    protected Long inputCount = 0l;
    
    public SchedulingStats(String statsName){
        stats = new NamedStats(statsName);
    }
    
    public void clear(){
        stats.clearStats();
        inputCount = 0l;
    }
    
    public abstract String getData();
    
}

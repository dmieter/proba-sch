
package org.dmieter.sch.prob.distribution;

/**
 *
 * @author emelyanov
 */

public abstract class Distribution {
    
    protected Integer startTime;
    protected Integer endTime;
    
    public Distribution(){
        
    }
    
    public Distribution(Integer startTime, Integer endTime){
        this.startTime = startTime;
        this.endTime = endTime;
    }
    
    // returns probaility P that Event is after time t
    abstract public Double getProbability(Integer t);

    /**
     * @return the startTime
     */
    public Integer getStartTime() {
        return startTime;
    }

    /**
     * @param startTime the startTime to set
     */
    public void setStartTime(Integer startTime) {
        this.startTime = startTime;
    }

    /**
     * @return the endTime
     */
    public Integer getEndTime() {
        return endTime;
    }

    /**
     * @param endTime the endTime to set
     */
    public void setEndTime(Integer endTime) {
        this.endTime = endTime;
    }
    
    protected boolean ifCorrectTime(Integer t){
        if((getStartTime() == null || t >= getStartTime()) && (getEndTime() == null || t <= getEndTime())){
            return true;
        }else{
            return false;
        }
    }
}

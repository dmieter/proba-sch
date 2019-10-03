package org.dmieter.sch.prob.scheduler;

/**
 *
 * @author dmieter
 */
public class SchedulerSettings {
    
    private Integer scanDelta = 1;
    
    private Integer costBudget;
    
    private Integer deadline;

    /**
     * @return the scanDelta
     */
    public Integer getScanDelta() {
        return scanDelta;
    }

    /**
     * @param scanDelta the scanDelta to set
     */
    public void setScanDelta(Integer scanDelta) {
        this.scanDelta = scanDelta;
    }

    /**
     * @return the costBudget
     */
    public Integer getCostBudget() {
        return costBudget;
    }

    /**
     * @param costBudget the costBudget to set
     */
    public void setCostBudget(Integer costBudget) {
        this.costBudget = costBudget;
    }

    /**
     * @return the deadline
     */
    public Integer getDeadline() {
        return deadline;
    }

    /**
     * @param deadline the deadline to set
     */
    public void setDeadline(Integer deadline) {
        this.deadline = deadline;
    }
}

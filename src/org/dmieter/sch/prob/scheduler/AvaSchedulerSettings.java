package org.dmieter.sch.prob.scheduler;

/**
 *
 * @author dmieter
 */
public class AvaSchedulerSettings extends SchedulerSettings{
    
    private Double minAvailability;

    /**
     * @return the minAvailability
     */
    public Double getMinAvailability() {
        return minAvailability;
    }

    /**
     * @param minAvailability the minAvailability to set
     */
    public void setMinAvailability(Double minAvailability) {
        this.minAvailability = minAvailability;
    }
    
}

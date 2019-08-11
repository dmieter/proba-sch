package org.dmieter.sch.prob.entity;

/**
 *
 * @author dmieter
 */
public class ResourceRequest {
    protected Integer budget;
    protected Integer parallelNum;
    protected Integer volume;
    protected Double minPerformance;
    
    public ResourceRequest(Integer budget, Integer parallelNum, Integer volume, Integer minPerformance) {
        this.budget = budget;
        this.parallelNum = parallelNum;
        this.volume = volume;
        this.minPerformance = this.minPerformance;
    }

    /**
     * @return the budget
     */
    public Integer getBudget() {
        return budget;
    }

    /**
     * @return the parallelNum
     */
    public Integer getParallelNum() {
        return parallelNum;
    }

    /**
     * @return the volume
     */
    public Integer getVolume() {
        return volume;
    }

    /**
     * @return the minPerformance
     */
    public Double getMinPerformance() {
        return minPerformance;
    }
}

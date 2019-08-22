package org.dmieter.sch.prob.user;

import lombok.Getter;

/**
 *
 * @author dmieter
 */

@Getter
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

    
}
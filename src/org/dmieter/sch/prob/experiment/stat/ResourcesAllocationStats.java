
package org.dmieter.sch.prob.experiment.stat;

import org.dmieter.sch.prob.job.Job;
import org.dmieter.sch.prob.resources.ResourcesAllocation;
import org.dmieter.sch.prob.scheduler.criteria.AvailableProbabilityCriterion;
import org.dmieter.sch.prob.scheduler.criteria.SumCostCriterion;

/**
 *
 * @author emelyanov
 */
public class ResourcesAllocationStats extends SchedulingStats {
    
    private static final String START_TIME_STAT = "Start Time";
    private static final String FINISH_TIME_STAT = "Finish Time";
    private static final String LENGTH_STAT = "Length";
    private static final String TOTAL_COST_STAT = "Total Cost";
    private static final String SUCCESS_RATE_STAT = "Success Rate";
    private static final String SUCCESS_PROB_STAT = "Success Probability";
    private static final String USER_CRITERION_STAT = "User Criterion Value";
    
    public ResourcesAllocationStats(String statsName) {
        super(statsName);
    }
    
    public void processAllocation(Job job){
        ResourcesAllocation allocation = job.getResourcesAllocation();
        
        if(allocation == null){
            stats.addValue(SUCCESS_RATE_STAT, 0d);
            return;
        } else {
            stats.addValue(SUCCESS_RATE_STAT, 1d);
        }
        
        stats.addValue(START_TIME_STAT, allocation.getStartTime().doubleValue());
        stats.addValue(FINISH_TIME_STAT, allocation.getEndTime().doubleValue());
        stats.addValue(LENGTH_STAT, allocation.getEndTime().doubleValue() - allocation.getStartTime().doubleValue());
        
        AvailableProbabilityCriterion availabilityC = new AvailableProbabilityCriterion();
        stats.addValue(SUCCESS_PROB_STAT, availabilityC.getValue(allocation));
        
        SumCostCriterion sumC = new SumCostCriterion();
        stats.addValue(TOTAL_COST_STAT, sumC.getValue(allocation));
        
        if(job.getPreferences() != null && job.getPreferences().getCriterion() != null){
            stats.addValue(USER_CRITERION_STAT, job.getPreferences().getCriterion().getValue(allocation));
        }
        
    }

    @Override
    public String getData() {
        return stats.getData();
    }

    @Override
    public void logFailedExperiment() {
        stats.addValue(SUCCESS_RATE_STAT, 0d);
    }
    
}

package org.dmieter.sch.prob.experiment;

import org.dmieter.sch.prob.SchedulingController;
import org.dmieter.sch.prob.resources.ResourceDomain;

/**
 *
 * @author dmieter
 */
public interface Experiment {

    public void run(int expNnum);

    public String printResults();
    
    public SchedulingController getSchedulingController();
    
}


package org.dmieter.sch.prob.user.experiment;

import org.dmieter.sch.prob.SchedulingController;
import org.dmieter.sch.prob.graphics.DomainVisualizerFrame;

/**
 *
 * @author emelyanov
 */
public class ExperimentRunner {
        
    public static void main(String[] args) {
        runExperiment();
    }

    private static void runExperiment() {
        Experiment exp = new SimpleExperiment();
        exp.run(1);
        DomainVisualizerFrame frame = new DomainVisualizerFrame(exp.getSchedulingController().getResourceDomain());
        frame.setVisible(true);
    }
}

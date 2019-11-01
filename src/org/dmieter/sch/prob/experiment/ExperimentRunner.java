
package org.dmieter.sch.prob.experiment;

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
        System.out.println(exp.printResults());
        DomainVisualizerFrame frame = new DomainVisualizerFrame(exp.getSchedulingController().getResourceDomain());
        frame.setVisible(true);
    }
}

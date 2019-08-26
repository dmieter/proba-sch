
package org.dmieter.sch.prob.user.experiment;

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
    }
}

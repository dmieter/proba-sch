package org.dmieter.sch.prob.experiment;

import org.apache.commons.math3.distribution.LogNormalDistribution;
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
        //Experiment exp = new SimpleExperiment();
//        Experiment exp = new SimplerExperiment();
//        Experiment exp = new EvenSimplerExperiment();
//        Experiment exp = new SimpleExperimentTransition();
//          Experiment exp = new SimpleExperimentWithOpt();
//          Experiment exp = new SimpleExperimentOnInterval();
        Experiment exp = new SimplerGroupExperiment();

          //test();
          
        exp.run(40);
        System.out.println(exp.printResults());
        //DomainVisualizerFrame frame = new DomainVisualizerFrame(exp.getSchedulingController().getResourceDomain());
        //frame.setVisible(true);
    }

}

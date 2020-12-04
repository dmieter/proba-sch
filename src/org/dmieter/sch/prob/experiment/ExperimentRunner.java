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
//        Experiment exp = new SimpleExperiment();
//        Experiment exp = new SimplerExperiment();
//        Experiment exp = new EvenSimplerExperiment();
//        Experiment exp = new SimpleExperimentTransition();
          Experiment exp = new SimpleExperimentWithOpt();

          test();
          
        exp.run(1);
        System.out.println(exp.printResults());
        DomainVisualizerFrame frame = new DomainVisualizerFrame(exp.getSchedulingController().getResourceDomain());
        frame.setVisible(true);
    }
    
    private static void test(){
        LogNormalDistribution distribution = new LogNormalDistribution(10, 1);
        
        System.out.print(distribution.cumulativeProbability(10));
    }
}

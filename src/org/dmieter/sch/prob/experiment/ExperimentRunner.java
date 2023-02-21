package org.dmieter.sch.prob.experiment;

import org.apache.commons.math3.distribution.LogNormalDistribution;
import org.dmieter.sch.prob.graphics.DomainVisualizerFrame;

import java.util.Arrays;
import java.util.List;

/**
 *
 * @author emelyanov
 */
public class ExperimentRunner {

    public static void main(String[] args) {
        runCycleExperiment();
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
          
        exp.run(5);
        System.out.println(exp.printResults());
        //DomainVisualizerFrame frame = new DomainVisualizerFrame(exp.getSchedulingController().getResourceDomain());
        //frame.setVisible(true);
    }

    private static void runCycleExperiment() {
        List<Integer> budgets = Arrays.asList(30, 45, 60);
        StringBuilder resultBuilder = new StringBuilder();
        for(Integer budget: budgets) {
            SimplerGroupExperiment exp = new SimplerGroupExperiment();
            exp.JOB_BUDGET = budget;
            exp.run(5);
            resultBuilder.append("BUDGET: " + budget);
            resultBuilder.append(exp.printResultsShort());
        }

        System.out.println(resultBuilder.toString());

    }

}

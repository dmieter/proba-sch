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
        //runCycleExperimentSmallChangeBudgets();
        //runCycleExperimentSmallChangeRequired();
        //runCycleExperimentLargeChangeBudgets();
        //runCycleExperimentLargeChangeRequired();
        runCycleExperimentVeryLargeChangeRequired();
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
          
        exp.run(2);
        System.out.println(exp.printResults());
        //DomainVisualizerFrame frame = new DomainVisualizerFrame(exp.getSchedulingController().getResourceDomain());
        //frame.setVisible(true);
    }

    private static void runCycleExperimentSmallChangeBudgets() {
        SimplerGroupExperiment.RESOURCES_NUMBER = 22;
        SimplerGroupExperiment.GROUPS_NUMBER = 7;
        SimplerGroupExperiment.RESOURCES_REQUIRED = 8;
        SimplerGroupExperiment.USE_BRUTE_FORCE = true;
        List<Integer> budgets = Arrays.asList(32, 40, 50, 65, 80, 100);
        //List<Integer> budgets = Arrays.asList(50);
        StringBuilder resultBuilder = new StringBuilder();
        for(Integer budget: budgets) {
            SimplerGroupExperiment exp = new SimplerGroupExperiment();
            exp.JOB_BUDGET = budget;
            exp.run(300);
            resultBuilder.append("\n\n\nBUDGET: " + budget);
            resultBuilder.append(exp.printResultsShort());
        }
        System.out.println(resultBuilder.toString());
    }

    private static void runCycleExperimentSmallChangeRequired() {
        SimplerGroupExperiment.RESOURCES_NUMBER = 22;
        SimplerGroupExperiment.GROUPS_NUMBER = 7;
        SimplerGroupExperiment.USE_BRUTE_FORCE = true;
        List<Integer> requiredList = Arrays.asList(1, 3, 5, 7, 9, 11, 13, 15, 17, 19, 22);
        StringBuilder resultBuilder = new StringBuilder();
        for(Integer required: requiredList) {
            SimplerGroupExperiment exp = new SimplerGroupExperiment();
            exp.RESOURCES_REQUIRED = required;
            exp.JOB_BUDGET = required * 8;

            exp.run(250);
            resultBuilder.append("\n\n\nREQUIRED: " + required);
            resultBuilder.append(exp.printResultsShort());
        }
        System.out.println(resultBuilder.toString());
    }

    private static void runCycleExperimentLargeChangeBudgets() {
        SimplerGroupExperiment.RESOURCES_NUMBER = 200;
        SimplerGroupExperiment.GROUPS_NUMBER = 30;
        SimplerGroupExperiment.RESOURCES_REQUIRED = 20;
        SimplerGroupExperiment.USE_BRUTE_FORCE = false;
        List<Integer> budgets = Arrays.asList(40, 60, 80, 100, 120, 140, 160, 180);
        //List<Integer> budgets = Arrays.asList(120);
        StringBuilder resultBuilder = new StringBuilder();
        for(Integer budget: budgets) {
            SimplerGroupExperiment exp = new SimplerGroupExperiment();
            exp.JOB_BUDGET = budget;
            exp.run(200);
            resultBuilder.append("\n\n\nBUDGET: " + budget);
            resultBuilder.append(exp.printResultsShort());
        }
        System.out.println(resultBuilder.toString());
    }

    private static void runCycleExperimentLargeChangeRequired() {
        SimplerGroupExperiment.RESOURCES_NUMBER = 200;
        SimplerGroupExperiment.GROUPS_NUMBER = 30;
        SimplerGroupExperiment.USE_BRUTE_FORCE = false;
        List<Integer> requiredList = Arrays.asList(1, 5, 10, 15, 20, 25, 30);
        //List<Integer> requiredList = Arrays.asList(30);
        StringBuilder resultBuilder = new StringBuilder();
        for(Integer required: requiredList) {
            SimplerGroupExperiment exp = new SimplerGroupExperiment();
            exp.RESOURCES_REQUIRED = required;
            exp.JOB_BUDGET = required * 6;

            exp.run(250);
            resultBuilder.append("\n\n\nREQUIRED: " + required);
            resultBuilder.append(exp.printResultsShort());
        }
        System.out.println(resultBuilder.toString());
    }

    private static void runCycleExperimentVeryLargeChangeRequired() {
        SimplerGroupExperiment.RESOURCES_NUMBER = 1000;
        SimplerGroupExperiment.GROUPS_NUMBER = 100;
        SimplerGroupExperiment.USE_BRUTE_FORCE = false;
        SimplerGroupExperiment.USE_TREE = false;
        List<Integer> requiredList = Arrays.asList(1, 10, 20, 40, 60, 80);
        //List<Integer> requiredList = Arrays.asList(50);
        StringBuilder resultBuilder = new StringBuilder();
        for(Integer required: requiredList) {
            SimplerGroupExperiment exp = new SimplerGroupExperiment();
            SimplerGroupExperiment.USE_TREE = false;
            exp.RESOURCES_REQUIRED = required;
            exp.JOB_BUDGET = required * 6;

            exp.run(200);
            resultBuilder.append("\n\n\nREQUIRED: " + required);
            resultBuilder.append(exp.printResultsShort());
        }
        System.out.println(resultBuilder.toString());
    }

}

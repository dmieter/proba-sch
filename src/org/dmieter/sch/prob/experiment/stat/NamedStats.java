package org.dmieter.sch.prob.experiment.stat;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

/**
 *
 * @author emelyanov
 */
public class NamedStats {

    private String groupName;
    private Map<String, DescriptiveStatistics> stats = new HashMap<>();

    public NamedStats(String name) {
        this.groupName = name;
        stats.clear();
    }

    public void clearStats() {
        stats = new HashMap<>();
    }

    public void addValue(String name, Double value) {
        DescriptiveStatistics varStats = stats.get(name);
        if (varStats == null) {
            varStats = new DescriptiveStatistics();
            stats.put(name, varStats);
        }
        varStats.addValue(value);
    }

    public String getData() {
        String data = "\n";
        if (groupName != null) {
            data += groupName.toUpperCase() + "\n";
        }

        for (Map.Entry<String, DescriptiveStatistics> entry : stats.entrySet()) {
            DescriptiveStatistics varStats = entry.getValue();
            data += entry.getKey() + ": \n"
                    + "\t" + varStats.getMean() + "(" + varStats.getN() + ")" + "\n"
                    + "\t min: " + varStats.getMin() + "\n"
                    //+ "\t 25%: " + varStats.getPercentile(25) + "\n"
                    //+ "\t 50%: " + varStats.getPercentile(50) + "\n"
                    //+ "\t 75%: " + varStats.getPercentile(75) + "\n"
                    + "\t max: " + varStats.getMax() + "\n";
        }

        return data;
    }

    public String getData(String alias) {
        String data = "\n";
        if (groupName != null) {
            data += groupName.toUpperCase() + "\n";
        }

        DescriptiveStatistics varStats = stats.get(alias);
        if (varStats != null) {

            data += alias + ": \n"
                    + "\t" + varStats.getMean() + "(" + varStats.getN() + ")" + "\n"
                    + "\t min: " + varStats.getMin() + "\n"
                    + "\t 25%: " + varStats.getPercentile(25) + "\n"
                    + "\t 50%: " + varStats.getPercentile(50) + "\n"
                    + "\t 75%: " + varStats.getPercentile(75) + "\n"
                    + "\t max: " + varStats.getMax() + "\n";
        }

        return data;
    }
    
    public String getDetailedData(String alias) {
        String data = "\n";
        if (groupName != null) {
            data += groupName.toUpperCase() + "\n";
        }

        DescriptiveStatistics varStats = stats.get(alias);
        if (varStats != null) {

            data += alias + ": \n"
                    + "\t" + varStats.getMean() + "(" + varStats.getN() + ")" + "\n"
                    + "\t min: " + varStats.getMin() + "\n"
                    + "\t 25%: " + varStats.getPercentile(25) + "\n"
                    + "\t 50%: " + varStats.getPercentile(50) + "\n"
                    + "\t 75%: " + varStats.getPercentile(75) + "\n"
                    + "\t max: " + varStats.getMax() + "\n"
                    + "\t values: " + Arrays.toString(varStats.getValues()) + "\n";
        }

        return data;
    }

}

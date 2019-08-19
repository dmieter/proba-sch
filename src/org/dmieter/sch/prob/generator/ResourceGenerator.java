package org.dmieter.sch.prob.generator;

import org.dmieter.sch.prob.resources.ResourceDescription;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math3.geometry.euclidean.oned.Interval;
import project.math.distributions.DistributionGenerator;
import project.math.distributions.UniformFacade;

/**
 *
 * @author dmieter
 */
public class ResourceGenerator {

    /* random hardware index is calculated once for all hardware resources */
    protected DistributionGenerator genHardwareIndex = new UniformFacade(0, 1);
    /* real hardware values are generated as a combination of a common hardware index + some mutation */
    public DistributionGenerator genHardwareMutationIndex;

    /* intervals for different hardware parameters */
    public Interval intMIPS;
    public Interval intRAM;
    
    /* mutation factor for price based on hardware index */
    public DistributionGenerator genPriceMutationIndex;
    /* base interval for price */
    public Interval intPrice;

    public List<ResourceDescription> generateResources(int amount) {
        List<ResourceDescription> resources = new ArrayList<>(amount);

        for (int i = 0; i < amount; i++) {
            resources.add(generateResource());
        }

        return resources;
    }

    public ResourceDescription generateResource() {

        ResourceDescription resource = new ResourceDescription();

        double hwIndex = genHardwareIndex.getRandom();
        double hwMutationFactor;
        
        resource.hwIndex = hwIndex;

        if (intMIPS != null) {
            hwMutationFactor = genHardwareMutationIndex.getRandom();
            resource.mips = getMutatedIntervalValue(intMIPS, hwIndex, hwMutationFactor).intValue();
        }

        if (intRAM != null) {
            hwMutationFactor = genHardwareMutationIndex.getRandom();
            resource.ram = getMutatedIntervalValue(intRAM, hwIndex, hwMutationFactor).intValue();
        }

        if (intPrice != null) {
            double priceMutationFactor = genPriceMutationIndex.getRandom();
            resource.price = getMutatedIntervalValue(intPrice, hwIndex, priceMutationFactor);
        }

        return resource;
    }

    protected Double getMutatedIntervalValue(Interval interval, double baseFactor, double mutationFactor) {
        return interval.getInf() + interval.getSize() * baseFactor * mutationFactor;
    }

}

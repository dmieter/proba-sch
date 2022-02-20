package org.dmieter.sch.prob.generator;

import org.dmieter.sch.prob.resources.ResourceDescription;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.math3.geometry.euclidean.oned.Interval;
import org.dmieter.sch.prob.resources.Resource;
import org.dmieter.sch.prob.resources.ResourceDomain;
import project.math.distributions.DistributionGenerator;
import project.math.distributions.UniformFacade;
import project.math.utils.MathUtils;

/**
 *
 * @author dmieter
 */
public class ResourceGenerator extends Generator {

    private static long resourceCounter = 0;
    
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

    public List<ResourceDescription> generateResourceDescriptions(int amount) {
        List<ResourceDescription> resources = new ArrayList<>(amount);

        for (int i = 0; i < amount; i++) {
            resources.add(generateResourceDescription());
        }

        return resources;
    }
        
    public List<Resource> generateResources(int amount) {
        
        return generateResourceDescriptions(amount).stream()
                .map(r -> generateResource(r))
                .collect(Collectors.toList());
    }
    
    public ResourceDomain generateResourceDomain(int amount){
        return new ResourceDomain(generateResources(amount));
    }

    public Resource generateResource(ResourceDescription description) {
        return new Resource(addResource(), description);
    }
    
    public ResourceDescription generateResourceDescription() {

        ResourceDescription resource = new ResourceDescription();

        double hwIndex = genHardwareIndex.getRandom();
        double hwMutationFactor;
        
        resource.hwIndex = hwIndex;

        if (intMIPS != null) {
            hwMutationFactor = genHardwareMutationIndex.getRandom();
            resource.mips = MathUtils.nextUp(getMutatedIntervalValue(intMIPS, hwIndex, hwMutationFactor));
        }

        if (intRAM != null) {
            hwMutationFactor = genHardwareMutationIndex.getRandom();
            resource.ram = getMutatedIntervalValue(intRAM, hwIndex, hwMutationFactor);
        }

        if (intPrice != null) {
            double priceMutationFactor = genPriceMutationIndex.getRandom();
            resource.price = getMutatedIntervalValue(intPrice, hwIndex, priceMutationFactor);
            resource.price *= Math.exp(0.05 * (resource.mips - 1));
        }

        return resource;
    }
    
    private synchronized long addResource(){
        return ++resourceCounter;
    }

}

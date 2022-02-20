
package org.dmieter.sch.prob.resources;

/**
 *
 * @author dmieter
 */
public class ResourceDescription { 
    
    public double mips;
    public double ram;
    
    public double price;
    
    public double hwIndex;
    
    public ResourceDescription copy(){
        ResourceDescription copy = new ResourceDescription();
        copy.mips = mips;
        copy.ram = ram;
        copy.price = price;
        copy.hwIndex = hwIndex;
        
        return copy;
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dmieter.sch.prob.distribution;

import org.apache.commons.math3.distribution.RealDistribution;

/**
 *
 * @author emelyanov
 */
public class NormalDistribution extends Distribution {

    RealDistribution distribution;
    
    public NormalDistribution(Double mean, Double sd){
        distribution = new org.apache.commons.math3.distribution.NormalDistribution(mean, sd);
    }
    
    public NormalDistribution(Double mean, Double sd, Integer startTime, Integer endTime){
        super(startTime, endTime);
        distribution = new org.apache.commons.math3.distribution.NormalDistribution(mean, sd);
    }
    
    @Override
    public Double getProbability(Integer t) {
        if(ifCorrectTime(t)){
            return distribution.cumulativeProbability(t);
        }else{
            return 0d;
        }
    }
    
}

package org.dmieter.sch.prob.job;

import org.dmieter.sch.prob.user.ResourceRequest;

/**
 *
 * @author dmieter
 */
public class RegularJob extends Job {

    public RegularJob(ResourceRequest request) {
        super(request);
        this.startVariability = 0d;
        this.finishVariability = 0d;
    }

}

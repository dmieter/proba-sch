package org.dmieter.sch.prob.graphics;

import javax.swing.JFrame;
import org.dmieter.sch.prob.resources.ResourceDomain;

/**
 *
 * @author emelyanov
 */
public class DomainVisualizerFrame extends JFrame {

    private ResourceDomain domain;
    
    public DomainVisualizerFrame(ResourceDomain domain) {
        this.domain = domain;
        initUI();
    }

    private void initUI() {

        final DomainVisualizerPanel surface = new DomainVisualizerPanel(domain);
        add(surface);

        setTitle("Points");
        setSize(350, 250);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}

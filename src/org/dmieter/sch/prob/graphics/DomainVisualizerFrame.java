package org.dmieter.sch.prob.graphics;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
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
        
        final JScrollPane scrollPane = new JScrollPane(surface);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED); //SETTING SCHEME FOR HORIZONTAL BAR
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        

        setTitle("Points");
        setSize(350, 250);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        add(scrollPane);
    }
}

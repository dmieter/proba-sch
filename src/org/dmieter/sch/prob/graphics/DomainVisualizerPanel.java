package org.dmieter.sch.prob.graphics;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.Random;
import javax.swing.JPanel;
import org.dmieter.sch.prob.events.Event;
import org.dmieter.sch.prob.resources.Resource;
import org.dmieter.sch.prob.resources.ResourceDomain;
import project.math.utils.MathUtils;

/**
 *
 * @author emelyanov
 */
public class DomainVisualizerPanel extends JPanel {

    private ResourceDomain domain;

    private int RESOURCE_HEIGHT = 30;
    private int PROBABILITY_HEIGHT = 25;

    public DomainVisualizerPanel(ResourceDomain domain) {
        this.domain = domain;
    }

    private void doDrawing(Graphics g) {

        Graphics2D g2d = (Graphics2D) g;

        Point startPoint = new Point(20, 40);
        for (Resource r : domain.getResources()) {
            drawResource(g2d, r, startPoint);
            startPoint.y += RESOURCE_HEIGHT;
        }

//        g2d.setPaint(Color.blue);
//
//        int w = getWidth();
//        int h = getHeight();
//
//        Random r = new Random();
//
//        for (int i = 0; i < 2000; i++) {
//
//            int x = Math.abs(r.nextInt()) % w;
//            int y = Math.abs(r.nextInt()) % h;
//            g2d.drawLine(x, y, x, y);
//        }
    }

    private void drawResource(Graphics2D g2d, Resource resource, Point startPoint) {
        g2d.drawLine(startPoint.x, startPoint.y, startPoint.x, startPoint.y - RESOURCE_HEIGHT);
        g2d.drawLine(startPoint.x, startPoint.y, startPoint.x + 1000, startPoint.y);
        for (Event e : resource.getActiveEvents(0, Integer.MAX_VALUE)) {
            for (Integer x = e.getStartTime(); x < e.getEndTime(); x++) {
                int probValue = MathUtils.intNextUp(e.getResourcesAllocatedP(x)*PROBABILITY_HEIGHT);
                g2d.drawLine(startPoint.x+x, startPoint.y-probValue-1, startPoint.x+x, startPoint.y-probValue-1);
            }
            g2d.drawLine(startPoint.x+e.getEventTime(), startPoint.y, startPoint.x+e.getEventTime(), startPoint.y-10);
        }
    }

    @Override
    public void paintComponent(Graphics g) {

        super.paintComponent(g);
        doDrawing(g);
    }

}

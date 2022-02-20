package org.dmieter.sch.prob.graphics;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Font;
import java.awt.Point;
import java.lang.Math;
import java.util.List;
import java.util.Random;
import javax.swing.JPanel;
import org.dmieter.sch.prob.events.Event;
import org.dmieter.sch.prob.job.Job;
import org.dmieter.sch.prob.resources.Resource;
import org.dmieter.sch.prob.resources.ResourceDomain;
import project.math.utils.MathUtils;

/**
 *
 * @author emelyanov
 */
public class DomainVisualizerPanel extends JPanel {

    private ResourceDomain domain;

    private int RESOURCE_HEIGHT = 200;
    private int PROBABILITY_HEIGHT = 160;
    private int HEIGHT_FONT = Math.round((float) 0.15 * RESOURCE_HEIGHT);
    private int HEIGHT_FONT_NUM = Math.round((float) 0.1 * RESOURCE_HEIGHT);
    private int HEIGHT_FONT_NUM_MAIN = Math.round((float) 0.125 * RESOURCE_HEIGHT);
    private int MAX_X = 0;
    private Font MyFont = new Font(Font.SANS_SERIF, Font.PLAIN, HEIGHT_FONT);
    private Font MyFont_NUM = new Font(Font.SANS_SERIF, Font.PLAIN, HEIGHT_FONT_NUM);
    private Font MyFont_NUM_MAIN = new Font(Font.SANS_SERIF, Font.PLAIN, HEIGHT_FONT_NUM_MAIN);

    public DomainVisualizerPanel(ResourceDomain domain) {
        this.domain = domain;
        this.setPreferredSize(new Dimension(5000, (domain.getResources().size() + 1) * RESOURCE_HEIGHT));
        this.setBackground(Color.WHITE);
    }

    private void doDrawing(Graphics g) {

        Graphics2D g2d = (Graphics2D) g;

        Point startPoint = new Point(Math.round(RESOURCE_HEIGHT * (float) 1.5), RESOURCE_HEIGHT);
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
        // 0. color init
        g2d.setColor(Color.BLACK);
        g2d.setFont(MyFont);
        //Integer max_x = 0;
        // 1. draw events
        for (Event e : resource.getActiveEvents(0, Integer.MAX_VALUE)) {
            //1.1 draw events
            g2d.setStroke(new BasicStroke(2));
            for (Integer x = e.getStartTime(); x < e.getEndTime(); x++) {
                int probValue = MathUtils.intNextUp(e.getResourcesAllocatedP(x) * PROBABILITY_HEIGHT);
                if (x >= 0) {
                    if (e.getEventColor() != null) {
                        g2d.setColor(e.getEventColor());
                        g2d.drawLine(startPoint.x + x, startPoint.y, startPoint.x + x, startPoint.y - probValue - 1);
                    } else {
                        g2d.setColor(Color.BLACK);
                        g2d.drawLine(startPoint.x + x, startPoint.y - probValue - 1, startPoint.x + x, startPoint.y - probValue - 1);
                    }
                }
            }
            if (MAX_X < e.getEndTime()) {
                MAX_X = e.getEndTime();
            }
            // 1.2 draw eventTime and time
            g2d.setStroke(new BasicStroke(1));
            g2d.setFont(MyFont_NUM_MAIN);
            g2d.setColor(Color.BLACK);
            Integer i = 200;
            while (i < MAX_X) {
                int k = (int) Math.ceil(Math.log10(i));
                g2d.drawString(i.toString(), startPoint.x + i - k * Math.round(HEIGHT_FONT_NUM_MAIN * 0.275), startPoint.y + HEIGHT_FONT_NUM_MAIN);
                g2d.drawLine(startPoint.x + i, startPoint.y, startPoint.x + i, startPoint.y - 20);
                i += 200;
            }
            g2d.setFont(MyFont_NUM);
            g2d.setColor(Color.BLACK);
            if ((e.getEventTime()) >= 0) {
                int k = (int) Math.ceil(Math.log10(e.getEventTime()));
                g2d.drawLine(startPoint.x + e.getEventTime(), startPoint.y, startPoint.x + e.getEventTime(), startPoint.y - 10);
                g2d.drawString(e.getEventTime().toString(), startPoint.x + e.getEventTime() - k * Math.round(HEIGHT_FONT_NUM * 0.275), startPoint.y + HEIGHT_FONT_NUM);
            }
        }
        // 2. draw mini coordinates and text
        g2d.setFont(MyFont);
        g2d.drawString("ID = " + resource.id_View(), startPoint.x - Math.round(RESOURCE_HEIGHT * (float) 1.25), startPoint.y - Math.round(RESOURCE_HEIGHT * (float) 0.4) - 2 * HEIGHT_FONT);
        g2d.drawString("Mips = " + (Math.round(resource.mips_View() * 100) / 100.0), startPoint.x - Math.round(RESOURCE_HEIGHT * (float) 1.25), startPoint.y - Math.round(RESOURCE_HEIGHT * (float) 0.4) - HEIGHT_FONT);
        g2d.drawString("Ram = " + Math.round(resource.ram_View() * 100) / 100.0, startPoint.x - Math.round(RESOURCE_HEIGHT * (float) 1.25), startPoint.y - Math.round(RESOURCE_HEIGHT * (float) 0.4));
        g2d.drawString("Price = " + Math.round(resource.price_View() * 100) / 100.0, startPoint.x - Math.round(RESOURCE_HEIGHT * (float) 1.25), startPoint.y - Math.round(RESOURCE_HEIGHT * (float) 0.4) + HEIGHT_FONT);
        g2d.drawString("HwIndex = " + Math.round(resource.hwIndex_View() * 100) / 100.0, startPoint.x - Math.round(RESOURCE_HEIGHT * (float) 1.25), startPoint.y - Math.round(RESOURCE_HEIGHT * (float) 0.4) + 2 * HEIGHT_FONT);
        g2d.drawLine(startPoint.x, startPoint.y, startPoint.x, startPoint.y - RESOURCE_HEIGHT);
        g2d.drawLine(startPoint.x, startPoint.y, startPoint.x + MAX_X + 100, startPoint.y);
        ;
    }

    @Override
    public void paintComponent(Graphics g) {

        super.paintComponent(g);
        doDrawing(g);
    }

}

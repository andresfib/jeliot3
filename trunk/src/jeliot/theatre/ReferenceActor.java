package jeliot.theatre;

import java.awt.*;
import java.util.*;

/**
  * @author Pekka Uronen
  *
  * created         10.10.1999
  */
public class ReferenceActor extends ValueActor {

    private InstanceActor instance = null;
    private VariableActor variable = null;
    private static int refWidth = 6;

    private static int refLen = 18;

    private Point[] bend;

    public ReferenceActor() { }

    public ReferenceActor(InstanceActor inst) {
        this.instance = inst;
    }

    public ReferenceActor(InstanceActor inst, VariableActor var) {
        this.instance = inst;
        this.variable = var;
    }

    public InstanceActor getInstanceActor() {
        return this.instance;
    }

    public void setInstanceActor(InstanceActor inst) {
        this.instance = inst;
    }

    public void setVariableActor(VariableActor var) {
        this.variable = var;
    }

    public VariableActor getVariableActor() {
        return this.variable;
    }

    public void paintActor(Graphics g) {

        Color bc = bgcolor;
        Color fc = fgcolor;

        int h = height;
        //Point p = getRootLocation();

        if (instance != null) {

            bc = instance.bgcolor;
            fc = instance.fgcolor;

            // draw reference area
            g.setColor(darkColor);
            g.fillRect(0, 0, refWidth, h);
            //g.setColor(bc);
            //g.fillRect(p.x+1, p.y+1, refWidth-2, h-2);
            g.setColor(fc);
            g.drawRect(0, 0, refWidth, h);

            Point vp = this.getRootLocation();

            g.translate(-vp.x, -vp.y);

            calculateBends();

            int n = bend.length;

            for (int i = 1; i < n; ++i) {
                Point p1 = bend[i-1];
                Point p2 = bend[i];

                g.setColor(bc);
                g.drawLine(p1.x, p1.y, p2.x, p2.y);
                g.setColor(fc);
                if (p2.x > p1.x) {
                    g.drawLine(p1.x+1, p1.y-1, p2.x, p1.y-1);
                    g.drawLine(p1.x, p1.y+1, p2.x, p1.y+1);
                } else {
                    g.drawLine(p1.x-1, p1.y+1, p2.x-1, p2.y);
                    g.drawLine(p1.x+1, p1.y, p2.x+1, p2.y);
                }
            }

            g.translate(vp.x, vp.y);

        } else {

            // draw reference area
            g.setColor(darkColor);
            g.fillRect(0, 0, refWidth, h);
            //g.setColor(bc);
            //g.fillRect(p.x+1, p.y+1, refWidth-2, h-2);
            g.setColor(fc);
            g.drawRect(0, 0, refWidth, h);

            g.setColor(fgcolor);

            int a = refWidth - 3;
            int b = a + refLen;
            //System.out.println("h = " +h);


            /*
            * There reference that is drawn below
            *        | .
            *  ------| | |
            *        | '
            */
            //Borders
            g.drawLine(a, h/2 - 1,
                       b, h/2 - 1);
            g.drawLine(a, h/2 + 1,
                       b, h/2 + 1);

            g.drawLine(b, h/2 - 8,
                       b, h/2 + 8);
            g.drawLine(b+2, h/2 - 8,
                       b+2, h/2 + 8);

            g.drawLine(b+5, h/2 - 5,
                       b+5, h/2 + 5);
            g.drawLine(b+7, h/2 - 5,
                       b+7, h/2 + 5);

            g.drawLine(b+10, h/2 - 2,
                       b+10, h/2 + 2);
            g.drawLine(b+12, h/2 - 2,
                       b+12, h/2 + 2);


            //Insides
            g.setColor(bgcolor);
            g.drawLine(a, h/2,
                       b, h/2);

            g.drawLine(b+1, h/2 - 8,
                       b+1, h/2 + 8);
            g.drawLine(b+6, h/2 - 5,
                       b+6, h/2 + 5);
            g.drawLine(b+11, h/2 - 2,
                       b+11, h/2 + 2);

        }
    }

    public void calculateBends() {
        Point ip = instance.getRootLocation();
        Point vp = this.getRootLocation();

        int iy1 = ip.y;
        int iy2 = iy1 + instance.getHeight();
        int vy1 = vp.y;
        int vy2 = vy1 + height;

        int ix = ip.x;
        int vx = vp.x + refWidth;

        bend = new Point[4];
        bend[0] = new Point(vx - 3, (vy1 + vy2) / 2);
        bend[1] = new Point(vx + 45 /*- (vy1/6)*/, bend[0].y);
        bend[2] = new Point(bend[1].x, iy1 + 12);
        bend[3] = new Point(ix, bend[2].y);
    }

    public void calculateSize() {
        setSize(getPreferredSize());
    }

    public int getReferenceWidth() {
        if (instance != null) {
            return bend[1].x - bend[0].x + 4;
        } else {
            return refLen + 15;
        }
    }

    public Dimension getPreferredSize() {
        FontMetrics fm = getFontMetrics();
        int h = fm.getHeight();
        int w = refWidth;
        return new Dimension(w, h);
    }

}
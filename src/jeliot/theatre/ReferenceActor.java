package jeliot.theatre;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;

/**
  * @author Pekka Uronen
  *
  * created         10.10.1999
  */
public class ReferenceActor extends ValueActor {

    private InstanceActor instance = null;
    private VariableActor variable = null;

    /**
     * Reference width is the width of the rectangle
     * in the variable end of the reference.
     */
    private static int refWidth = 6;

    /**
     * Length of the null reference line and also the base for the
     * first part of the reference when not null.
     */
    private static int refLen = 18;

    /**
     * Used to make the first part of the reference line different in
     * length.
     */
    private int refWidthRandom = 12;

    private boolean instVarConnect = false;

    private Point[] bend;
    private Point[] arrowhead;
    private Polygon arrowheadPolygon1;
    private Polygon arrowheadPolygon2;

    public ReferenceActor() {
        refWidthRandom += (int) (Math.random() * 15);
    }

    public ReferenceActor(InstanceActor inst) {
        this();
        this.instance = inst;
    }

    public ReferenceActor(InstanceActor inst, boolean instVarConnect) {
        this(inst);
        this.instVarConnect = instVarConnect;
    }

    public ReferenceActor(InstanceActor inst, VariableActor var) {
        this(inst);
        this.variable = var;
    }

    public ReferenceActor(InstanceActor inst, VariableActor var, boolean instVarConnect) {
        this(inst, var);
        this.instVarConnect = instVarConnect;
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

                g.setColor(fc);
                if (p1.y == p2.y) {
                    g.drawLine(p1.x, p1.y-1, p2.x, p1.y-1);
                    g.drawLine(p1.x, p1.y+1, p2.x, p1.y+1);
                } else if (p2.x == p1.x) {
                    g.drawLine(p1.x-1, p1.y, p2.x-1, p2.y);
                    g.drawLine(p1.x+1, p1.y, p2.x+1, p2.y);
                }
            }

            for (int i = 1; i < n; ++i) {
                Point p1 = bend[i-1];
                Point p2 = bend[i];
                g.setColor(bc);
                g.drawLine(p1.x, p1.y, p2.x, p2.y);
            }

            //Here is drawn something that shows that
            //the reference is pointing to this exact instance.
            g.setColor(fc);
            g.fillPolygon(arrowheadPolygon1);
            g.setColor(bc);
            g.fillPolygon(arrowheadPolygon2);

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

        int ix  = ip.x;
        int ix2 = ix + instance.getWidth();
        int vx  = vp.x + refWidth;

        if (!instVarConnect) {
            int xp2 = vx + refLen + refWidthRandom;
            if (xp2 < ix) {
                bend = new Point[4];
                bend[0] = new Point(vx - 3, (vy1 + vy2) / 2);
                bend[1] = new Point(xp2 /*- (vy1/6)*/, bend[0].y);
                bend[2] = new Point(bend[1].x, iy1 + 12);
                bend[3] = new Point(ix - 3, bend[2].y);
                calculateArrowhead(2);
            } else if (xp2 > ix2) {
                bend = new Point[4];
                bend[0] = new Point(vx - 3, (vy1 + vy2) / 2);
                bend[1] = new Point(xp2 /*- (vy1/6)*/, bend[0].y);
                bend[2] = new Point(bend[1].x, iy1 + 12);
                bend[3] = new Point(ix2 - 3, bend[2].y);
                calculateArrowhead(4);
            } else {
                bend = new Point[3];
                bend[0] = new Point(vx - 3, (vy1 + vy2) / 2);
                bend[1] = new Point(xp2 /*- (vy1/6)*/, bend[0].y);
                bend[2] = new Point(bend[1].x, iy1);
                calculateArrowhead(3);
            }
        } else {
            //Change this!
            //It should contain 7 points with 5 bends
            bend = new Point[4];
            bend[0] = new Point(vx - 3, (vy1 + vy2) / 2);
            bend[1] = new Point(vx + refLen + refWidthRandom /*- (vy1/6)*/, bend[0].y);
            bend[2] = new Point(bend[1].x, iy1 + 12);
            bend[3] = new Point(ix + 3, bend[2].y);
            calculateArrowhead(2);
        }
    }

    /** dir is 1 up, 2 right, 3 down and 4 left */
    public void calculateArrowhead(int dir) {
        int n = bend.length - 1;

        switch (dir) {
            // left
            case 4: {
                arrowhead = new Point[3];
                arrowhead[0] = new Point(bend[n]);
                arrowhead[1] = new Point(bend[n]);
                arrowhead[2] = new Point(bend[n]);
                arrowhead[0].translate(-3, 0);
                arrowhead[1].translate(10, -7);
                arrowhead[2].translate(10, 7);

                arrowheadPolygon1 = new Polygon();
                for (int i = 0; i < 3; i++) {
                    arrowheadPolygon1.addPoint(arrowhead[i].x,
                                               arrowhead[i].y);
                }

                arrowhead[0].translate(3,0);
                arrowhead[1].translate(-2, 3);
                arrowhead[2].translate(-2, -3);

                arrowheadPolygon2 = new Polygon();
                for (int i = 0; i < 3; i++) {
                    arrowheadPolygon2.addPoint(arrowhead[i].x,
                                               arrowhead[i].y);
                }
                break;
            }

            // right
            case 2: {
                arrowhead = new Point[3];
                arrowhead[0] = new Point(bend[n]);
                arrowhead[1] = new Point(bend[n]);
                arrowhead[2] = new Point(bend[n]);
                arrowhead[0].translate(3, 0);
                arrowhead[1].translate(-10, -7);
                arrowhead[2].translate(-10, 7);

                arrowheadPolygon1 = new Polygon();
                for (int i = 0; i < 3; i++) {
                    arrowheadPolygon1.addPoint(arrowhead[i].x,
                                               arrowhead[i].y);
                }

                arrowhead[0].translate(-3,0);
                arrowhead[1].translate(2, 3);
                arrowhead[2].translate(2, -3);

                arrowheadPolygon2 = new Polygon();
                for (int i = 0; i < 3; i++) {
                    arrowheadPolygon2.addPoint(arrowhead[i].x,
                                               arrowhead[i].y);
                }
                break;
            }
            // down
            case 3: {
                arrowhead = new Point[3];
                arrowhead[0] = new Point(bend[n]);
                arrowhead[1] = new Point(bend[n]);
                arrowhead[2] = new Point(bend[n]);
                arrowhead[0].translate(0, 0);
                arrowhead[1].translate(-7, -13);
                arrowhead[2].translate(7, -13);

                arrowheadPolygon1 = new Polygon();
                for (int i = 0; i < 3; i++) {
                    arrowheadPolygon1.addPoint(arrowhead[i].x,
                                               arrowhead[i].y);
                }

                arrowhead[0].translate(0, -3);
                arrowhead[1].translate(3, 2);
                arrowhead[2].translate(-3, 2);

                arrowheadPolygon2 = new Polygon();
                for (int i = 0; i < 3; i++) {
                    arrowheadPolygon2.addPoint(arrowhead[i].x,
                                               arrowhead[i].y);
                }
                break;
            }

            // up
            case 1: {
                arrowhead = new Point[3];
                arrowhead[0] = new Point(bend[n-1]);
                arrowhead[1] = new Point(bend[n-1]);
                arrowhead[2] = new Point(bend[n-1]);
                arrowhead[0].translate(0, 0);
                arrowhead[1].translate(-7, 13);
                arrowhead[2].translate(7, 13);

                arrowheadPolygon1 = new Polygon();
                for (int i = 0; i < 3; i++) {
                    arrowheadPolygon1.addPoint(arrowhead[i].x,
                                               arrowhead[i].y);
                }

                arrowhead[0].translate(0, 3);
                arrowhead[1].translate(3, -2);
                arrowhead[2].translate(-3, -2);

                arrowheadPolygon2 = new Polygon();
                for (int i = 0; i < 3; i++) {
                    arrowheadPolygon2.addPoint(arrowhead[i].x,
                                               arrowhead[i].y);
                }
                break;
            }
        }
    }

    public void calculateSize() {
        setSize(getPreferredSize());
    }

    public int getReferenceWidth() {
        calculateBends();
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
package jeliot.theatre;

import java.awt.*;
import java.util.*;

/**
  * @author Pekka Uronen
  * @author Niko Myller
  */
public class Actor implements Cloneable {


    static Component dummy = new Panel();
    static Image shadowImage;

    private static Font defaultFont =
            new Font("SansSerif", Font.PLAIN, 12);

    public static final int HIGHLIGHT   =  -1;
    public static final int NORMAL      =   0;
    public static final int SHADED      =   1;

    /** Actor's x-coordinate in parent. */
    private int x;

    /** Actor's y-coordinate in parent. */
    private int y;

    /** Actor's width, not including shadow. */
    protected int width;

    /** Actor's height, not including shadow. */
    protected int height;

    /** Width of actor's border */
    protected int borderWidth = 2;

    private int shadoww = 0;

    /** Margin, not including border */
    protected Insets insets = new Insets(0, 0, 0, 0);

    /** Trace left by the actor. */
    private Trace trace;

    /** HIGHLIGHT, NORMAL or SHADED. */
    protected int light = NORMAL;

    /** Font used to draw text on the actor. */
    protected Font font = defaultFont;

    /** Actor's background color. */
    protected Color bgcolor;

    /** Actor's foreground color. Used to draw text, for example. */
    protected Color fgcolor = Color.black;

    /** Darker shade of background color. */
    protected Color darkColor;

    /** Lighter shade of background color. */
    protected Color lightColor;

    /** Color used for highlighting. */
    protected Color highColor = Color.white;

    /** Color used for painting borders. */
    protected Color borderColor = Color.black;

    /** Parent actor. */
    private ActorContainer parent;

    public ActorContainer getParent() {
        return parent;
    }

    public Color getBackground() {
        return bgcolor;
    }

    public void setParent(ActorContainer parent) {
        if (this.parent != null && this.parent != parent) {
            this.parent.removeActor(this);
        }
        this.parent = parent;
    }

    public void setShadow(int s) {
        this.shadoww = s;
    }

    public int getShadow() {
        return this.shadoww;
    }

    /** Paints the shadow of the actor. Override this. */
    public void paintShadow(Graphics g) {
        if (shadoww > 0 && shadowImage != null) {
            Shape clip = g.getClip();
            g.clipRect(shadoww, height, width, shadoww);
            for (int x = 0; x < width; x += 24) {
                g.drawImage(shadowImage, x + shadoww, height, dummy);
            }
            g.setClip(clip);
            g.clipRect(width, shadoww, shadoww, height);
            int b = (width + height) % 2;
            for (int y = -b; y < height; y += 24) {
                g.drawImage(shadowImage, width, y + shadoww, dummy);
            }
            g.setClip(clip);
        }
    }

    /** Paints the actor. Override this. */
    public void paintActor(Graphics g) {
        g.setColor(Color.black);
        g.fillRect(0, 0, width, height);
    }

    /** Paints the actors contained in the vector on this actor.
      */
    protected void paintActors(Graphics g, Vector actors) {
        int n = actors.size();
        for (int i = 0; i < n; ++i) {
            Actor act = (Actor)actors.elementAt(i);
            int x = act.getX();
            int y = act.getY();
            g.translate(x, y);
            act.paintActor(g);
            g.translate(-x, -y);
        }
    }

    public void setBounds(int x, int y, int w, int h) {
        setLocation(x, y);
        setSize(w, h);
    }

    public void setBorderWidth(int w) {
        this.borderWidth = w;
    }

    public void setInsets(Insets insets) {
        this.insets = insets;
    }

    public void setLocation(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setLocation(Point loc) {
        setLocation(loc.x, loc.y);
    }

    public Point getLocation() {
        return new Point(x, y);
    }

    public Point getRootLocation() {
        int x = this.x;
        int y = this.y;
        ActorContainer parent = this.parent;
        while ( parent instanceof Actor ) {
            Actor p = (Actor)parent;
            x += p.x;
            y += p.y;
            parent = p.parent;
        }
        return new Point(x, y);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Dimension getSize() {
        return new Dimension(width, height);
    }

    public void setSize(Dimension d) {
        setSize(d.width, d.height);
    }

    public void setSize(int w, int h) {
        this.width = w;
        this.height = h;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    protected FontMetrics getFontMetrics() {
        return dummy.getFontMetrics(this.font);
    }

    public void setFont(Font font) {
        this.font = font;
    }

    public Font getFont() {
        return this.font;
    }

    public void setForeground(Color fgcolor) {
        this.fgcolor = fgcolor;
    }

    public Color getForeground() {
        return fgcolor;
    }

    public void setShadowImage(Image si) {
        this.shadowImage = si;
    }

    public void setBackground(Color bgcolor) {
        this.bgcolor = bgcolor;
        this.lightColor = bgcolor.brighter();
        this.darkColor = bgcolor.darker();
    }

    public void setLight(int light) {
        this.light = light;
    }

    public void calculateSize() { }

    public Object clone() {
        try {
            return super.clone();
        }
        catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }


    protected void paintBackground(
            Graphics g,
            Image backImage,
            int xx, int yy,
            int w, int h ) {

        Dimension d = getSize();
        int biw = backImage.getWidth(dummy);
        int bih = backImage.getHeight(dummy);
        Shape clip = g.getClip();
        g.clipRect(xx, yy, w, h);

        if (biw >= 1 && bih >= 1) {
            for (int x = 0; x < w; x += biw) {
                for (int y = 0; y < h; y += bih) {
                    g.drawImage(backImage, xx+x, yy+y, null);
                }
            }
        }
        g.setClip(clip);
    }

    public Animation fly(Point p) {
        return fly(p, 4);
    }


    /** Makes the actor move to given point in given time (millis)
      * Returns a reference to the animation object. */
    public Animation fly(Point p, final int shadow) {

        Point loc = getRootLocation();
        final double startx = loc.x;
        final double starty = loc.y;
        final double destx = p.x;
        final double desty = p.y;

        double xd = destx - startx;
        double yd = desty - starty;

        final double len = Math.sqrt(xd*xd + yd*yd);

        final double angle = Math.atan2(yd, xd);

        final double cos = Math.cos(angle);
        final double sin = Math.sin(angle);

        final int originalShadow = getShadow();

        return new Animation() {
            double x = startx;
            double y = starty;
            double l = 0;
            double step;

            double traceSpace = 20;
            double tracel = traceSpace;

            public void init() {
                this.addActor(Actor.this);
                step = len / getDuration();
                setShadow(shadow);
                x -= shadow;
                y -= shadow;
            }

            public void animate(double pulse) {
                setLocation((int)x, (int)y);
                x += pulse * step * cos;
                y += pulse * step * sin;
                l += pulse * step;

                if (trace != null) {
                    double ml = Math.min(l, len) - traceSpace;
                    while (tracel < ml) {
                        int trax =
                            (int)(startx + width/2 + tracel * cos);
                        int tray =
                            (int)(starty + height/2 + tracel * sin);
                        trace.putTrace(trax, tray);
                        tracel += traceSpace;
                    }
                }
                repaint();
            }

            public void finish() {
                setShadow(originalShadow);
                setLocation( (int)destx, (int)desty);
                this.repaint();
            }

            public void finalFinish() {
                this.passivate(Actor.this);
            }
        };
    }

    /** Returns an animation that makes the actor appear. Default
      * implementation shows the actor highlighted for given number of
      * milliseconds.
      */
    public Animation appear(final Point loc) {
        return new Animation() {
            public void init() {
                this.addActor(Actor.this);
                setLocation(loc);
                setLight(HIGHLIGHT);
                this.repaint();
            }

            public void animate(double pulse) { }

            public void finish() {
                setLight(NORMAL);
            }

            public void finalFinish() {
                this.passivate(Actor.this);
            }
        };
    }

    public Actor getActorAt(int x, int y) {
        return (x >= 0 && x < width &&
                y >= 0 && y < height) ?
               this : null;
    }

}

package jeliot.theater;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.util.Enumeration;
import java.util.Vector;

/**
  *
  * @author Niko Myller
  */
public class ObjectStage extends InstanceActor {


    /** Variable actors in this stage. */
    private Vector variables = new Vector();

    /** Name of the stage. */
    private String name;

    /** Height of the name. */
    private int nheight;

    /** Width of the name. */
    private int nwidth;

    /** Number of pixels around the divider line. */
    private int margin = 2;

    /** Number of pixels between actors. */
    private int actorMargin = 3;

    /** Maximum possible number of variables on the stage at the moment. */
    private int varCount = 0;

    /** Maximum width of the actors contained in this ObjectStage.
      */
    private int actWidth;

    /** Maximum height of the actors contained in this ObjectStage.
      */
    private int actHeight;

    /** If the variables should be drawn on top of this actor */
    private boolean paintVars = true;

    /**
      * Actor that is going to be bind to this ObjectStage but is not
      * yet bind. For example during appearing variables need their location
      * but their are bind after they have appeared.
      *
      * @see #reserve(Actor)
      */
    private Actor reserved;

    /**
      * RootLocation of the latest reserved actor.
      */
    private Point resLoc;

    /**
      * Constructor that sets the name and variable number to
      * the instance variables and set the insets.
      *
      * @param name     Name of the class that the instance represents.
      * @param varCount Number of variables that this instance has.
      */
    public ObjectStage(String name, int varCount) {
        this.name = name;
        this.varCount = varCount;
        insets = new Insets(2, 6, 4, 6);
    }

    /**
	 * @param name
	 * @return
	 */
	public VariableActor findVariableActor(String name) {
        Enumeration enum = variables.elements();
        while (enum.hasMoreElements()) {
            VariableActor va = (VariableActor) enum.nextElement();
            if (va.getName().equals(name)) {
                return va;
            }
        }
        //throw new RuntimeException("No Variable " + name);
        return null;
    }

    /**
	 * @param maxActWidth
	 * @param actHeight
	 */
	public void calculateSize(int maxActWidth, int actHeight) {

        this.actWidth = maxActWidth;
        this.actHeight = actHeight;

        Dimension d = calculateSizeDimensions();

        setSize(d.width, d.height);
    }

    /**
	 * @return
	 */
	public Dimension calculateSizeDimensions() {
        return calculateSizeDimensions(this.varCount);
    }

    /**
	 * @param varCount
	 * @return
	 */
	public Dimension calculateSizeDimensions(int varCount) {

        int w = borderWidth * 2 + insets.right + insets.left +
            Math.max(actWidth, nwidth) + 2 * margin;

        int h = borderWidth * 2 + insets.top + insets.bottom +
            nheight + 2 * margin + actorMargin +
            (actorMargin + actHeight) * varCount;

        return new Dimension(w, h);
    }

    /* (non-Javadoc)
	 * @see jeliot.theater.Actor#paintActor(java.awt.Graphics)
	 */
	public void paintActor(Graphics g) {
        int w = width;
        int h = height;
        int bw = borderWidth;

        int hgh = nheight + margin*3/2;

        // fill background
        g.setColor(light == HIGHLIGHT ? lightColor : bgcolor);
        g.fillRect(bw, hgh+2, w - 2 * bw, h - 2 * bw - hgh);
        g.setColor(lightColor);
        g.fillRect(bw, bw, w - 2 * bw, hgh - bw);

        // draw border
        g.setColor(darkColor);
        for (int i = 1; i < bw; ++i) {
            g.drawRect(i, i, w-i*2-1, h-i*2-1);
        }

        g.setColor(borderColor);
        g.drawRect(0, 0, w-1, h-1);

        // draw line
        g.drawRect(1, hgh, w - 2, 1);

        // draw name
        g.setFont(font);
        g.setColor(fgcolor);
        g.drawString(name, insets.left, insets.top + nheight);

        if (paintVars) {
            paintActors(g, variables);
        }
    }

    /* (non-Javadoc)
	 * @see jeliot.theater.Actor#setFont(java.awt.Font)
	 */
	public void setFont(Font font) {
        super.setFont(font);
        FontMetrics fm = getFontMetrics();
        nheight = fm.getHeight();
        nwidth = fm.stringWidth(name);
    }

    /**
	 * @param actor
	 * @return
	 */
	public Point reserve(Actor actor) {
        Actor prev = (variables.isEmpty()) ?
                      null :
                      (Actor)variables.lastElement();

        int y = ((prev == null) ?
                 insets.top + nheight + margin * 2 + borderWidth :
                 prev.getHeight() + prev.getY()) + actorMargin;

        int x = getWidth() - insets.right - actor.getWidth();

        reserved = actor;
        resLoc = new Point(x, y);
        Point rp = getRootLocation();
        rp.translate(x, y);
        return rp;
    }

    /**
	 * 
	 */
	public void bind() {
        reserved.setLocation(resLoc);
        variables.addElement(reserved);
        reserved.setParent(this);
    }

    /* (non-Javadoc)
	 * @see jeliot.theater.ActorContainer#removeActor(jeliot.theater.Actor)
	 */
	public void removeActor(Actor actor) {
        variables.removeElement(actor);
    }

    /* (non-Javadoc)
	 * @see jeliot.theater.Actor#getActorAt(int, int)
	 */
	public Actor getActorAt(int xc, int yc) {

        int n = variables.size();

        for (int i = n-1; i >= 0; --i) {
             Actor actor = (Actor)variables.elementAt(i);
             Actor at = actor.getActorAt(xc - actor.getX(),
                                         yc - actor.getY());
             if (at != null) {
                 return at;
             }
        }
        return super.getActorAt(xc, yc);
    }

    /* (non-Javadoc)
	 * @see jeliot.theater.Actor#appear(java.awt.Point)
	 */
	public Animation appear(final Point loc) {

        return new Animation() {
            Dimension size;
            double h;
            double plus;
            int full;
            public void init() {
                size = new Dimension(getWidth(), nheight + margin * 3);
                h = size.height;
                full = getHeight();
                plus = (full - h) / getDuration();
                this.addActor((Actor) ObjectStage.this);
                setLocation(loc);
                setSize(size);
                setLight(HIGHLIGHT);
                paintVars = false;
                repaint();
            }

            public void animate(double pulse) {
                h += plus * pulse;
                size.height = (int) h;
                setSize(size);
                this.repaint();
            }

            public void finish() {
                setLight(NORMAL);
                size.height = full;
                setSize(size);
                paintVars = true;
            }

            public void finalFinish() {
                this.passivate((Actor) ObjectStage.this);
            }
        };
    }

    /**
	 * @return
	 */
	public Animation disappear() {

        return new Animation() {
            Dimension size;
            double h;
            double plus;
            int full;
            public void init() {
                size = getSize();
                full = nheight + margin * 3;
                h = getHeight();
                plus = (full - h) / getDuration();
                this.addActor((Actor) ObjectStage.this);
                setSize(size);
                paintVars = false;
                repaint();
            }

            public void animate(double pulse) {
                h += plus * pulse;
                size.height = (int) h;
                setSize(size);
                this.repaint();
            }

            public void finish() {
                this.removeActor((Actor) ObjectStage.this);
            }
        };
    }
}

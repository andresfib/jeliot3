package jeliot.theatre;

import java.awt.*;
import java.util.*;

/**
  * @author Pekka Uronen
  *
  * created         9.8.1999
  * revised         18.9.1999
  *
  * The space allocation for variables need to be changed
  * also parameters need to be handled somehow.
  */
public class Stage extends Actor implements ActorContainer {

    //Indicates how many variables is defined in this scope
    private int scopeVarCount = 0;

    //Keeps track of scopes and the amount of variables in each scope.
    private Stack scopes = new Stack();

    /** Variable actors in this stage. */
    private Stack variables = new Stack();

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

    /** Maximum number of variables on the stage at any time. */
    private int varCount = 3;
    private int totalVarCount = 0;

    private int actWidth;
    private int actHeight;

    private boolean paintVars = true;

    private Actor reserved;
    private Point resLoc;

    public Stage(String name) {
        this.name = name;
        insets = new Insets(2, 6, 4, 6);
    }

    public VariableActor findVariableActor(String name) {
        for (int i = 0; i < variables.size(); i++) {
            VariableActor va = (VariableActor) variables.elementAt(i);
            if (name.equals(va.getName())) {
                return va;
            }
        }
        return null;
    }

    public void calculateSize(int maxActWidth, int actHeight) {

        this.actWidth = maxActWidth;
        this.actHeight = actHeight;

        Dimension d = calculateSizeDimensions();

        setSize(d.width, d.height);
    }

    public Dimension calculateSizeDimensions() {

        int w = borderWidth * 2 + insets.right + insets.left +
            Math.max(actWidth, nwidth) + 2 * margin;

        int h = borderWidth * 2 + insets.top + insets.bottom +
            nheight + 2 * margin + actorMargin +
            (actorMargin + actHeight) * this.varCount;

        return new Dimension(w, h);
    }

    public Dimension calculateSizeDimensions(int varCount) {

        int w = borderWidth * 2 + insets.right + insets.left +
            Math.max(actWidth, nwidth) + 2 * margin;

        int h = borderWidth * 2 + insets.top + insets.bottom +
            nheight + 2 * margin + actorMargin +
            (actorMargin + actHeight) * varCount;

        return new Dimension(w, h);
    }

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

    public void setFont(Font font) {
        super.setFont(font);
        FontMetrics fm = getFontMetrics();
        nheight = fm.getHeight();
        nwidth = fm.stringWidth(name);
    }

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

    public void bind() {
        reserved.setLocation(resLoc);
        variables.push(reserved);
        reserved.setParent(this);

        //Added for Jeliot 3
        totalVarCount++;
        scopeVarCount++;
    }

    //Added for Jeliot 3
    public void openScope() {
        scopes.push(new Integer(scopeVarCount));
        scopeVarCount = 0;
    }

    //Added for Jeliot 3
    public void closeScope() {
        for (int i = 0; i < scopeVarCount; i++) {
            variables.pop();
        }
        totalVarCount -= scopeVarCount;
        scopeVarCount = ((Integer) scopes.pop()).intValue();
    }

    public void removeActor(Actor actor) {
        variables.removeElement(actor);
    }

    public Actor getActorAt(int xc, int yc) {

        int n = variables.size();

        for (int i = n-1; i >= 0; --i) {
             Actor actor = (Actor)variables.elementAt(i);
             Actor at = actor.getActorAt(
                     xc - actor.getX(), yc - actor.getY());
             if (at != null) {
                 return at;
             }
        }
        return super.getActorAt(xc, yc);
    }

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
                this.addActor((Actor) Stage.this);
                setLocation(loc);
                setSize(size);
                setLight(HIGHLIGHT);
                paintVars = false;
                repaint();
            }

            public void animate(double pulse) {
                h += plus * pulse;
                size.height = (int)h;
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
                this.passivate((Actor)Stage.this);
            }
        };
    }

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
                this.addActor((Actor)Stage.this);
                setSize(size);
                paintVars = false;
                repaint();
            }

            public void animate(double pulse) {
                h += plus * pulse;
                size.height = (int)h;
                setSize(size);
                this.repaint();
            }

            public void finish() {
                this.removeActor((Actor)Stage.this);
            }
        };
    }

    public Animation extend()  {

        if ((totalVarCount + 1) > varCount) {

            varCount = totalVarCount + 1;

            return new Animation() {

                Dimension size, newSize;
                double h;
                double plus;
                int full;

                public void init() {
                    size = getSize();
                    h = size.height;
                    newSize = calculateSizeDimensions(totalVarCount + 1);
                    full = newSize.height;
                    plus = (full - h) / getDuration();
                    //setLight(HIGHLIGHT);
                    this.repaint();
                }

                public void animate(double pulse) {
                    h += plus * pulse;
                    size.height = (int)h;
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
                    //this.passivate((Actor)Stage.this);
                }
            };

        } else {

            return null;

        }
    }
}

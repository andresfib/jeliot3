package jeliot.theatre;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;

/**
  * @author Pekka Uronen
  *
  * created         5.10.1999
  */
public class BubbleActor extends Actor implements ActorContainer{

    String name;

    Actor speaker;
    Actor actor;
    Polygon tip;
    boolean bound;
    Point loc;

    public BubbleActor(Actor speaker) {
        this.speaker = speaker;
    }

    public void setActor(Actor actor) {
        reserve(actor);
        bind();
    }

    public Actor getActor() {
        return (Actor) this.actor.clone();
    }

    public Point reserve(Actor actor) {
        this.actor = actor;
        loc = new Point(insets.left, insets.top);
        calculateSize();
        Point rp = getRootLocation();
        rp.translate(loc.x, loc.y);
        return rp;
    }

    public void bind() {
        bound = true;
        actor.setParent(this);
        actor.setLocation(loc);
    }

    public void paintActors(Graphics g) {
        if (actor != null && bound) {
            g.translate(actor.getX(), actor.getY());
            actor.paintActor(g);
            g.translate(-actor.getX(), -actor.getY());
        }
    }

    public void paintActor(Graphics g) {
        int w = getWidth();
        int h = getHeight();

        // draw background
        g.setColor(bgcolor);
        g.fillRect(2, 2, w-4, h-4);

        // draw border
        g.setColor(darkColor);
        g.drawRect(1, 1, w-3, h-3);
        g.setColor(fgcolor);
        g.drawRect(0, 0, w-1, h-1);

        // draw tip
        if (tip != null) {
            g.setColor(bgcolor);
            g.fillPolygon(tip);
            g.setColor(fgcolor);
            g.drawPolygon(tip);
        }
        paintActors(g);
    }

    public void removeTip() {
        tip = null;
    }

    public void calculateSize() {
        setSize(actor.getWidth() + insets.right + insets.left,
                actor.getHeight()+ insets.bottom + insets.top);

        // tip
        Point sp = speaker.getRootLocation();
        int sx = sp.x;
        int sy = sp.y;
        int x = getX();
        int y = getY();

        int sw = speaker.getWidth();
        int sh = speaker.getHeight();
        int w = getWidth();
        int h = getHeight();

        int lefgap = sx - (x + w)  ;
        int riggap = x - (sx + sw) ;
        int topgap = sy - (y + h)  ;
        int botgap = y - (sy + sh) ;

        int stub =20;

        int xpp = lefgap > 0 ? lefgap : (
                riggap > 0 ? -riggap : sx - x + sw/2);

        int[] xps = { (w-stub)/2, xpp, (w+stub)/2 };
        int[] yps = { 0, -botgap, 0 };
        tip = new Polygon(xps, yps, 3);

    }

    public void removeActor(Actor actor) {
        if (actor == this.actor) {
            this.actor = null;
        }
    }

}

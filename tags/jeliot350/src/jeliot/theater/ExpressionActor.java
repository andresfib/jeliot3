package jeliot.theater;

import java.awt.Graphics;
import java.awt.Point;

/**
 * ExpressionActor represents a single line of a scratch the evaluation
 * area. It can contain any number of different actors inside that it
 * renders.
 * 
 * @author Pekka Uronen
 * @author Niko Myller
 */
public class ExpressionActor extends Actor implements ActorContainer {

    //DOC: Document!

    /**
     * identifis the ExpressionActors
     */
    private long id;

    /**
     *
     */
    private Actor[] actors;

    /**
     *
     */
    private Point[] locs;

    /**
     *
     */
    private boolean[] bound;

    /**
     *
     */
    private int next;

    /**
     *
     */
    private int margin = 2;

    /**
     * @param n
     */
    public ExpressionActor(int n) {
        actors = new Actor[n];
        locs = new Point[n];
        bound = new boolean[n];
    }

    /**
     * @param n
     * @param i
     */
    public ExpressionActor(int n, long i) {
        id = i;
        actors = new Actor[n];
        locs = new Point[n];
        bound = new boolean[n];
    }

    /**
     * @return
     */
    //Jeliot 3 addition
    public long getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * @param actor
     * @return
     */
    public Point reserve(Actor actor) {
        actors[next] = actor;
        int y = 0;
        int x = (next == 0) ?
                0 :
                locs[next - 1].x
                    + margin
                    + ((actors[next - 1] instanceof ReferenceActor) ?
                      ((ReferenceActor) actors[next - 1]).getReferenceWidth() :
                      actors[next - 1].getWidth());
        locs[next++] = new Point(x, y);
        Point rp = getRootLocation();
        rp.translate(x, y);
        return rp;
    }

    /**
     * @param actor
     */
    public void bind(Actor actor) {
        for (int i = 0; i < next; ++i) {
            if (actors[i] == actor) {
                bound[i] = true;
                actor.setParent(this);
                actor.setLocation(locs[i]);
                return;
            }
        }
        throw new RuntimeException();
    }

    /**
     * 
     */
    public void cut() {
        actors[--next] = null;
        bound[next] = false;
    }

    /* (non-Javadoc)
     * @see jeliot.theater.Actor#paintActor(java.awt.Graphics)
     */
    public void paintActor(Graphics g) {
        int n = next;
        for (int i = 0; i < n; ++i) {
            if (bound[i]) {
                g.translate(locs[i].x, locs[i].y);
                actors[i].paintActor(g);
                g.translate(-locs[i].x, -locs[i].y);
            }

        }
    }

    /* (non-Javadoc)
     * @see jeliot.theater.Actor#getHeight()
     */
    public int getHeight() {
        int n = next;
        int max = 0;
        for (int i = 0; i < n; ++i) {
            int y = actors[i].getHeight();
            max = y > max ? y : max;
        }
        return max;
    }

    /* (non-Javadoc)
     * @see jeliot.theater.ActorContainer#removeActor(jeliot.theater.Actor)
     */
    public void removeActor(Actor actor) {
        int n = next;
        for (int i = 0; i < n; ++i) {
            if (actors[i] == actor) {
                bound[i] = false;
            }
        }
    }

    /* (non-Javadoc)
     * @see jeliot.theater.Actor#setLight(int)
     */
    public void setLight(int light) {
        super.setLight(light);
        int n = next;
        for (int i = 0; i < n; ++i) {
            actors[i].setLight(light);
        }
    }
}
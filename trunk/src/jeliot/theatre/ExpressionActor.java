package jeliot.theatre;

import java.awt.*;

/**
  * @author Pekka Uronen
  *
  * created         17.8.1999
  * revised         21.9.1999
  * modified        12.12.2002 by Niko Myller
  */
public class ExpressionActor extends Actor implements ActorContainer{

    //Added for Jeliot 3 to identify the ExpressionActors
    int id;

    Actor[] actors;
    Point[] locs;
    boolean[] bound;
    int next;

    int margin = 2;

    ExpressionActor(int n) {
        actors = new Actor[n];
        locs = new Point[n];
        bound = new boolean[n];
    }

    ExpressionActor(int n, int i) {
        id = i;
        actors = new Actor[n];
        locs = new Point[n];
        bound = new boolean[n];
    }

    //Jeliot 3 addition
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public Point reserve(Actor actor) {
        actors[next] = actor;
        int y = 0;
        int x = (next == 0) ?
                0 :
                locs[next -1].x + margin + actors[next -1].getWidth();
        locs[next++] = new Point(x, y);
        Point rp = getRootLocation();
        rp.translate(x, y);
        return rp;
    }


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

    public void cut() {
        actors[--next] = null;
        bound[next] = false;
    }

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

    public int getHeight() {
        int n = next;
        int max = 0;
        for (int i = 0; i < n; ++i) {
            int y = actors[i].getHeight();
            max = y > max ? y : max;
        }
        return max;
    }

    public void removeActor(Actor actor) {
        int n = next;
        for (int i = 0; i < n; ++i) {
            if (actors[i] == actor) {
                bound[i] = false;
            }
        }
    }

    public void setLight(int light) {
        super.setLight(light);
        int n = next;
        for (int i = 0; i < n; ++i) {
            actors[i].setLight(light);
        }
    }
}

package jeliot.theatre;

import java.util.*;
import java.awt.*;
import jeliot.lang.*;
import jeliot.gui.*;

/**
  * @author Pekka Uronen
  *
  * created         19.8.1999
  */
public class Scratch extends Actor implements ActorContainer {

    Vector exprs = new Vector();

    Vector crap = new Vector();
    Vector crapRemovers = new Vector();


    //Added for Jeliot 3
    public ExpressionActor findActor(int number) {
        for (int i = 0; i < exprs.size(); i++) {
            ExpressionActor actor = (ExpressionActor) exprs.elementAt(i);
            if (actor.getId() == number) {
                return actor;
            }
        }
        return null;
    }

    //Second parameter added for Jeliot 3 to identify the expressions
    public ExpressionActor getExpression(int n, int id)  {
        ExpressionActor ea = new ExpressionActor(n, id);
        accommodate(ea);
        return ea;
    }

    public Animation fly(Point p) {
        return this.fly(p, 0);
    }

    public Point accommodate(Actor actor) {
        actor.setParent(this);
        int size = exprs.size();
        int y = 0;
        if (size > 0) {
            Actor prev = (Actor)exprs.elementAt(size-1);
            y = prev.getY() + prev.getHeight() + 4;
        }
        actor.setLocation(0, y);
        exprs.addElement(actor);
        setSize(getWidth(), y + actor.getHeight());

        return actor.getRootLocation();
    }

    public Point getSpot() {
        int y = 0;
        int size = exprs.size();
        if (size > 0) {
            Actor prev = (Actor)exprs.elementAt(size-1);
            y = prev.getY() + prev.getHeight() + 4;
        }
        return new Point(getX(), y + getY());

    }

    Point memloc;

    public void memorizeLocation() {
        memloc = getLocation();
    }

    public Point recallLocation() {
        return memloc;
    }

    public void paintActor(Graphics g) {
        paintActors(g, exprs);
    }

    public void registerCrap(Actor actor) {
        crap.addElement(actor);
    }

    public void registerCrapRemover(Runnable remover) {
        crapRemovers.addElement(remover);
    }

    public void removeCrap() {
        int n = crap.size();
        for (int i = 0; i < n; ++i) {
            Actor a = (Actor)crap.elementAt(i);
            ActorContainer cont = a.getParent();
            if (cont instanceof Theatre) {
                cont.removeActor(a);
                ((Theatre)cont).removePassive(a);
            }
        }
        crap.removeAllElements();

        int m = crapRemovers.size();
        for (int i = 0; i < m; ++i) {
            Runnable r = (Runnable)crapRemovers.elementAt(i);
            r.run();
        }
        crapRemovers.removeAllElements();
    }

    public void removeActor(Actor actor) {
        exprs.removeElement(actor);
    }

    public void clean() {
        exprs.removeAllElements();
        crap.removeAllElements();
        crapRemovers.removeAllElements();
    }

}


package jeliot.theater;

import java.awt.Graphics;
import java.awt.Point;
import java.util.Vector;

/**
 * Scratch controls the expression evaluation area. It
 * allocates the space for each <code>ExpressionEvaluationActor</code>
 * and possible other <code>Actor</code>s that area there
 * temporarily.
 * 
 * @author Pekka Uronen
 * @author Niko Myller
 */
public class Scratch extends Actor implements ActorContainer {

//  DOC: Document!
    /**
	 *
	 */
	Vector exprs = new Vector();

    /**
	 *
	 */
	Vector crap = new Vector();
    /**
	 *
	 */
	Vector crapRemovers = new Vector();

    /**
	 * @param number
	 * @return
	 */
	public ExpressionActor findActor(long number) {
        for (int i = 0; i < exprs.size(); i++) {
            ExpressionActor actor = (ExpressionActor) exprs.elementAt(i);
            if (actor.getId() == number) {
                return actor;
            }
        }
        return null;
    }

    
    /**
     * Second parameter added for Jeliot 3 to identify the expressions.
	 * @param n
	 * @param id
	 * @return
	 */
	public ExpressionActor getExpression(int n, long id)  {
        ExpressionActor ea = new ExpressionActor(n, id);
        accommodate(ea);
        return ea;
    }

    /* (non-Javadoc)
	 * @see jeliot.theater.Actor#fly(java.awt.Point)
	 */
	public Animation fly(Point p) {
        return this.fly(p, 0);
    }

    /**
	 * @param actor
	 * @return
	 */
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

    /**
	 * @return
	 */
	public Point getSpot() {
        int y = 0;
        int size = exprs.size();
        if (size > 0) {
            Actor prev = (Actor)exprs.elementAt(size-1);
            y = prev.getY() + prev.getHeight() + 4;
        }
        return new Point(getX(), y + getY());

    }

    /**
	 *
	 */
	Point memloc;

    /**
	 * 
	 */
	public void memorizeLocation() {
        memloc = getLocation();
    }

    /**
	 * @return
	 */
	public Point recallLocation() {
        return memloc;
    }

    /* (non-Javadoc)
	 * @see jeliot.theater.Actor#paintActor(java.awt.Graphics)
	 */
	public void paintActor(Graphics g) {
        paintActors(g, exprs);
    }

    /**
	 * @param actor
	 */
	public void registerCrap(Actor actor) {
        crap.addElement(actor);
    }

    /**
	 * @param remover
	 */
	public void registerCrapRemover(Runnable remover) {
        crapRemovers.addElement(remover);
    }

    /**
	 * @param actor
	 */
	public void removeCrap(Actor actor) {
        crap.removeElement(actor);
        ActorContainer cont = actor.getParent();
        if (cont instanceof Theater) {
            cont.removeActor(actor);
            ((Theater)cont).removePassive(actor);
        }
    }

    /**
	 * 
	 */
	public void removeCrap() {
        int n = crap.size();
        for (int i = 0; i < n; ++i) {
            Actor a = (Actor)crap.elementAt(i);
            ActorContainer cont = a.getParent();
            if (cont instanceof Theater) {
                cont.removeActor(a);
                ((Theater)cont).removePassive(a);
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

    /* (non-Javadoc)
	 * @see jeliot.theater.ActorContainer#removeActor(jeliot.theater.Actor)
	 */
	public void removeActor(Actor actor) {
        exprs.removeElement(actor);
    }

    /**
	 * 
	 */
	public void clean() {
        exprs.removeAllElements();
        removeCrap();
    }

}


package jeliot.theater;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.util.Vector;

/**
 * This is the theatre component that is added in the left pane of the
 * user interface and on which the program animation produced in the theater
 * package is currently drawn.
 *
 * @author Pekka Uronen
 * @author Niko Myller
 */
public class Theater extends javax.swing.JComponent implements ActorContainer {

	/**
     * Background image.
     */
    private Image backImage;

	/**
     * Captured image of the screen, used on active mode for extra efficiency.
     */
    private Image captScreen;

	/**
     * Graphics object for captured image when the animation is going on.
     */
    private Graphics csg;

	/** True, if the theatre is in active mode or captured. Active mode
      * means that something is or is going to be animated. This means
      * that the extra efficiency is needed and needless painting of all
      * the actors is not done.
      * @see Animation
      */
    private boolean active;

	/**
     * Vector of passive actors which are drawn in passive mode.
     */
    private Vector pasAct = new Vector();

	/**
     * Vector of active, moving actors which are drawn in active
     * mode (during animation).
     */
    private Vector actAct = new Vector();

	/**
     * Highlighted actor if any.
     */
    private Actor highActor;

    /**
	 *
	 */
	private TheaterManager manager = new TheaterManager(this);

	/** Variable is set true if there are other <code>JComponents</code>
      * on the Theatre component. At the moment this happens only when
      * input is requested. The state of the variable changes the
      * operation of the <code>paint</code> method.
      *
      * @see #paint(Graphics g)
      */
    private boolean showComponents;


	/**
     * Sets the opaque of the component to be true.
     *
     * @see #setOpaque(boolean)
     */
    public Theater() {
        setOpaque(true);
    }

	/**
     * Returns the TheatreManager
     * @return TheatreManager object
     */
    public TheaterManager getManager() {
        return manager;
    }

	/**
     * Sets the background image (<code>backImage</code>) of this theatre.
     *
     * @param backImage Image for background.
     */
    public void setBackground(Image backImage) {
        this.backImage = backImage;
    }

	/**
     * Paints the theatre.
     * If theatre is in active mode then a captured picture and only active
     * actors are painted otherwise background, the passive, highlighted
     * and active actors are painted.
     *
     * @param g Everything is painted on the given Graphics object.
     */
    public void paintComponent(Graphics g) {
        //Whether or not we are in the middle of animation.
        if (active) {
            //We are in the middle of animation and the captured image
            //is painted on the theatre.
            synchronized (csg) {
                paintCapturedScreen(g);
            }
        } else {
            //We are not in the middle of animation and
            //background, the passive and highlighted
            //actors are painted.
            paintBackground(g);
            paintActors(g, pasAct);
            paintHighlight(g);
        }
        //Finally the active actors are painted.
        paintActors(g, actAct);
    }

    /* (non-Javadoc)
	 * @see java.awt.Component#paint(java.awt.Graphics)
	 */
	public void paint(Graphics g) {
        if (showComponents) {
            super.paint(g);
        }
        else {
            paintComponent(g);
        }
    }

    /**
     * Paints the image of captured screen.
	 * @param g
	 */
    private void paintCapturedScreen(Graphics g) {
        g.drawImage(captScreen, 0, 0, null);
    }

    /**
     * Fills the background with background image.
	 * @param g
	 */
    private void paintBackground(Graphics g) {
        Dimension d = getSize();
        int w = d.width;
        int h = d.height;
        int biw = backImage.getWidth(this);
        int bih = backImage.getHeight(this);

        if (biw >= 1 || bih >= 1) {
            for (int x = 0; x < w; x += biw) {
                for (int y = 0; y < h; y += bih) {
                    g.drawImage(backImage, x, y, this);
                }
            }
        }
    }



    /**
     * Paints the actors contained in given vector.
	 * @param g
	 * @param actors
	 */
    private void paintActors(Graphics g, Vector actors) {
        synchronized(actors) {
            int n = actors.size();

            for (int i = 0; i < n; ++i) {
                Actor act = (Actor)actors.elementAt(i);
                int x = act.getX();
                int y = act.getY();
                g.translate(x, y);
                act.paintShadow(g);
                act.paintActor(g);
                g.translate(-x, -y);
            }

            /* Old version: Not valid code.
            for (int i = 0; i < n; ++i) {
                Actor act = (Actor)actors.elementAt(i);

                int x = act.getX();
                int y = act.getY();
                g.translate(x, y);
                act.paintActor(g);
                g.translate(-x, -y);
            }
            */
        }
    }

    /**
     * Paints the highlight marker around highlighted actor.
	 * @param g
	 */
    private void paintHighlight(Graphics g) {
        if (highActor != null) {
            Point loc = highActor.getRootLocation();
            int x = loc.x;
            int y = loc.y;
            int w = highActor.getWidth();
            int h = highActor.getHeight();

            g.setColor(Color.white);
            g.drawRect(x-1, y-1, w+1, h+1);
            g.drawRect(x-3, y-3, w+5, h+5);
            g.setColor(Color.black);
            g.drawRect(x-2, y-2, w+3, h+3);
        }
    }

//  DOC: Document!
    /**
	 * @param actor
	 */
	public void addPassive(Actor actor) {
        pasAct.addElement(actor);
        actor.setParent(this);
    }

    /**
	 * @param actor
	 */
	public void removePassive(Actor actor) {
        pasAct.removeElement(actor);
        if (actor == highActor) {
            highActor = null;
        }
    }

    /**
	 * @param actor
	 */
	public void addActor(Actor actor) {
        actAct.addElement(actor);
        actor.setParent(this);
    }

    /**
	 * @param actor
	 */
	public void promote(Actor actor) {
        if (pasAct.contains(actor)) {
            pasAct.removeElement(actor);
            actAct.addElement(actor);
        }
        else {
            addActor(actor);
        }
    }

    /**
	 * @param actor
	 */
	public void passivate(Actor actor) {
        if (actAct.contains(actor)) {
            actAct.removeElement(actor);
        }
        pasAct.addElement(actor);
    }

    /* (non-Javadoc)
	 * @see jeliot.theater.ActorContainer#removeActor(jeliot.theater.Actor)
	 */
	public void removeActor(Actor actor) {
        if (actAct.contains(actor)) {
            actAct.removeElement(actor);
        }
        else {
            pasAct.removeElement(actor);
        }
    }

    /* (non-Javadoc)
	 * @see java.awt.Component#getWidth()
	 */
	public int getWidth() {
        return getSize().width;
    }

    /* (non-Javadoc)
	 * @see java.awt.Component#getHeight()
	 */
	public int getHeight() {
        return getSize().height;
    }

    /**
	 * 
	 */
	public void updateCapture() {
        int w = getWidth();
        int h = getHeight();
        if (captScreen == null || captScreen.getWidth(null) != w ||
                captScreen.getHeight(null) != h) {

            captScreen = createImage(w, h);
            csg = captScreen.getGraphics();
        }
        synchronized (csg) {
            paintBackground(csg);
            paintActors(csg, pasAct);
        }
    }

    /**
	 * 
	 */
	public void capture() {
        updateCapture();
        active = true;
        flush();
    }

    /**
	 * 
	 */
	public void release() {
        active = false;
        flush();
    }

    /**
	 * 
	 */
	public void cleanUp() {
        removeAll();
        actAct.removeAllElements();
        pasAct.removeAllElements();
        manager.cleanUp();
    }

    /**
	 * 
	 */
	public void flush() {
        repaint();
        try {
            Thread.sleep(50);
        }
        catch (InterruptedException e) { }
    }

    /**
	 * @param x
	 * @param y
	 * @return
	 */
	public Actor getActorAt(int x, int y) {
        synchronized(pasAct) {
            int n = pasAct.size();
            for (int i = n-1; i >= 0; --i) {
                Actor actor = (Actor)pasAct.elementAt(i);
                Actor at = actor.getActorAt(
                        x - actor.getX(), y - actor.getY());
                if (at != null) {
                    return at;
                }
            }
        }
        return null;
    }

    /**
	 * @param actor
	 */
	public void setHighlightedActor(Actor actor) {
        if (actor != highActor) {
            this.highActor = actor;
            repaint();
        }
    }

    /**
	 * @param show
	 */
	public void showComponents(boolean show) {
        this.showComponents = show;
    }

    /**
	 * @return
	 */
	public boolean isCaptured() {
        return active;
    }

}

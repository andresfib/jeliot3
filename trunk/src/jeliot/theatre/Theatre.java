package jeliot.theatre;

import java.util.*;
import java.awt.*;
import jeliot.parser.*;
import jeliot.lang.*;
import jeliot.gui.*;

/**
  * @author Pekka Uronen
  *
  * created         9.8.1999
  */
public class Theatre extends javax.swing.JComponent implements ActorContainer {

    /** Background image, drawn behind everything */
    private Image backImage;

    /** Captured image of the screen, used on active mode for extra
      * efficiency. */
    private Image captScreen;

    /** Graphics object for captured image. */
    private Graphics csg;

    /** True, if the theatre is in active mode. */
    private boolean active;

    /** Vector of passive actors which are drawn in passive mode. */
    private Vector pasAct = new Vector();

    /** Vector of active, moving actors which are drawn in active
      * mode.
      */
    private Vector actAct = new Vector();

    /** Highlighted actor */
    private Actor highActor;

    private TheatreManager manager = new TheatreManager(this);

    private boolean showComponents;

    public Theatre() {
        setOpaque(true);
    }

    public TheatreManager getManager() {
        return manager;
    }

    /** Sets the background image of this theatre.
      */
    public void setBackground(Image backImage) {
        this.backImage = backImage;
    }

    /** Paints the theatre.
      */
    public void paintComponent(Graphics g) {
        if (active) {
            synchronized (csg) {
                paintCapturedScreen(g);
            }
        }
        else {
            paintBackground(g);
            paintActors(g, pasAct);
            paintHighlight(g);
        }
        paintActors(g, actAct);
    }

    public void paint(Graphics g) {
        if (showComponents) {
            super.paint(g);
        }
        else {
            paintComponent(g);
        }
    }

    /** Paints the image of captured screen.
      */
    private void paintCapturedScreen(Graphics g) {
        g.drawImage(captScreen, 0, 0, null);
    }

    /** Fills the background with background image.
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

    /** Paints the actors contained in given vector.
      */
    private void paintActors(Graphics g, Vector actors) {
        synchronized(actors) {
            int n = actors.size();
//            System.out.println(n);
            for (int i = 0; i < n; ++i) {
                Actor act = (Actor)actors.elementAt(i);
                int x = act.getX();
                int y = act.getY();
                g.translate(x, y);
                act.paintShadow(g);
                act.paintActor(g);
                g.translate(-x, -y);
            }
           /* for (int i = 0; i < n; ++i) {
                Actor act = (Actor)actors.elementAt(i);

                int x = act.getX();
                int y = act.getY();
                g.translate(x, y);
                act.paintActor(g);
                g.translate(-x, -y);
            }*/
        }
    }

    /** Paints the highlight marker around highlighted actor.
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

    public void addPassive(Actor actor) {
        pasAct.addElement(actor);
        actor.setParent(this);
    }

    public void removePassive(Actor actor) {
        pasAct.removeElement(actor);
        if (actor == highActor) {
            highActor = null;
        }
    }

    public void addActor(Actor actor) {
        actAct.addElement(actor);
        actor.setParent(this);
    }

    public void promote(Actor actor) {
        if (pasAct.contains(actor)) {
            pasAct.removeElement(actor);
            actAct.addElement(actor);
        }
        else {
            addActor(actor);
        }
    }

    public void passivate(Actor actor) {
        if (actAct.contains(actor)) {
            actAct.removeElement(actor);
        }
        pasAct.addElement(actor);
    }

    public void removeActor(Actor actor) {
        if (actAct.contains(actor)) {
            actAct.removeElement(actor);
        }
        else {
            pasAct.removeElement(actor);
        }
    }

    public Scratch requestScratch() {
        Scratch scratch = new Scratch();
        manager.addScratch(scratch);
        pasAct.addElement(scratch);
        return scratch;
    }

    public void removeScratch(Scratch scratch) {
        manager.removeScratch(scratch);
        removeActor(scratch);
    }

    public int getWidth() {
        return getSize().width;
    }

    public int getHeight() {
        return getSize().height;
    }

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

    public void capture() {
        updateCapture();
        active = true;
        flush();
    }

    public void release() {
        active = false;
        flush();
    }

    public void cleanUp() {
        removeAll();
        actAct.removeAllElements();
        pasAct.removeAllElements();
        manager.cleanUp();
    }

    public void flush() {
        repaint();
        try {
            Thread.sleep(100);
        }
        catch (InterruptedException e) { }
    }

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

    public void setHighlightedActor(Actor actor) {
        if (actor != highActor) {
            this.highActor = actor;
            repaint();
        }
    }

    public void showComponents(boolean show) {
        this.showComponents = show;
    }

    public boolean isCaptured() {
        return active;
    }

}

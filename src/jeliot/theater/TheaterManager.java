package jeliot.theater;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Stack;
import java.util.Vector;

/**
  *
  * @author Pekka Uronen
  * @author Niko Myller
  */
public class TheaterManager implements ComponentListener {

    /**
      * Contains Points that are set as the rootlocations of the
      * new MethodStage instances. They are circulated so that
      * after the last value of the table the first value is used
      * again.
      */
    private Point[] methodStagePoints = {new Point(10, 20),
                                         new Point(20, 30),
                                         new Point(30, 40),
                                         new Point(15, 50),
                                         new Point(25, 45)};

    /**
      * Reference to the current Theatre instance.
      * Reference is need for two reasons:
      * Firstly, to set this TheatreManager instance as the
      * ComponentListener of the Theatre and then assign the actors in
      * correct places when the Theatre (<code>JComponent</code>)
      * resized. Secondly, for inserting new actors to the passive
      * (<code>pasAct</code>) and active (<code>actAct</code>) actors.
      */
    private Theater theatre;

    /** */
    private Stack methods = new Stack();

    /** */
    private Vector objects = new Vector();

    /** */
    private Vector scratches = new Vector();

    /** */
    private ConstantBox constantBox;

    /** */
    private int maxMethodStageX;

    /** */
    private Hashtable reservations = new Hashtable();

    /** */
    private Point lrCorner;

    private int minInstanceY = Integer.MAX_VALUE;
    private int minInstanceX = Integer.MAX_VALUE;

    /** */
    public TheaterManager(Theater theatre) {
        this.theatre = theatre;
        theatre.addComponentListener(this);
        Dimension d = theatre.getSize();
        lrCorner = new Point(d.width, d.height);
    }

    /** */
    public void cleanUp() {
        methods.removeAllElements();
        objects.removeAllElements();
        scratches.removeAllElements();
        constantBox = null;
        minInstanceY = Integer.MAX_VALUE;
        minInstanceX = Integer.MAX_VALUE;
    }


    /** */
    public Point reserve(MethodStage stage) {
        Point loc = methodStagePoints[methods.size() % methodStagePoints.length];
        reservations.put(stage, loc);

        return loc;
    }


    /** */
    public void bind(MethodStage stage) {
        Point loc = (Point)reservations.remove(stage);
        methods.push(stage);
        stage.setLocation(loc);
        theatre.passivate(stage);

        maxMethodStageX = 0;
        int n = methods.size();
        for (int i = 0; i < n; ++i) {
            MethodStage s = (MethodStage) methods.elementAt(i);
            int wx = s.getX() + s.getWidth();
            if (wx > maxMethodStageX) {
                maxMethodStageX = wx;
            }
        }
    }


    /** */
    public Point reserve(InstanceActor actor) {
        int w = actor.getWidth();
        int h = actor.getHeight();

        int x = objects.isEmpty() ?
                theatre.getWidth() - w - 45 :
                ((Actor)objects.lastElement()).getX() - w - 45;
        int y = theatre.getHeight() - h - 10;
        Point loc = new Point(x, y);
        reservations.put(actor, loc);

        return loc;
    }


    /** */
    public void bind(InstanceActor actor) {
        Point loc = (Point)reservations.remove(actor);
        objects.addElement(actor);
        theatre.passivate(actor);
        actor.setLocation(loc);
        if (loc.y < minInstanceY) {
            minInstanceY = loc.y;
        }
        if (loc.x < minInstanceX) {
            minInstanceX = loc.x;
        }
    }


    /** */
    public void removeInstance(InstanceActor actor) {
        //move also minInstanceX and -Y
        objects.removeElement(actor);
        theatre.removePassive(actor);
        theatre.flush();
    }


    /** */
    public void removeMethodStage(MethodStage stage) {
        methods.removeElement(stage);
        theatre.removePassive(stage);
    }


    /** */
    public void setConstantBox(ConstantBox cbox) {
        this.constantBox = cbox;
        positionConstantBox();
    }


    /** */
    public void addScratch(Scratch scratch) {
        scratches.addElement(scratch);
        scratch.setLocation(getScratchPositionX(), 10);
        theatre.addPassive(scratch);
    }

    public int getScratchPositionX() {
        if (!methods.empty()) {
            return maxMethodStageX + 45;
        } else {
            return (ActorFactory.getMaxMethodStageWidth()) + 45;
        }
    }

    /** */
    public void removeScratch(Scratch scratch) {
        scratches.removeElement(scratch);
        theatre.removePassive(scratch);
    }


    /** */
    private void positionConstantBox() {
        if (constantBox != null) {
            int x = 10; //theatre.getWidth() - 10 - cbox.getWidth();
            int y = theatre.getHeight() - 10 - constantBox.getHeight();
            constantBox.setLocation(x, y);
        }
    }

    public int getConstantPositionY() {
        //Change this when static variables are visualized!
        return theatre.getHeight() - 10 - constantBox.getHeight();
    }

    public int getMinInstanceY() {
        return minInstanceY;
    }

    public int getMinInstanceX() {
        return minInstanceX;
    }

    /** */
    public void positionObjects(Point from, Point to) {
        Enumeration enum = objects.elements();
        while (enum.hasMoreElements()) {
            Actor actor = (Actor)enum.nextElement();
            Point loc = actor.getLocation();
            actor.setLocation(
                    loc.x + to.x - from.x,
                    loc.y + to.y - from.y);
        }
    }


    /** */
    public Point getOutputPoint() {
        Dimension d = theatre.getSize();
        return new Point(d.width/2, d.height);
    }


    /** Draws the lines separating different areas
     *  and writes texts on them.
     */
    public void setLinesAndText(LinesAndText lat) {
        lat.setManager(this);
        lat.setTheatre(theatre);
    }

    /** Called, when the theatre object is resized. Rearranges the
      * theatre after resizing.
      */
    public void componentResized(ComponentEvent e) {
        positionConstantBox();
        Dimension d = theatre.getSize();
        positionObjects(
                lrCorner,
                lrCorner = new Point(d.width, d.height));
        theatre.repaint();
    }

    /** These methods are needed to conform to the ComponentListener
      * interface.
      */
    public void componentMoved(ComponentEvent e) { }
    public void componentShown(ComponentEvent e) { }
    public void componentHidden(ComponentEvent e) { }

}

package jeliot.theatre;

import java.util.*;
import java.awt.*;
import java.awt.event.*;

/**
  * @author Pekka Uronen
  *
  * created         18.9.1999
  */
public class TheatreManager implements ComponentListener {

    private Point[] stagep = {
        new Point(10, 10),
        new Point(20, 20),
        new Point(30, 30),
        new Point(15, 40),
        new Point(25, 35)};

    private Theatre theatre;

    private Stack stages = new Stack();
    private Vector objects = new Vector();
    private Vector scratches = new Vector();

    private ConstantBox cbox;

    //private ConstantBox input;

    private int maxStagex;

    private Hashtable reservations = new Hashtable();

    private Point lrCorner;

    public TheatreManager(Theatre theatre) {
        this.theatre = theatre;
        theatre.addComponentListener(this);
        Dimension d = theatre.getSize();
        lrCorner = new Point(d.width, d.height);
    }

    public void cleanUp() {
        stages.removeAllElements();
        objects.removeAllElements();
        scratches.removeAllElements();
        cbox = null;
    }

    public Point reserve(Stage stage) {
        Point loc = stagep[stages.size() % stagep.length];
        reservations.put(stage, loc);

        return loc;
    }

    public void bind(Stage stage) {
        Point loc = (Point)reservations.remove(stage);
        stages.push(stage);
        stage.setLocation(loc);
        theatre.passivate(stage);

        maxStagex = 0;
        int n = stages.size();
        for (int i = 0; i < n; ++i) {
            Stage s = (Stage)stages.elementAt(i);
            int wx = s.getX() + s.getWidth();
            if (wx > maxStagex) {
                maxStagex = wx;
            }
        }
    }

    public Point reserve(InstanceActor actor) {
        int w = actor.getWidth();
        int h = actor.getHeight();

        int x = objects.isEmpty() ?
                theatre.getWidth() - w - 10 :
                ((Actor)objects.lastElement()).getX() - w - 10;
        int y = theatre.getHeight() - h - 10;
        Point loc = new Point(x, y);
        reservations.put(actor, loc);

        return loc;
    }

    public void bind(InstanceActor actor) {
        Point loc = (Point)reservations.remove(actor);
        objects.addElement(actor);
        theatre.passivate(actor);
        actor.setLocation(loc);
    }

    public void removeInstance(InstanceActor actor) {
        objects.removeElement(actor);
        theatre.removePassive(actor);
        theatre.repaint();
    }

    public void removeStage(Stage stage) {
        stages.removeElement(stage);
        theatre.removePassive(stage);
    }

    public void setConstantBox(ConstantBox cbox) {
        this.cbox = cbox;
        positionConstantBox();
    }

/*
    public void setInputBox(ConstantBox input) {
        this.input = input;
        positionInputBox();
    }
*/

    public void addScratch(Scratch scratch) {
        scratches.addElement(scratch);
        if (!stages.empty()) {
            scratch.setLocation(maxStagex + 20, 10);
        } else {
            scratch.setLocation(ActorFactory.getTypeValueWidth(8) + 100, 10);
        }
        theatre.addPassive(scratch);
    }

    public void removeScratch(Scratch scratch) {
        scratches.removeElement(scratch);
        theatre.removePassive(scratch);
    }

    private void positionConstantBox() {
        if (cbox != null) {
            int x = 10; //theatre.getWidth() - 10 - cbox.getWidth();
            int y = theatre.getHeight() - 10 - cbox.getHeight();
            cbox.setLocation(x, y);
        }
    }

/*    private void positionInputBox() {
        if (input != null) {
            //There is now 50 pixels between constant box and input box
            int x = 60 + cbox.getWidth();
            int y = theatre.getHeight() - 10 - input.getHeight();
            input.setLocation(x, y);
        }
    }
*/
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


    public Point getOutputPoint() {
        Dimension d = theatre.getSize();
        return new Point(d.width/2, d.height);
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
    }

    /** These methods are needed to conform to the ComponentListener
      * interface.
      */
    public void componentMoved(ComponentEvent e) { }
    public void componentShown(ComponentEvent e) { }
    public void componentHidden(ComponentEvent e) { }

}

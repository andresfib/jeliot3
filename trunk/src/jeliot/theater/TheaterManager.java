package jeliot.theater;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.Hashtable;
import java.util.Stack;
import java.util.Vector;

/**
 * <code>TheaterManager</code> allocates the space for all
 * <code>InstanceActor</code>s, <code>MethodStage</code>s,
 * <code>Scratch</code>es and constants (<code>ConstantBox</code>), and also listens the <code>Theater</code>
 * component for resizes so the the allocation of the space
 * is valid after resizing of the <code>Theater</code> component.
 *
 * @author Pekka Uronen
 * @author Niko Myller
 * 
 * @see jeliot.theater.InstanceActor
 * @see jeliot.theater.MethodStage
 * @see jeliot.theater.Scratch
 * @see jeliot.theater.ConstantBox
 * @see jeliot.theater.Theater
 */
public class TheaterManager implements ComponentListener {



	/**
      * Contains Points that are set as the rootlocations of the
      * new MethodStage instances. They are circulated so that
      * after the last value of the table the first value is used
      * again.
      */
    private static Point[] methodStagePoints = {new Point(10, 20),
                                         		new Point(20, 30),
                                         		new Point(30, 40),
                                         		new Point(15, 50),
                                         		new Point(25, 45)};
    private static int maxMethodStageInsetX = 30;

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

//  DOC: Document!

    /**
	 *
	 */
    private Stack methods = new Stack();

    /**
	 *
	 */
    private Vector objects = new Vector();

    /**
	 *
	 */
    private Vector scratches = new Vector();

    /**
	 *
	 */
    private ConstantBox constantBox;

    /**
	 *
	 */
    private int maxMethodStageX;

    /**
	 *
	 */
    private Hashtable reservations = new Hashtable();

    /**
	 *
	 */
    private Point lrCorner;

    /**
	 *
	 */
	//private int minInstanceY = Integer.MAX_VALUE;
    
    /**
	 *
	 */
	//private int minInstanceX = Integer.MAX_VALUE;

    /**
	 * @param theatre
	 */
    public TheaterManager(Theater theatre) {
        this.theatre = theatre;
        theatre.addComponentListener(this);
        Dimension d = theatre.getSize();
        lrCorner = new Point(d.width, d.height);
    }

    public static int getMaxMethodInset() {
        return maxMethodStageInsetX;
    }
    
    /**
	 * 
	 */
    public void cleanUp() {
        methods.removeAllElements();
        objects.removeAllElements();
        scratches.removeAllElements();
        constantBox = null;
        //minInstanceY = Integer.MAX_VALUE;
        //minInstanceX = Integer.MAX_VALUE;
    }


    /**
	 * @param stage
	 * @return
	 */
    public Point reserve(MethodStage stage) {
        Point loc = methodStagePoints[methods.size() % methodStagePoints.length];
        reservations.put(stage, loc);
        return loc;
    }


    /**
	 * @param stage
	 */
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
                //System.out.println("Stage width: " + maxMethodStageX);
            }
        }
    }


    /**
	 * @param actor
	 * @return
	 */
    public Point reserve(InstanceActor actor) {
        int w = actor.getWidth();
        int h = actor.getHeight();

        int x = objects.isEmpty() ?
                getMinInstanceX() + 20 :
                    ((Actor)objects.lastElement()).getX() +((Actor)objects.lastElement()).getWidth() + 40;
        int y = getMinInstanceY() + 30;
        
        /*
        int x = objects.isEmpty() ?
                theatre.getWidth() - w - 45 :
                ((Actor)objects.lastElement()).getX() - w - 45;
        int y = theatre.getHeight() - h - 10;
        */
        
        Point loc = new Point(x, y);
        reservations.put(actor, loc);
        
        actor.setPosition(objects.size());
        
        return loc;
    }


    /**
	 * @param actor
	 */
    public void bind(InstanceActor actor) {
        Point loc = (Point)reservations.remove(actor);
        objects.addElement(actor);
        theatre.passivate(actor);
        actor.setLocation(loc);
        /*
        if (loc.y < minInstanceY) {
            minInstanceY = loc.y;
        }
        if (loc.x < minInstanceX) {
            minInstanceX = loc.x;
        }
        */
        
        Dimension d = theatre.getSize();
        
        if (d.width < loc.x + actor.getWidth() + 35) {
            d.width = loc.x + actor.getWidth() + 35;
        }
        if (d.height < loc.y + actor.getHeight() + 35) {
            d.height = loc.y + actor.getHeight() + 35;
        }
        theatre.setPreferredSize(d);
        theatre.revalidate();
    }


    /**
	 * @param actor
	 */
    public void removeInstance(InstanceActor actor) {
        //move also minInstanceX and -Y
        objects.removeElement(actor);
        theatre.removePassive(actor);
        theatre.flush();
    }


    /**
	 * @param stage
	 */
    public void removeMethodStage(MethodStage stage) {
        methods.removeElement(stage);
        theatre.removePassive(stage);
    }


    /**
	 * @param cbox
	 */
    public void setConstantBox(ConstantBox cbox) {
        this.constantBox = cbox;
        positionConstantBox();
    }


    /**
	 * @param scratch
	 */
    public void addScratch(Scratch scratch) {
        scratches.addElement(scratch);
        scratch.setLocation(getScratchPositionX(), 20);
        theatre.addPassive(scratch);
    }

    /**
	 * @return
	 */
	public static int getScratchPositionX() {
	    return 250;
	    /*
        if (!methods.empty()) {
            return 235;
        } else {
            return (ActorFactory.getMaxMethodStageWidth()) + 45;
        }
        */
    }

    /**
	 * @param scratch
	 */
    public void removeScratch(Scratch scratch) {
        scratches.removeElement(scratch);
        theatre.removePassive(scratch);
    }


    /**
	 * 
	 */
    private void positionConstantBox() {
        if (constantBox != null) {
            int x = 10; //theatre.getWidth() - 10 - cbox.getWidth();
            int y = getConstantPositionY() + 10;
            constantBox.setLocation(x, y);
        }
    }

    /**
	 * @return
	 */
	public static int getConstantPositionY() {
        //Change this when static variables are visualized!
        return 338;
    }

    /**
	 * @return
	 */
	public static int getMinInstanceY() {
	    return 258;
        //return minInstanceY;
    }

    /**
	 * @return
	 */
	public static int getMinInstanceX() {
	    return 250;
        //return minInstanceX;
    }

    /**
	 * @param from
	 * @param to
	 */
    public void positionObjects(Point from, Point to) {
        /*
        Enumeration enum = objects.elements();
        while (enum.hasMoreElements()) {
            Actor actor = (Actor)enum.nextElement();
            Point loc = actor.getLocation();
            actor.setLocation(
                    loc.x + to.x - from.x,
                    loc.y + to.y - from.y);
        }
        */
    }


    /**
	 * @return
	 */
    public Point getOutputPoint() {
        Dimension d = theatre.getSize();
        return new Point(d.width/2, d.height);
    }


	/**
     * Draws the lines separating different areas
     * and writes texts on them.
     * @param lat
     */
    public void setLinesAndText(LinesAndText lat) {
        lat.setTheatre(theatre);
    }

	/** Called, when the theatre object is resized. Rearranges the
     * theatre after resizing.
     * @see java.awt.event.ComponentListener#componentResized(java.awt.event.ComponentEvent)
     */
    public void componentResized(ComponentEvent e) {
        positionConstantBox();
        Dimension d = theatre.getSize();
        positionObjects(
                lrCorner,
                lrCorner = new Point(d.width, d.height));
        theatre.repaint();
    }

    /* (non-Javadoc)
	 * @see java.awt.event.ComponentListener#componentMoved(java.awt.event.ComponentEvent)
	 */
    public void componentMoved(ComponentEvent e) { }
    /* (non-Javadoc)
	 * @see java.awt.event.ComponentListener#componentShown(java.awt.event.ComponentEvent)
	 */
	public void componentShown(ComponentEvent e) { }
    /* (non-Javadoc)
	 * @see java.awt.event.ComponentListener#componentHidden(java.awt.event.ComponentEvent)
	 */
	public void componentHidden(ComponentEvent e) { }

}

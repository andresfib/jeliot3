package jeliot.theater;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Vector;
import java.util.Hashtable;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollBar;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.awt.Graphics2D;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentListener;
import javax.swing.JDialog;
import jeliot.util.ResourceBundles;
import java.util.ResourceBundle;
import javax.swing.JScrollPane;

/**
 * This is the <code>Theatre</code> component that is added in the left pane
 * of the user interface and on which the program animation produced in the
 * theater package is currently drawn.
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

    /**
     * 
     */
    private Rectangle clipRect;

    /**
     * True, if the theatre is in active mode or captured. Active mode means
     * that something is or is going to be animated. This means that the extra
     * efficiency is needed and needless painting of all the actors is not done.
     * 
     * @see Animation
     */
    private boolean active;

    /**
     * Vector of passive actors which are drawn in passive mode.
     */
    private Vector pasAct = new Vector();

    /**
     * Vector of active, moving actors which are drawn in active mode (during
     * animation).
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

    /**
     * Variable is set true if there are other <code>JComponents</code> on the
     * Theatre component. At the moment this happens only when input is
     * requested. The state of the variable changes the operation of the
     * <code>paint</code> method.
     * 
     * @see #paint(Graphics g)
     */
    private boolean showComponents;

    /**
     *  
     */
    private boolean runUntil = false;
    
    /*
     * Hashtable destined to save relations ActorName - Description
     * and provide an easier acces to information.
     */
    private Hashtable infoActor = new Hashtable();
    
    /*
     * Attribute destined to make easier the creation of quick dialogs
     * with some info about the actors.
     */
    //private JOptionPane optionPane;
    
    /**
     * Constant destined to control the zoomed scale of the components
     */
    private static final int ZOOMED_SCALE = 2;
    
    /**
     * Control variable for control the size(zoom) on the screen
     */
    private boolean zoomOut = false;
    
    /**
     * Popup menu for the handling of the variables.
     */
    private JPopupMenu variableMenu;
    
    /**
     * Popup menu for the handling of the methods.
     */
    private JPopupMenu methodMenu;
    
    /**
     * Popup menu for the handling of the constant.
     */
    private JPopupMenu constantMenu;
    
    /**
     * Popup menu for the handling of the bubbles.
     */
    private JPopupMenu bubbleMenu;
    
    /**
     * Popup menu for the handling of the objects.
     */
    private JPopupMenu objectMenu;
    
    /**
     * Popup menu for the handling of the classes.
     */
    private JPopupMenu classMenu;
    
    /**
     * Popup menu for the handling of the references.
     */
    private JPopupMenu referenceMenu;
    
    /**
     * ScrollPane for control the view in the theater
     */
    private JScrollPane scrollPane;
    
    /**
     * Variable that indicates if the actor is currently resized or not.
     * true -> yes,
     * false -> no.
     */
    public boolean contentResized = false;
    
    /*
     * ResourceBundle for the messages related with the theater and
     * the actors.
     */
    private ResourceBundle theaterDescription = ResourceBundles.getTheaterDescriptionResourceBundle();

    /**
     * Sets the opaque of the component to be true.
     * 
     * @see #setOpaque(boolean)
     */
    public Theater() {
        initAditionalComponents();
        addListeners();
        getDescriptions();
        setOpaque(true);
    }

    /**
     * Returns the TheatreManager
     * 
     * @return TheatreManager object
     */
    public TheaterManager getManager() {
        return manager;
    }

    /**
     * Sets the background image (<code>backImage</code>) of this theatre.
     * 
     * @param backImage
     *            Image for background.
     */
    public void setBackground(Image backImage) {
        this.backImage = backImage;
    }

    /**
     * Paints the theatre. If theatre is in active mode then a captured picture
     * and only active actors are painted otherwise background, the passive,
     * highlighted and active actors are painted.
     * 
     * @param g
     *            Everything is painted on the given Graphics object.
     */
    public void paintComponent(Graphics g) {
        if (!runUntil) {
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
                //here was the call to the method paintHighlight(g)
            }
            //Finally the active actors are painted.
            paintActors(g, actAct);
            paintHighlight(g);
        } else {
            paintBackground(g);
        }
    }

    /**
     * Painting the component and other components (if it contains any) to the
     * given Graphics object.
     * 
     * @see java.awt.Component#paint(java.awt.Graphics)
     */
    public void paint(Graphics g) {
        clipRect = g.getClipBounds();
        // If the component contains other components
        // call the super classes paint to first paint
        // this component and then other components on top of it.
        if (showComponents) {
            super.paint(g);
            // Otherwise just paint the current component.
        } else {
            paintComponent(g);
        }
        //g.dispose();
    }

    /**
     * Paints the image of captured screen.
     * 
     * @param g
     */
    private void paintCapturedScreen(Graphics g) {
        g.drawImage(captScreen, 0, 0, this);
    }

    /**
     * Fills the background with background image.
     * 
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
     * 
     * @param g
     * @param actors
     */
    private void paintActors(Graphics g, Vector actors) {
        synchronized (actors) {
            int n = actors.size();
            for (int i = 0; i < n; ++i) {
                Actor act = (Actor) actors.elementAt(i);
                int x = act.getX();
                int y = act.getY();
                g.translate(x, y);
                act.paintShadow(g);
                act.paintActor(g);
                g.translate(-x, -y);
            }
            /*
             * Old version: Not valid code. for (int i = 0; i < n; ++i) { Actor
             * act = (Actor)actors.elementAt(i);
             * 
             * int x = act.getX(); int y = act.getY(); g.translate(x, y);
             * act.paintActor(g); g.translate(-x, -y); }
             */
        }
    }

    /**
     * Paints the highlight marker around highlighted actor.
     * 
     * @param g
     */
    private void paintHighlight(Graphics g) {
        //if (highActor != null && highActor instanceof ReferenceActor) {
            
            //capture info about the actor
            /*ReferenceActor ha = (ReferenceActor) highActor;
            Point loc = highActor.getRootLocation();
            int x = loc.x;
            int y = loc.y;
            int w = highActor.getWidth();
            int h = highActor.getHeight();*/
            
            //this highlight the "origin" of the reference actor
            /*g.setColor(Color.white);
            g.drawRect(x - 1, y - 1, w + 1, h + 1);
            g.drawRect(x - 3, y - 3, w + 5, h + 5);
            g.setColor(Color.black);
            g.drawRect(x - 2, y - 2, w + 3, h + 3);*/
            
            //this try to highlight the rest of the reference actor
            /*if(ha.getInstanceActor() != null){
                System.out.println("nada null");
                g.setColor(Color.white);
                g.drawRect(x + 3,y + 9,ha.getReferenceWidth()-3,2);
            }
            else{
                g.setColor(Color.white);
                g.drawRect(x + 3,y + 9,18,2);
            }*/
            
            /*g.setColor(Color.white);
            if(ha.getArrowPolygon1() != null)
                g.drawPolygon(ha.getArrowPolygon1());
            
            if(ha.getArrowPolygon2() != null)
                g.drawPolygon(ha.getArrowPolygon2());*/
            /**************VIKTOR***************************************************************/
        //}
        /*else*/ if (highActor != null) {
            Point loc = highActor.getRootLocation();
            int x = loc.x;
            int y = loc.y;
            int w = highActor.getWidth();
            int h = highActor.getHeight();

            g.setColor(Color.white);
            g.drawRect(x - 1, y - 1, w + 1, h + 1);
            g.drawRect(x - 3, y - 3, w + 5, h + 5);
            g.setColor(Color.black);
            g.drawRect(x - 2, y - 2, w + 3, h + 3);
        }
    }

    //DOC: Document!
    /**
     * 
     * @param b
     */
    public void setRunUntilEnabled(boolean b) {
        runUntil = b;
    }

    /**
     * @param actor
     */
    public void addPassive(Actor actor) {
        pasAct.addElement(actor);
        actor.setParent(this);
        promoteHighlightedActor();
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
        promoteHighlightedActor();
    }

    /**
     * @param actor
     */
    public void promote(Actor actor) {
        if (pasAct.contains(actor)) {
            pasAct.removeElement(actor);
            actAct.addElement(actor);
        } else {
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
        if (!pasAct.contains(actor)) {
            pasAct.addElement(actor);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see jeliot.theater.ActorContainer#removeActor(jeliot.theater.Actor)
     */
    public void removeActor(Actor actor) {
        boolean removed = false;
        if (actAct.contains(actor)) {
            removed = actAct.removeElement(actor);
        } else {
            removed = pasAct.removeElement(actor);
        }
        if (removed) {
            //For tracking
            actor.disappear();
        }
    }
    
    public boolean isContentResized() {
        return contentResized;
    }

    public void resizeContainedActors() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.Component#getWidth()
     */
    public int getWidth() {
        return getSize().width;
    }

    /*
     * (non-Javadoc)
     * 
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
        if (captScreen == null || captScreen.getWidth(this) != w
                || captScreen.getHeight(this) != h) {
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
        highActor = null;
        manager.cleanUp();
    }

    /**
     *  
     */
    public void flush() {
        repaint();
        if (!runUntil) {
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
            }
        }
    }

    /**
     * @param x
     * @param y
     * @return
     */
    public Actor getActorAt(int x, int y) {
        synchronized (pasAct) {
            int n = pasAct.size();
            for (int i = n - 1; i >= 0; --i) {
                Actor actor = (Actor) pasAct.elementAt(i);
                Actor at = actor.getActorAt(x - actor.getX(), y - actor.getY());
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

    public Image requestImage() {
        int w = getWidth();
        int h = getHeight();
        Image i = createImage(w, h);
        Graphics gr = i.getGraphics();
        if (this.showComponents) {
            synchronized (this) {
                InputComponent.showComponents = false;
                paint(gr);
                InputComponent.showComponents = true;
            }
        } else {
            paint(gr);
        }
        return i;
    }

    public Rectangle getClipRect() {
        return clipRect;
    }

    public JScrollPane getScrollPane() {
        return this.scrollPane;
    }

    public void setScrollPane(JScrollPane scrollPane) {
        this.scrollPane = scrollPane;
    }

    public Vector getActAct() {
        return actAct;
    }

    public Vector getPasAct() {
        return pasAct;
    }
    
    private void initAditionalComponents(){
        
        // Menu for the VariableActors
        variableMenu = new JPopupMenu(); {
            JPopupMenu menu = variableMenu;
            JMenuItem menuItem;

            menuItem = new JMenuItem("Show declaration");
            menu.add(menuItem);
            
            menuItem = new JMenuItem("Actor Info");
            menuItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent aevt) {
                    showActorContext();
                }
            });
            menu.add(menuItem);
            
            menuItem = new JMenuItem("Zoom in");
            menuItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent aevt) {
                    zoomIn(aevt);
                }
            }); 
            menu.add(menuItem);

            menuItem = new JMenuItem("Zoom out");
            menuItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent aevt) {
                    zoomOut(aevt);
                }
            });
            menu.add(menuItem);
        }

        // Menu for the ConstantBox
        constantMenu = new JPopupMenu(); {
            JPopupMenu menu = constantMenu;
            JMenuItem menuItem;
            
            menuItem = new JMenuItem("Show declaration");
            menu.add(menuItem);
            
            menuItem = new JMenuItem("Actor Info");
            menuItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent aevt) {
                    showActorContext();
                }
            });
            menu.add(menuItem);
            
            menuItem = new JMenuItem("Zoom in");
            menuItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent aevt) {
                    zoomIn(aevt);
                }
            }); 
            menu.add(menuItem);

            menuItem = new JMenuItem("Zoom out");
            menuItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent aevt) {
                    zoomOut(aevt);
                }
            });
            menu.add(menuItem);
        }
        
        // Menu for the MethodStage actors
        methodMenu = new JPopupMenu(); {
            JPopupMenu menu = methodMenu;
            JMenuItem menuItem;
            
            menuItem = new JMenuItem("Show declaration");
            menu.add(menuItem);
            
            menuItem = new JMenuItem("Actor Info");
            menuItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent aevt) {
                    showActorContext();
                }
            });
            menu.add(menuItem);
            
            menuItem = new JMenuItem("Resize Actor");
            menuItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent aevt) {
                    resizeMethodStage(aevt);
                }
            }); 
            menu.add(menuItem);
            
            menuItem = new JMenuItem("Zoom in");
            menuItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent aevt) {
                    zoomIn(aevt);
                }
            }); 
            menu.add(menuItem);

            menuItem = new JMenuItem("Zoom out");
            menuItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent aevt) {
                    zoomOut(aevt);
                }
            });
            menu.add(menuItem);
        }

        // Menu for the ReferenceActor actors
        referenceMenu = new JPopupMenu(); {
            JPopupMenu menu = referenceMenu;
            JMenuItem menuItem;

            menuItem = new JMenuItem("Show declaration");
            menu.add(menuItem);
            
            menuItem = new JMenuItem("Actor Info");
            menuItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent aevt) {
                    showActorContext();
                }
            });
            menu.add(menuItem);
            
            menuItem = new JMenuItem("Show Origin");
            menu.add(menuItem);
            menuItem = new JMenuItem("Show Destiny");
            menu.add(menuItem);
        }
    }
    
    /*
     * This method is destined to resize only the MethodStage
     * actors and their subcomponents.
     */
    private void resizeMethodStage(ActionEvent aevt){
        Actor act = highActor;
        
        if(act != null && act instanceof MethodStage){
            act.resize();
            MethodStage msact = (MethodStage) act;
            msact.repositionVariableActors();
            manager.validateTheater();
            flush();
        }
    }
    
    private void showActorContext(){
        if(highActor == null){
            System.out.println("ERROR: No actor selected!");
        }
        else{
            String description = (String) infoActor.get(highActor.getClass().getCanonicalName());
            
            if(description != null){
               ActorInfo infoPane = new ActorInfo(description);
            }
            else{
               ActorInfo infoPane = new ActorInfo("The information of this actor is not available.");
            }
        }
    }
    
    private void getDescriptions() {
        // here we got the descriptions and fill the Hashtable (infoActor) with them
        infoActor.put("jeliot.theater.MethodStage",theaterDescription.getString("description.method_stage"));
        infoActor.put("jeliot.theater.ConstantBox",theaterDescription.getString("description.constant_box"));
        infoActor.put("jeliot.theater.VariableActor",theaterDescription.getString("description.variable_actor"));
        infoActor.put("jeliot.theater.ReferenceVariableActor",theaterDescription.getString("description.reference_variable_actor"));
        infoActor.put("jeliot.theater.ClassActor",theaterDescription.getString("description.class_actor"));
        infoActor.put("jeliot.theater.ReferenceActor",theaterDescription.getString("description.reference_actor"));
        infoActor.put("jeliot.theater.CIActor",theaterDescription.getString("description.ci_actor"));
        infoActor.put("jeliot.theater.ObjectStage",theaterDescription.getString("description.object_stage_actor"));
        infoActor.put("jeliot.theater.BubbleActor",theaterDescription.getString("description.bubble_actor"));
        infoActor.put("jeliot.theater.MessageActor",theaterDescription.getString("description.message_actor"));
        infoActor.put("jeliot.theater.SMIActor",theaterDescription.getString("description.smi_actor"));
        infoActor.put("jeliot.theater.OMIActor",theaterDescription.getString("description.omi_actor"));
        //faltan los array actors
    }
    
    private void zoomIn(ActionEvent aevt){
        if(zoomOut == true){
            setSize(getWidth()*ZOOMED_SCALE,getHeight()*ZOOMED_SCALE);
            sizeUpActors();
            zoomOut = false;
            manager.validateTheater();
            flush();
        }
    }
    
    private void zoomOut(ActionEvent aevt){
        if(zoomOut == false){
            setSize(getWidth()/ZOOMED_SCALE,getHeight()/ZOOMED_SCALE);
            sizeDownActors();
            zoomOut = true;
            manager.validateTheater();
            flush();
        }
    }
    
    private void sizeUpActors() {
        int i=0;
        Actor actor = null;
        Vector avector = new Vector();
        
        avector.addAll(actAct);
        avector.addAll(pasAct);
        
        for(i=0;i<avector.size();i++){
            actor = (Actor)avector.get(i);
//TODO            actor.setDoubleSize();
            if(actor instanceof MethodStage){
                MethodStage mst = (MethodStage) actor;
//TODO                mst.sizeUpVariableActors();
            }
            else if(actor instanceof ClassActor){
                ClassActor cla = (ClassActor) actor;
                cla.sizeUpVariableActors();
            }
        }
    }

    private void sizeDownActors() {
        int i=0;
        Actor actor = null;
        Vector avector = new Vector();
        
        avector.addAll(actAct);
        avector.addAll(pasAct);
        
        for(i=0;i<avector.size();i++){
            actor = (Actor)avector.get(i);
//TODO            actor.setNormalSize();
            if(actor instanceof MethodStage){
                MethodStage mst = (MethodStage) actor;
//TODO                mst.sizeDownVariableActors();
            }
            else if(actor instanceof ClassActor){
                ClassActor cla = (ClassActor) actor;
                cla.sizeDownVariableActors();
            }
        }
    }
    
    /**
     * Method for adding interactivity with the actors inside the Theater
     */
    private void addListeners(){
        
        this.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                handleMouseEvent(evt);
            }
        });
    }
    
    /**
     * Method that handles the events when the actor should be
     * highlighted.
     *
     * @param   evt The mouse event that should be handled.
     */
    private void handleMouseEvent(MouseEvent evt) {
        int x = evt.getX();
        int y = evt.getY();
        Actor actor = getActorAt(x, y);
        
        if(actor != null){
            // show the name of the clicked actor
            /*System.out.println("***+***********");
            System.out.println("NAME: " + actor.getDescription());
            System.out.println("CLASS NAME: " + actor.getClass().getCanonicalName());
            System.out.println("***+***********");*/

            //code for distinguish between clicks on BUTTON1 and BUTTON3
            switch(evt.getButton()){
                case MouseEvent.BUTTON3 : {
                      showPopup(evt);
                      break;
                }

                default : break;
            }
            setHighlightedActor(actor);
            promoteHighlightedActor();
        }
    }
    
    /*
     * This method relocates the highlighted actor, if this one is a MethodStage
     * instance, inside the passive actors Vector, relocating it into the latest
     * position of the vector.
     */
    private void promoteHighlightedActor(){
        if(highActor != null){
            //ActorContainer parent = highActor.getParent();
            ActorContainer parent = findMethodStageParent();
        
            if(parent != null){
                if(parent instanceof MethodStage && pasAct.contains(parent)){
                    pasAct.removeElement(parent);
                    pasAct.add(parent);
                }
            }
        }
    }
    
    /*
     * This method checks if the actor container of the highlighted actor is,
     * apart from the Theatre itself, one MethodStage that could be promoted
     * to the front of the stack of method stages, and in case of finding it,
     * return it.
     */
    private ActorContainer findMethodStageParent(){
        Actor act = highActor;
        Actor actaux = null;
        ActorContainer parent = act.getParent();
        
        if(act instanceof MethodStage)
            parent = (ActorContainer) act;
        else{
            if(parent instanceof VariableActor || parent instanceof ReferenceVariableActor || parent instanceof ReferenceActor){
                parent = act.getParent();
                actaux = (Actor) parent;
                parent = actaux.getParent();
            }
        }
        
        return parent;
    }
    
    /**
     * Method checks what kind of popup menu it should activate or
     * should it activate any kind of popup menu.
     *
     * @param   evt The mouse event when mouse button3 is pressed.
     */
    private void showPopup(MouseEvent evt) {
        
        //zoomingMenu.show(evt.getComponent(), evt.getX(), evt.getY());
        
        int x = evt.getX();
        int y = evt.getY();
        Actor actor = getActorAt(x, y);
        
        if (actor != null) {
            JPopupMenu menu = null;

            if (actor instanceof VariableActor) {
                menu = variableMenu;
            } else if (actor instanceof MethodStage) {
                menu = methodMenu;
            } else if (actor instanceof ReferenceVariableActor){
                menu = referenceMenu;
            } else if (actor instanceof ConstantBox){
                menu = constantMenu;
            } else if (actor instanceof BubbleActor){
                menu = bubbleMenu;
            } else if (actor instanceof ReferenceActor){
                menu = referenceMenu;
            } else if (actor instanceof ObjectStage){
                menu = objectMenu;
            } else if (actor instanceof ClassActor){
                menu = classMenu;
            }
            
            if (menu != null) {
                menu.show(evt.getComponent(), evt.getX(), evt.getY());
            }
        }
    }
}
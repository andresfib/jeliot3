package jeliot.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import jeliot.theatre.*;

/**
 * <code>Theatre</code>'s popup menu class that gives information
 * about the actors fo the theatre.
 * NOT CURRENLY USED IN JELIOT!
 *
 * @author Pekka Uronen
 * @author Niko Myller
 */
public class TheatrePopup extends MouseAdapter implements MouseMotionListener {

    /**
     * Popup menu for the handling of the variables.
     */
    JPopupMenu variableMenu = new JPopupMenu(); {
        JPopupMenu menu = variableMenu;
        JMenuItem menuItem;
        menuItem = new JMenuItem("Variable");
        menu.add(menuItem);
        menuItem = new JMenuItem("Show declaration");
        menu.add(menuItem);
    }

    /**
     * Popup menu for the handling of the methods.
     */
    JPopupMenu methodMenu = new JPopupMenu(); {
        JPopupMenu menu = methodMenu;
        JMenuItem menuItem;
        menuItem = new JMenuItem("Method");
        menu.add(menuItem);
        menuItem = new JMenuItem("Show declaration");
        menu.add(menuItem);
    }

    /**
     * Method to handle the mouse events when the actors should be
     * highlighted.
     *
     * @param   evt The mouse event when mouse entered the area.
     */
    public void mouseEntered(MouseEvent evt) {
        handleMouseEvent(evt);
    }

    /**
     * Method to handle the mouse events when the actors should be
     * unhighlighted.
     *
     * @param   evt The mouse event when mouse exited the area.
     */
    public void mouseExited(MouseEvent evt) {
        handleMouseEvent(evt);
    }

    /**
     * Method to handle the mouse events when the actors should be
     * highlighted or unhighlighted.
     *
     * @param   evt The mouse event when mouse is moved.
     */
    public void mouseMoved(MouseEvent evt) {
        handleMouseEvent(evt);
    }

    /**
     * Shows the popup menu when the button is pushed on some actor.
     *
     * @param   evt The mouse event when mouse is pressed.
     */
    public void mousePressed(MouseEvent evt) {
       // System.err.println("mousePressed");
       // if (evt.isPopupTrigger()) {
            showPopup(evt);
       // }
    }

    /** Implemented to conform to MouseMotionListener interface. */
    public void mouseDragged(MouseEvent evt) { }

    /**
     * Method that handles the events when the actor should be
     * highlighted.
     *
     * @param   evt The mouse event that should be handled.
     */
    private void handleMouseEvent(MouseEvent evt) {
        int x = evt.getX();
        int y = evt.getY();
        Theatre theatre = (Theatre)evt.getComponent();

        Actor actor = theatre.getActorAt(x, y);
        //theatre.setHighlightedActor(actor);
    }

    /**
     * Method checks what kind of popup menu it should activate or
     * should it activate any kind of popup menu.
     *
     * @param   evt The mouse event when mouse button is pressed.
     */
    private void showPopup(MouseEvent evt) {
        int x = evt.getX();
        int y = evt.getY();
        Theatre theatre = (Theatre)evt.getComponent();

        Actor actor = theatre.getActorAt(x, y);
        //System.err.println("showPop:" +actor);
        if (actor != null) {
            JPopupMenu menu = null;

            if (actor instanceof VariableActor) {
                menu = variableMenu;
            } else if (actor instanceof MethodStage) {
                menu = methodMenu;
            }

            if (menu != null) {
                //System.err.println("showMenu:"+menu);
                //menu.setSize(menu.getPreferredSize());
                menu.show(evt.getComponent(), evt.getX(), evt.getY());
            }
        }
    }

}

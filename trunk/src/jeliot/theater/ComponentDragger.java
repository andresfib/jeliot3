package jeliot.theater;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/**
  * ComponentDragger is for Theatre's components to be dragged.
  * NOT CURRENTLY USED IN JELIOT!
  *
  * @author Pekka Uronen
  */
public class ComponentDragger implements MouseListener,
                                         MouseMotionListener {
    final static int FREE   = 0;
    final static int DRAG   = 1;
    final static int RESIZE = 2;
    final static int FIXED  = 3;

    int mode = FREE;
    Point dragPoint;
    Dimension origSize;
    Component comp;

    public ComponentDragger(Component comp) {
        this.comp = comp;
        comp.addMouseListener(this);
        comp.addMouseMotionListener(this);
        comp.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }


    boolean inStretchArea(Point p) {
        Dimension d = comp.getSize();
        return (p.x > d.width - 8) && (p.y > d.height-8);
    }

    public void mousePressed(MouseEvent e) {
        dragPoint = new Point(e.getX(), e.getY());
        origSize = comp.getSize();
        mode = inStretchArea(dragPoint) ? RESIZE : DRAG;

    }

    public void mouseDragged(MouseEvent e) {
        switch (mode) {
            case (DRAG):
                Point loc = comp.getLocation();
                loc.translate(e.getX(), e.getY());
                loc.translate(-dragPoint.x, -dragPoint.y);
                comp.setLocation(loc);
                break;
            case (RESIZE):
                int w = e.getX() - dragPoint.x + origSize.width;
                int h = e.getY() - dragPoint.y + origSize.height;
                comp.setSize(w, h);
                break;
        }
        comp.getParent().repaint();

    }

    public void mouseReleased(MouseEvent e) {
        mode = FREE;
        comp.invalidate();
        comp.validate();
    }

    public boolean isFree() {
        return mode == FREE;
    }

    public void mouseClicked(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseMoved(MouseEvent e) {}
}

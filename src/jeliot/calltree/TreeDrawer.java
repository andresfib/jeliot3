package jeliot.calltree;

import java.awt.*;
import java.awt.geom.*;

/**
 * @author Niko Myller
 */
public class TreeDrawer extends EulerTour {

    /**
     * Y offset from (0,0)
     */
    protected int Yoffset = 40;

    /**
     * X offset from (0,0)
     */
    protected int Xoffset = 20;

    /**
     * where to draw the tree
     */
    protected Graphics g;

    /**
     * fill color
     */
    protected Color background;

    /**
     * a running total to shift bounding boxes.  The shift
     * distance is the sum of the shifts stored at ancestors.
     * 
     */
    protected int totalShift;

    /**
     * 
     * @param gg
     */
    public TreeDrawer(Graphics gg) {
        g = gg;
        background = g.getColor();
    }

    /**
     * 
     * @return
     */
    public int getYoffset() {
        return Yoffset;
    }

    /**
     * 
     * @return
     */
    public int getXoffset() {
        return Xoffset;
    }

    /**
     * When visiting a node for the first time we shift x by totalShift
     * @param pos
     */
    protected void visitFirstTime(TreeNode pos) {
        if (pos.getProperty("x") != null) {
            int x = ((Integer) pos.getProperty("x")).intValue();
            int shift = ((Integer) pos.getProperty("shift")).intValue();
            pos.setProperty("x", new Integer(x + totalShift));
            totalShift += shift;
        }
    }

    /**
     * When visiting a node for the last time we draw the node.
     * @param pos
     */
    protected void visitLastTime(TreeNode pos) {
        int shift = ((Integer) pos.getProperty("shift")).intValue();
        if (!pos.isRoot()) {
            //Draw the edge to the parent
            g.setColor(Color.black);
            g.drawLine(xPos(pos), yPos(pos), xPos(pos.getParent()), yPos(pos.getParent()));
        }

        Color strColor;

        if (pos.getProperty("return") != null) {
            strColor = Color.black;
        } else if (pos.getProperty("current") != null) {
            strColor = Color.red;
        } else {
            strColor = Color.yellow;
        }

        drawString(pos, strColor);
        totalShift -= shift;
        cleanup(pos);
    }

    /**
     * External nodes are drawn in the same manner as internal nodes
     * @param pos
     */
    protected void visitExternal(TreeNode pos) {
        visitFirstTime(pos);
        visitLastTime(pos);
    }

    /**
     * Draw the string at its proper location.
     * @param pos
     * @param strColor
     */
    private void drawString(TreeNode pos, Color strColor) {

        String str = "";
        String str2 = "";

        if (pos.getProperty("element") != null) {
            str = pos.getProperty("element").toString() + "\n ";
        }

        if (pos.getProperty("return") != null) {
            Object o = pos.getProperty("return");
            if (o != null && o != Util.nullObject) {
                str2 = "returned " + ((String) o).toString();
            } else {
                str2 = "returned no value";
            }
        }

        if (pos.getProperty("element") != null) {
            int ascent = ((Integer) pos.getProperty("ascent")).intValue();
            int descent = ((Integer) pos.getProperty("descent")).intValue();
            int leading = ((Integer) pos.getProperty("leading")).intValue();
            Rectangle2D bounds = ((Rectangle2D) pos.getProperty("bounds"));
            int height = (int) bounds.getHeight();
            int width = (int) bounds.getWidth();
            int x = xPos(pos) - width / 2;
            int y = yPos(pos) - ascent / 2;
            g.setColor(background);
            g.fillRect(x - 4, y, width + 6, leading + ascent + descent + leading + ascent
                            + descent);

            g.setColor(strColor);
            g.drawRect(x - 4, y, width + 6, leading + ascent + descent + leading + ascent
                            + descent);

            g.setColor(strColor);
            y += leading + ascent;
            g.drawString(str, x, y);
            y += descent + leading + ascent;
            g.drawString(str2, x, y);
        }
    }

    /**
     * 
     * @param p
     * @return
     */
    private int xPos(TreeNode p) {
        int x = ((Integer) p.getProperty("x")).intValue();
        int width = ((Integer) p.getProperty("width")).intValue();
        return x + width / 2 + Xoffset;
    }

    /**
     * 
     * @param p
     * @return
     */
    private int yPos(TreeNode p) {
        return ((Integer) p.getProperty("y")).intValue() + Yoffset;
    }

    /**
     * 
     * @param p
     */
    private void cleanup(TreeNode p) {
        p.destroyProperty("x");
        p.destroyProperty("y");
        p.destroyProperty("shift");
        p.destroyProperty("ascent");
        p.destroyProperty("descent");
        p.destroyProperty("bounds");
    }
}
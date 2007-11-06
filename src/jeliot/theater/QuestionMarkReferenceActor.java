/*
 * Created on Nov 1, 2007
 */
package jeliot.theater;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;

/**
 * @author nmyller
 */
public class QuestionMarkReferenceActor extends ReferenceActor {

    private Image img;

    /**
     * 
     */
    public QuestionMarkReferenceActor() {
        super();
    }

    public QuestionMarkReferenceActor(Image img) {
        super();
        this.img = img;
    }

    /* (non-Javadoc)
     * @see jeliot.theater.Actor#paintActor(java.awt.Graphics)
     */
    public void paintActor(Graphics g) {

        Color bc = bgcolor;
        Color fc = fgcolor;

        int h = height;
        int w = this.width;
        //Point p = getRootLocation();

        // draw reference area
        g.setColor(lightColor);
        g.fillRect(0, 0, refWidth, h);
        //g.setColor(bc);
        //g.fillRect(p.x+1, p.y+1, refWidth-2, h-2);
        g.setColor(fc);
        g.drawRect(0, 0, refWidth, h);

        //g.setColor(lightColor);
        //g.drawString("?", 1, 14);
        g.setColor(fgcolor);
        FontMetrics fm = getFontMetrics();
        if (img != null) {
            g.drawImage(img, 1 + (w - img.getWidth(dummy)) / 2, (h - img
                    .getHeight(dummy)) / 2, dummy);
        } else {
            g.drawString("?", 2, h - 5);
        }
    }

    public void calculateBends() {
    }

    public void calculateArrowhead(int dir) {
    }

    /**
     * 
     */
    public String toString() {
        return "Unknown value ";
    }

    public int getReferenceWidth() {
        //Null value is shown.
        return 0;
    }

    /* (non-Javadoc)
     * @see jeliot.theater.Actor#calculateSize()
     */
    public void calculateSize() {
        setSize(getPreferredSize());
    }

    public Dimension getPreferredSize() {
        FontMetrics fm = getFontMetrics();
        int h = fm.getHeight();
        int w = refWidth;
        return new Dimension(w, h);
    }
}

package jeliot.theatre;

import java.awt.*;
import jeliot.lang.*;

/**
  * @author Pekka Uronen
  *
  * created         18.9.1999
  * modified        12.12.2002 by Niko Myller
  */
public class ValueActor extends Actor {

    String valstr;

    private int namex;
    private int namey;
    private int swidth;

    private int margin = 2;

    public ValueActor() {
        setFont(new Font("SansSerif", Font.BOLD, 16));
    }

    public void setLabel(String valstr) {
        this.valstr = valstr;
    }

    public String getLabel() {
        return this.valstr;
    }

    public void paintActor(Graphics g) {
        int w = width;
        int h = height;
        int bw = borderWidth;

        // fill background
        Color bcol = null;
        Color lcol = null;
        switch (light) {
            case (HIGHLIGHT):
                bcol = lightColor;
                lcol = bcol;
                break;
            case (NORMAL):
                bcol = bgcolor;
                lcol = lightColor;
                break;
            case (SHADED):
                bcol = darkColor;
                lcol = bgcolor;
                break;
        }

        g.setColor(bcol);
        g.fillRect(bw, bw, w - 2 * bw, h - 2 * bw);

        // draw border
        g.setColor(fgcolor);
        g.drawRect(0, 0, w-1, h-1);
        g.setColor(darkColor);
        g.drawLine(1, h-2, w-2, h-2);
        g.drawLine(w-2, 2, w-2, h-2);
        g.setColor(lcol);
        g.drawLine(1, 1, w-2, 1);
        g.drawLine(1, 1, 1, h-2);

        paintValue(g);
    }

    public void paintValue(Graphics g) {
        g.setFont(getFont());
        g.setColor(fgcolor);
        g.drawString(valstr, namex, namey-1);
    }

    public void setBounds(int x, int y, int w, int h) {
        int oldw = getWidth();
        int oldh = getHeight();
        super.setBounds(x, y, w, h);

        if (w != oldw || h != oldh) {
            calcLabelPosition();
        }
    }

    protected void calcLabelPosition() {
        FontMetrics fm = getFontMetrics();
        int sw = fm.stringWidth(valstr);
        int sh = fm.getHeight();

        namex = (getWidth()-sw)/2;
        namey = (getHeight()+sh)/2-2;

    }

    public void calculateSize() {
        setSize(getPreferredSize());
        calcLabelPosition();
    }

    public Dimension getPreferredSize() {
        FontMetrics fm = getFontMetrics();
        int sw = fm.stringWidth(valstr);
        int sh = fm.getHeight();
        int h = sh+margin;
        int w = Math.max(h, sw+margin+6);
        return new Dimension(w, h);
    }
}
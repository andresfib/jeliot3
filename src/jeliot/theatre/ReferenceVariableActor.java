package jeliot.theatre;

import java.awt.*;
import javax.swing.*;
import jeliot.lang.*;

/**
  * @author Pekka Uronen
  *
  * created         8.8.1999
  * revised         18.9.1999
  */
public class ReferenceVariableActor extends VariableActor {

    private int refWidth = 6;
    private ReferenceActor refActor;
    private int refLen = 15;


    public void paintActor(Graphics g) {
        int w = width;
        int h = height;
        int bw = borderWidth;

        // fill background
        g.setColor( (light == HIGHLIGHT) ?
                darkColor :
                bgcolor );
        g.fillRect(bw, bw, w - 2 * bw, h - 2 * bw);

        // draw the name
        g.setFont(font);
        g.setColor( (light == HIGHLIGHT) ?
                lightColor :
                fgcolor );
        g.drawString(name, namex, namey);

        // draw reference area
        g.setColor(darkColor);
        g.fillRect(w - bw - refWidth, bw, refWidth, h - 2 *bw);


        // draw border
        ActorContainer parent = getParent();
        g.setColor( (parent instanceof Actor)   ?
                ( (Actor)parent ).darkColor     :
                fgcolor );
        g.drawLine(0, 0, w-1, 0);
        g.drawLine(0, 0, 0, h-1);
        g.setColor( (parent instanceof Actor)   ?
                ( (Actor)parent ).lightColor     :
                fgcolor );
        g.drawLine(1, h-1, w-1, h-1);
        g.drawLine(w-1, 1, w-1, h-1);

        g.setColor(fgcolor);
        g.drawRect(1, 1, w-3, h-3);
        g.setColor(darkColor);
        g.drawLine(2, h-3, w-3, h-3);
        g.drawLine(w-3, 2, w-3, h-3);
        g.setColor(lightColor);
        g.drawLine(2, 2, w-3, 2);
        g.drawLine(2, 2, 2, h-3);

        // draw link
        if (refActor == null) {
            g.setColor(fgcolor);
            int a = w-2+refLen;
            g.drawLine(w-2, h/2-1, a, h/2-1);
            g.drawLine(w-2, h/2+1, a, h/2+1);
            g.drawLine(a, h/2 - 6, a, h/2 + 6);
            g.drawLine(a+2, h/2 - 6, a+2, h/2 + 6);

            g.setColor(bgcolor);
            g.drawLine(w-2, h/2, a, h/2);
            g.drawLine(a+1, h/2 - 6, a+1, h/2 + 6);
        }
        else {
            Point p = getRootLocation();
            g.translate(-p.x, -p.y);
            refActor.paintActor(g);
            g.translate(p.x, p.y);
        }

    }

    public void setBounds(int x, int y, int w, int h) {
        int oldw = getWidth();
        int oldh = getHeight();
        super.setBounds(x, y, w, h);

        if (w != oldw || h != oldh) {
            calcLabelPosition();
        }
    }

    public void calculateSize() {
        FontMetrics fm = getFontMetrics();
        int sw = fm.stringWidth(name);
        int sh = fm.getHeight();

        setSize(2*borderWidth + insets.right + insets.left +
                refWidth + sw,
                insets.top + insets.bottom + 4*borderWidth +
                Math.max(valueh, sh));
    }

    public void setReference(ReferenceActor refActor) {
        this.refActor = refActor;
    }

    public void theatreResized() {
        if (refActor != null) {
            refActor.calculateBends();
        }
    }

}

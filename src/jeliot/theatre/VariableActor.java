package jeliot.theatre;

import java.awt.*;
import javax.swing.*;
import jeliot.lang.*;

/**
  * @author Pekka Uronen
  *
  * created         8.8.1999
  * revised         18.9.1999
  * modified        12.12.2002 by Niko Myller
  */
public class VariableActor extends Actor implements ActorContainer {

    /** Location of the variable's name. */
    protected int namex;
    protected int namey;

    /** Location and size of the value slot. */
    protected int valuex, valuey;
    protected int valuew, valueh;

    /** Value box's width. */
    private int vborderw = 2;

    /** Variable's name. */
    protected String name;

    /** Value actor assigned to this variable actor. */
    protected ValueActor value;

    /** Background color of the values of this type. */
    protected Color valueColor;

    protected ValueActor reserved;

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

        // draw value box
        g.drawRect(valuex-1, valuey-1, valuew+1, valueh+1);
        g.setColor(darkColor);
        g.drawRect(valuex-2, valuey-2, valuew+3, valueh+3);
        g.setColor(valueColor);
        g.fillRect(valuex, valuey, valuew, valueh);

        // draw value
        if (value != null) {
            int actx = value.getX();
            int acty = value.getY();
            g.translate(actx, acty);
            value.paintValue(g);
            g.translate(-actx, -acty);
        }

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

    }

    public void setFont(Font font) {
        super.setFont(font);
        if (name != null) {
            calcLabelPosition();
        }
    }

    public Point reserve(ValueActor actor) {
        this.reserved = actor;
        Point rp = getRootLocation();
        int w = actor.width;
        int h = actor.height;
        rp.translate(valuex + (valuew-w)/2, valuey + (valueh-h)/2);
        return rp;
    }

    public void removeActor(Actor actor) {  }

    public void bind() {
        this.value = this.reserved;
        value.setParent(this);

        value.setLocation(
                valuex + (valuew - value.width)/ 2,
                valuey + (valueh - value.height)/ 2);
    }

    public void setValue(ValueActor actor) {
        this.reserved = actor;
        bind();
    }

    public ValueActor getValue() {
        ValueActor act = (ValueActor)this.value.clone();
        return act;
    }

    public void setValueColor(Color valuec) {
        this.valueColor = valuec;
    }

    public void setName(String name) {
        this.name = name;
        calcLabelPosition();
    }

    //Added for Jeliot 3 to find the variables.
    public String getName() {
        return this.name;
    }

    public void setValueDimension(int w, int h) {
        this.valuew = w;
        this.valueh = h;
        calcLabelPosition();
    }

    public void setSize(int w, int h) {
        super.setSize(w, h);
        calcLabelPosition();
    }

    protected void calcLabelPosition() {
        int w = getWidth();
        int h = getHeight();

        int aw = w - insets.right - insets.left - 4;
        int ah = h - insets.top - insets.bottom - 4;

        FontMetrics fm = getFontMetrics();
        int sw = fm.stringWidth(name);
        int sh = fm.getHeight();

        valuex = w - insets.right - 4 - valuew;
        valuey = (h-valueh)/2;

        namex = insets.left;
        namey = (h+sh)/2;
    }

    public void calculateSize() {
        FontMetrics fm = getFontMetrics();
        int sw = fm.stringWidth(name);
        int sh = fm.getHeight();
        setSize(4*borderWidth + insets.right + insets.left +
                valuew + sw,
                insets.top + insets.bottom + 4*borderWidth +
                Math.max(valueh, sh));
        calcLabelPosition();
    }
}

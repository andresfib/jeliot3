package jeliot.theater;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;

/**
 * <code>VariableInArrayActor</code> represent graphically the language construct
 * <code>VariableInArray</code> for primitive data types and Strings.
 * 
 * @author Pekka Uronen
 * @author Niko Myller
 * 
 * @see jeliot.lang.VariableInArray
 * @see jeliot.theater.VariableActor
 * @see jeliot.theater.ArrayActor
 */
public class VariableInArrayActor extends VariableActor {

//  DOC: Document!

    /**
	 *
	 */
	private int indexw;

    /**
	 * 
	 */
	public VariableInArrayActor() { }

    /**
	 * @param arrayActor
	 * @param name
	 */
	public VariableInArrayActor(ArrayActor arrayActor, String name) {
        setParent(arrayActor);
        this.name = name;
    }

    /* (non-Javadoc)
	 * @see jeliot.theater.Actor#paintActor(java.awt.Graphics)
	 */
	public void paintActor(Graphics g) {
        ArrayActor array = (ArrayActor)getParent();

        // fill background
        g.setColor( (light == HIGHLIGHT) ?
                array.darkColor :
                array.bgcolor);
        g.fillRect(0, 0, indexw, height);
        g.setColor(valueColor);
        g.fillRect(indexw + 2, 0, width - 2 - indexw, height);

        // draw value
        int x = value.getX();
        int y = value.getY();
        g.translate(x, y);
        value.paintValue(g);
        g.translate(-x, -y);

        // draw indices
        g.setFont(array.getFont());
        g.setColor( (light == HIGHLIGHT) ?
                Color.white :
                array.darkColor);
        g.drawString(name, namex, namey);
    }

    /* (non-Javadoc)
	 * @see jeliot.theater.VariableActor#calcLabelPosition()
	 */
	protected void calcLabelPosition() { }

    /**
	 * @param indexw
	 * @param valuew
	 * @param h
	 */
	public void calculateSize(int indexw, int valuew, int h) {
        setSize(indexw + 2 + valuew, valueh);

        FontMetrics fm = ((ArrayActor)getParent()).getFontMetrics();
        int namew = fm.stringWidth(name);

        this.indexw = indexw;
        this.namex = indexw - namew;
        this.namey = h - 1;

        this.valuex = indexw + 2;
        this.valuey = 0;

        this.valuew = valuew;
        this.valueh = h;

        setSize(valuew + 2 + indexw, h);

        setValue(value);
    }
	
	public int getIndexWidth() {
		return indexw;
	}
}

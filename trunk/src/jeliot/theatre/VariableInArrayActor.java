package jeliot.theatre;

import java.awt.*;

/**
  * @author Pekka Uronen
  *
  * created         13.10.1999
  */
public class VariableInArrayActor extends VariableActor {

    private int indexw;

    public VariableInArrayActor(ArrayActor arrayActor, String name) {
        setParent(arrayActor);
        this.name = name;
    }
    
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

    protected void calcLabelPosition() { }

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
}

package jeliot.theatre;

import java.awt.*;
import java.util.*;
import jeliot.lang.*;

/**
  * @author Pekka Uronen
  *
  * created         10.10.1999
  */
public class ArrayActor extends InstanceActor {
    
    private VariableInArrayActor[] variableActors;
    
    private Color valueColor;
    
    /** The x-coordinate of the vertical line separating indices from
      * values. */
    private int vlinex;
    
    /** The width of a cell reserved for a single value actor. */
    private int valuew;
    
    /** The height of a single value actor. */
    private int valueh;
    
    /** The width of an index label. */
    private int indexw;
    
    
    public ArrayActor(ValueActor[] valueActors) {
        int n = valueActors.length;
        this.variableActors = new VariableInArrayActor[n];
        for (int i = 0; i < n; ++i) {
            variableActors[i] = new VariableInArrayActor(
                    this, Integer.toString(i));
            variableActors[i].setValue(valueActors[i]);
            variableActors[i].setParent(this);
        }
    }
    
    public void setValueColor(Color valueColor) {
        int n = variableActors.length;
        for (int i = 0; i < n; ++i) {
            variableActors[i].setValueColor(valueColor);
        }
    }
    
    public VariableActor getVariableActor(int index) {
        return variableActors[index];
    }
    
    public void calculateSize(int valuew, int valueh) {
        FontMetrics fm = getFontMetrics();
        this.indexw = fm.stringWidth("00");
        this.valuew = valuew;
        this.valueh = valueh;
        
        int n = variableActors.length;
        int w = 6 + valuew + indexw;
        int h = 3 + (valueh + 1) * n;
        setSize(w, h);       
        
        int x = 2;
        int y = 2;
        for (int i = 0; i < n; ++i) {
            variableActors[i].setSize(valuew, valueh);
            variableActors[i].setLocation(x, y);
            variableActors[i].calculateSize(indexw, valuew, valueh);
            y += 1 + valueh;
        }
        
    }
    
    public void paintActor(Graphics g) {
        int w = this.width;
        int h = this.height;
        int bw = 2;
        int n = variableActors.length;
        
        // draw cells
        for (int i = 0; i < n; ++i) {
            VariableInArrayActor a = variableActors[i];
            int x = a.getX();
            int y = a.getY();
            g.translate(x, y);
            a.paintActor(g);
            g.translate(-x, -y);
        }
        
        // draw border
        g.setColor(borderColor);
        g.drawRect(0, 0, w-1, h-1);
        g.setColor(darkColor);
        g.drawRect(1, 1, w-3, h-3); 
        
        // draw vertical line
        int vlinex = 2 + indexw;
        g.drawLine(vlinex, bw, vlinex, h-2);
        g.drawLine(vlinex+1, bw, vlinex+1, h-2);
        
        // draw horizontal lines
        int x1 = bw, x2 = w - 2 * bw;
        int yc = bw - 1;
        for (int i = 1; i < n; ++i) {
            yc += 1 + valueh;
            g.drawLine(x1, yc, x2, yc);
        }
        
    }
    
    
   
}

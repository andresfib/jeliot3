package jeliot.theatre;

import java.awt.*;
import java.util.*;

/**
  * @author Pekka Uronen
  *
  * created         10.10.1999
  */
public class ReferenceActor extends Actor {

    private InstanceActor instance;
    private VariableActor variable;
   
    private Point[] bend;
   
    public ReferenceActor(InstanceActor inst, VariableActor var) {
        this.instance = inst;
        this.variable = var;
    }
    
    public void paintActor(Graphics g) {
        Color bc = variable.bgcolor;
        Color fc = variable.fgcolor;
        
        int n = bend.length;
        for (int i = 1; i < n; ++i) {
            Point p1 = bend[i-1];
            Point p2 = bend[i];
            
            g.setColor(bc);
            g.drawLine(p1.x, p1.y, p2.x, p2.y);
            g.setColor(fc);
            if (p2.x > p1.x) {
                g.drawLine(p1.x+1, p1.y-1, p2.x, p1.y-1);
                g.drawLine(p1.x, p1.y+1, p2.x, p1.y+1);
            }
            else {
                g.drawLine(p1.x-1, p1.y+1, p2.x-1, p2.y);
                g.drawLine(p1.x+1, p1.y, p2.x+1, p2.y);
            }
        }
    }
    
    public void calculateBends() {
        Point ip = instance.getRootLocation();
        Point vp = variable.getRootLocation();
        
        int iy1 = ip.y;
        int iy2 = iy1 + instance.getHeight();
        int vy1 = vp.y;
        int vy2 = vy1 + variable.getHeight();
        
        int ix = ip.x;
        int vx = vp.x + variable.getWidth();
        
        bend = new Point[4];
        bend[0] = new Point(vx -2, (vy1+vy2)/2);
        bend[1] = new Point(vx + 45 - (vy1/6), bend[0].y);
        bend[2] = new Point(bend[1].x, iy1 + 12);
        bend[3] = new Point(ix, bend[2].y);
    }    
     
}
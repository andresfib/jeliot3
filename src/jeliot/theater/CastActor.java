package jeliot.theater;

import java.awt.Graphics;
import java.awt.Shape;

/**
 * CastActor handles the animation of the casting
 * of the primitive values.
 * 
 * @author Pekka Uronen
 * @author Niko Myller
 */
public class CastActor extends Actor {

    /**
	 *
	 */
	ValueActor fromActor;
    
    /**
	 *
	 */
	ValueActor toActor;
    
    /**
	 *
	 */
	int line;

	/**
	 *
	 */
	int linex;
    
    /**
     *
     */    
    int linew;
    
    /**
	 * @param fromActor
	 * @param toActor
	 */
	public CastActor(ValueActor fromActor, ValueActor toActor) {
        this.fromActor = fromActor;
        this.toActor = toActor;
        
        linex = 0;
        linew = Math.max(fromActor.getWidth(), toActor.getWidth());
        
        setSize(Math.max(fromActor.getHeight(), toActor.getHeight()),
            linew);
    }
    
    /* (non-Javadoc)
	 * @see jeliot.theater.Actor#paintActor(java.awt.Graphics)
	 */
	public void paintActor(Graphics g) {
        if (line <= 0) {
            fromActor.paintActor(g);
        }
        else if (line > toActor.getHeight()) {
            toActor.paintActor(g);
        }
        else {
            Shape clip = g.getClip();
            
            
            // DOESN'T WORK! BUGBUG!
            g.clipRect(-1000, -1000, 1000, line);
            fromActor.paintActor(g);
            
            g.setClip(clip);
            g.clipRect(-1000, line, 1000, 1000);
            toActor.paintActor(g);
            
            g.setClip(clip);
            g.setColor(fgcolor);
            g.fillRect(linex, line-1, linew, 3); 
        }
    }
    
    /**
	 * @return
	 */
	public Animation cast() {
        return new Animation() {
            double plus;
            double h;
            public void init() {
                plus = (double)getHeight() / getDuration();
            }
            
            public void animate(double pulse) { 
                h += plus * pulse;
                line = (int)h;
                this.repaint();
            }
        };
    }
    
}


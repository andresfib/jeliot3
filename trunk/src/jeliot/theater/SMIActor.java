package jeliot.theater;

import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;

/**
 * 
 * @author Pekka Uronen
 * @author Niko Myller
 */
public class SMIActor extends Actor implements ActorContainer{

    /**
	 *
	 */
	String name;

    /**
	 *
	 */
	Actor[] actors;
    
    /**
	 *
	 */
	Point[] locs;
    
    /**
	 *
	 */
	boolean[] bound;
    
    /**
	 *
	 */
	int next = 0;

    /**
	 *
	 */
	int margin = 2;
    
    /**
	 *
	 */
	int titlemargin = 4;
    
    /**
	 *
	 */
	int namey;
    
    /**
	 *
	 */
	int namex;
    
	/**
	 *
	 */
	int namew;
    
	/**
	 *
	 */
	int nameh;
    
    /**
	 *
	 */
	int commaMargin;

    /**
	 * @param name
	 * @param n
	 */
	public SMIActor(String name, int n) {
        this.name = name;
        actors = new Actor[n];
        locs = new Point[n];
        bound = new boolean[n];
        FontMetrics fm = getFontMetrics();
        commaMargin = fm.stringWidth(",");        
    }

    /**
	 * @param actor
	 * @return
	 */
	public Point reserve(Actor actor) {
        actors[next] = actor;
        //int y = insets.top + namey + titlemargin;
        //int x = insets.left;
		int y = insets.top; 
		int x = insets.left + namew + margin;

        if (next > 0) {
            if (actors[next-1] instanceof ReferenceActor) {
                x = locs[next - 1].x +
                    margin + commaMargin + margin +
                    actors[next - 1].getWidth() +
                    ((ReferenceActor) actors[next - 1]).getReferenceWidth();
            } else {
                x = locs[next - 1].x +
                    margin + commaMargin + margin +
                    actors[next -1].getWidth();
            }
        }

        locs[next++] = new Point(x, y);
        Point rp = getRootLocation();
        rp.translate(x, y);
        return rp;
    }


    /**
	 * @param actor
	 */
	public void bind(Actor actor) {
        for (int i = 0; i < next; ++i) {
            if (actors[i] == actor) {
                bound[i] = true;
                actor.setParent(this);
                actor.setLocation(locs[i]);
                return;
            }
        }
        throw new RuntimeException();
    }

    /**
	 * @param g
	 */
	public void paintActors(Graphics g) {
        int n = next;
        for (int i = 0; i < n; ++i) {
            if (bound[i]) {
                g.translate(locs[i].x, locs[i].y);
                actors[i].paintActor(g);
                g.translate(-locs[i].x, -locs[i].y);
            }
        }
    }

    /* (non-Javadoc)
	 * @see jeliot.theater.Actor#paintActor(java.awt.Graphics)
	 */
	public void paintActor(Graphics g) {
        int w = getWidth();
        int h = getHeight();

        // draw background
        //g.setColor(bgcolor);
        //g.fillRect(2, 2, w-4, h-4);

        // draw border
        //g.setColor(darkColor);
        //g.drawRect(1, 1, w-3, h-3);
        g.setColor(fgcolor);
        //g.drawRect(0, 0, w-1, h-1);

        // draw text
        g.setFont(getFont());
        	    
		if (next > 0) {        
        	g.drawString(name + "(", namex, namey);
        	
        	for (int i = 0; i < next; i++) {
	        	if (i != (next-1)) {
		        	if (actors[i] instanceof ReferenceActor) {
			        	g.drawString(",",
	        					 locs[i].x +
	        					 actors[i].getWidth() +
	        					 ((ReferenceActor) actors[i]).getReferenceWidth() +
	        					 margin,
	        					 namey);
                    
	            	} else {
			        	g.drawString(",",
	        					 locs[i].x +
	        					 actors[i].getWidth() +
	        					 margin,
	        					 namey);
    	        	}
    			}
        	}
        	       
        	if (actors[next-1] instanceof ReferenceActor) {
	        	g.drawString(")",
	     					 locs[next-1].x +
	      					 actors[next-1].getWidth() +
	       					 ((ReferenceActor) actors[next-1]).getReferenceWidth() +
	       					 margin,
	       					 namey);
                    
	       	} else {
		       	g.drawString(")",
	       					 locs[next-1].x +
	       					 actors[next-1].getWidth() +
	       					 margin,
	       					 namey);
    	        	}
		} else {
        	g.drawString(name + "()", namex, namey);			
		}

        paintActors(g);
    }

    /* (non-Javadoc)
	 * @see jeliot.theater.Actor#calculateSize()
	 */
	public void calculateSize() {
        // Get the size of the name.
        FontMetrics fm = getFontMetrics();
        nameh = fm.getHeight();
        namew = fm.stringWidth(this.name + "(");

        int n = next;
        int maxh = insets.top + titlemargin + nameh;
        int maxw = insets.left + namew;
        for (int i = 0; i < n; ++i) {
            int h = locs[i].y + actors[i].getHeight();
            maxh = h > maxh ? h : maxh;
            int w = locs[i].x + actors[i].getWidth();
            maxw = w > maxw ? w : maxw;
        }
        namex = insets.left;
        namey = insets.top + nameh;
        setSize(maxw + insets.right, maxh + insets.bottom);
    }

    /* (non-Javadoc)
	 * @see jeliot.theater.ActorContainer#removeActor(jeliot.theater.Actor)
	 */
	public void removeActor(Actor actor) {
        int n = next;
        for (int i = 0; i < n; ++i) {
            if (actors[i] == actor) {
                bound[i] = false;
            }
        }
    }

    /* (non-Javadoc)
	 * @see jeliot.theater.Actor#setLight(int)
	 */
	public void setLight(int light) {
        super.setLight(light);
        int n = next;
        for (int i = 0; i < n; ++i) {
            actors[i].setLight(light);
        }
    }
}
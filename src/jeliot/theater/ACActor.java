package jeliot.theater;

import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;

/**
  * @author Niko Myller
  *
  * created         23.6.2003
  */
public class ACActor extends Actor implements ActorContainer{

    String name;

    Actor[] actors;
    Point[] locs;
    boolean[] bound;
    int next = 0;

    int margin = 2;
    int titlemargin = 4;
    int namey;
    int namex;
	int namew;
	int nameh;
    int bracketMargin;

    public ACActor(String name, int n) {
        this.name = name;
        actors = new Actor[n];
        locs = new Point[n];
        bound = new boolean[n];
        FontMetrics fm = getFontMetrics();
        bracketMargin = fm.stringWidth("][");        
    }

    public Point reserve(Actor actor) {
        actors[next] = actor;
        //int y = insets.top + namey + titlemargin;
        //int x = insets.left;
		int y = insets.top; 
		int x = insets.left + namew + margin;

        if (next > 0) {
            if (actors[next-1] instanceof ReferenceActor) {
                x = locs[next - 1].x +
                    margin + bracketMargin + margin +
                    actors[next - 1].getWidth() +
                    ((ReferenceActor) actors[next - 1]).getReferenceWidth();
            } else {
                x = locs[next - 1].x +
                    margin + bracketMargin + margin +
                    actors[next -1].getWidth();
            }
        }

        locs[next++] = new Point(x, y);
        Point rp = getRootLocation();
        rp.translate(x, y);
        return rp;
    }


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
        	g.drawString(name + "[", namex, namey);
        	
        	for (int i = 0; i < next; i++) {
	        	if (i != (next-1)) {
		        	if (actors[i] instanceof ReferenceActor) {
			        	g.drawString("][",
	        					 locs[i].x +
	        					 actors[i].getWidth() +
	        					 ((ReferenceActor) actors[i]).getReferenceWidth() +
	        					 margin,
	        					 namey);
                    
	            	} else {
			        	g.drawString("][",
	        					 locs[i].x +
	        					 actors[i].getWidth() +
	        					 margin,
	        					 namey);
    	        	}
    			}
        	}
        	       
        	if (actors[next-1] instanceof ReferenceActor) {
	        	g.drawString("]",
	     					 locs[next-1].x +
	      					 actors[next-1].getWidth() +
	       					 ((ReferenceActor) actors[next-1]).getReferenceWidth() +
	       					 margin,
	       					 namey);
                    
	       	} else {
		       	g.drawString("]",
	       					 locs[next-1].x +
	       					 actors[next-1].getWidth() +
	       					 margin,
	       					 namey);
    	        	}
		} else {
        	g.drawString(name + "[]", namex, namey);			
		}

        paintActors(g);
    }

    public void calculateSize() {
        // Get the size of the name.
        FontMetrics fm = getFontMetrics();
        nameh = fm.getHeight();
        namew = fm.stringWidth(this.name + "[");

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

    public void removeActor(Actor actor) {
        int n = next;
        for (int i = 0; i < n; ++i) {
            if (actors[i] == actor) {
                bound[i] = false;
            }
        }
    }

    public void setLight(int light) {
        super.setLight(light);
        int n = next;
        for (int i = 0; i < n; ++i) {
            actors[i].setLight(light);
        }
    }
}

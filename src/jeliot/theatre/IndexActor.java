package jeliot.theatre;

import java.awt.*;
import java.util.*;

/**
  * @author Pekka Uronen
  *
  * created         13.10.1999
  */
public class IndexActor extends Actor {

    private Actor source;

    private Point startPoint;
    private Point endPoint;

    public IndexActor(Actor source) {
        this.source = source;
        this.fgcolor = Color.white;
    }

    public void paintActor(Graphics g) {
        g.setColor(fgcolor);
        g.drawLine(startPoint.x, startPoint.y, endPoint.x, endPoint.y);
        g.drawLine(startPoint.x, startPoint.y+1, endPoint.x, endPoint.y+1);
        g.drawLine(startPoint.x, startPoint.y-1, endPoint.x, endPoint.y-1);
    }

    public Animation index(VariableInArrayActor varAct) {

        final Point finalPoint = varAct.getRootLocation();
        finalPoint.translate(0, varAct.getHeight()/2);

        Point sLoc = source.getRootLocation();
        Dimension sSize = source.getSize();
        startPoint = new Point(
                sLoc.x + sSize.width,
                sLoc.y + sSize.height / 2);

        int dx = finalPoint.x - startPoint.x;
        int dy = finalPoint.y - startPoint.y;
        final double len = Math.sqrt(dx*dx + dy*dy);
        double angle = Math.atan2(dy, dx);
        final double cos = Math.cos(angle);
        final double sin = Math.sin(angle);

        return new Animation() {
            double xp = startPoint.x;
            double yp = startPoint.y;
            double l = 0;
            double step;

            public void init() {
                this.addActor((Actor)IndexActor.this);
                step = len / getDuration();
                endPoint = new Point(startPoint);
            }

            public void animate(double pulse) {
                xp += pulse * step * cos;
                yp += pulse * step * sin;
                l += pulse * step;

                endPoint.x = (int)xp;
                endPoint.y = (int)yp;

                repaint();
            }

            public void finish() {
                endPoint = finalPoint;
                //this.repaint();
            }

            public void finalFinish() {
                this.passivate((Actor)IndexActor.this);
            }
        };

    }

}
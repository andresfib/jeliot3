package jeliot.theater;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;

/**
  * @author Pekka Uronen
  *
  * created         30.8.1999
  */
public class OperatorActor extends Actor {

    private Image image;
    private Image darkImage;

    public OperatorActor(Image image, Image dark) {
        this.image = image;
        this.darkImage = dark;
    }

    public void paintActor(Graphics g) {
        g.drawImage(
                ( (light == SHADED) ?
                    darkImage:
                    image),
                insets.left, insets.top, null);
    }

    public void calculateSize() {
        setSize(image.getWidth(null) + insets.left + insets.right,
                 image.getHeight(null) + insets.top + insets.bottom);
    }

    /** Returns an animation that makes the actor appear. Default
      * implementation shows the actor highlighted for given number of
      * milliseconds.
      */
    public Animation appear(final Point loc) {
        return new Animation() {
            public void init() {
                this.addActor(OperatorActor.this);
                setLocation(loc);
                setLight(NORMAL);
                this.repaint();
            }

            public void animate(double pulse) { }

            public void finish() {
                setLight(NORMAL);
            }

            public void finalFinish() {
                this.passivate(OperatorActor.this);
            }
        };
    }

}

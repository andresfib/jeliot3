package jeliot.theatre;

import java.awt.Graphics;
import java.awt.Image;

/**
  * Animating actor is a actor that may be used to show frame based
  * animation.
  *
  * @author Pekka Uronen
  *
  * created         3.10.1999
  */
public class AnimatingActor extends Actor {

    private Image image;

    public AnimatingActor(Image image) {
        this.image = image;
        setShadow(0);
	}

    public void paintActor(Graphics g) {
        g.drawImage(image, insets.left, insets.top, null);
    }

    public void calculateSize() {
        setSize(image.getWidth(null) + insets.left + insets.right,
                 image.getHeight(null) + insets.top + insets.bottom);
    }

    public void setImage(Image image) {
        this.image = image;
    }

    /** Returns an animation object that changes the image of this
      * actor to the given image. */
    public Animation changeImage(final Image chim ) {
        return new Animation() {

            public void animate(double pulse) { }

            public void finish() {
                setImage(chim);
            }
        };
    }
}
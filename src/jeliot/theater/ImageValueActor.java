package jeliot.theater;

import java.awt.Graphics;
import java.awt.Image;

/**
  * @author Pekka Uronen
  *
  * created         23.9.1999
  */
public class ImageValueActor extends ValueActor {

    /**
	 *
	 */
	private Image image;

    /**
	 * @param image
	 */
	public ImageValueActor(Image image) {
        this.image = image;
	}

    /* (non-Javadoc)
	 * @see jeliot.theater.ValueActor#paintValue(java.awt.Graphics)
	 */
	public void paintValue(Graphics g) {
        g.drawImage(image, 0, 0, null);
    }

    /* (non-Javadoc)
	 * @see jeliot.theater.ValueActor#calcLabelPosition()
	 */
	protected void calcLabelPosition() { }
    
    /* (non-Javadoc)
	 * @see jeliot.theater.Actor#calculateSize()
	 */
	public void calculateSize() {
        setSize(image.getWidth(null), image.getHeight(null));
    }

}

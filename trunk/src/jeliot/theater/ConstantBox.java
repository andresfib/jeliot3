package jeliot.theater;

import java.awt.Graphics;
import java.awt.Image;

/**
 * 
 * @author Pekka Uronen
 * @author Niko Myller
 */
public class ConstantBox extends Actor {

    /**
	 *
	 */
	private Image image;
    
    /**
	 * @param image
	 */
	public ConstantBox(Image image) {
		this.image = image;
		setShadow(4);
	}

    /* (non-Javadoc)
	 * @see jeliot.theater.Actor#paintActor(java.awt.Graphics)
	 */
	public void paintActor(Graphics g) {
        g.drawImage(image, 0, 0, null);
    }
    
    /* (non-Javadoc)
	 * @see jeliot.theater.Actor#calculateSize()
	 */
	public void calculateSize() {
        setSize(image.getWidth(null), image.getHeight(null));
    }
    
}

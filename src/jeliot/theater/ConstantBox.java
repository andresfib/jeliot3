package jeliot.theater;

import java.awt.Graphics;
import java.awt.Image;

/**
 * Constant box instance represents a place where all the literal constants
 * appear during the animation.
 * 
 * @author Pekka Uronen
 * @author Niko Myller
 */
public class ConstantBox extends Actor {

//  DOC: Document!

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
        //g.drawImage(image, 0, 0, null);
        g.drawImage(image, 0, 0, dummy);
    }
    
    /* (non-Javadoc)
	 * @see jeliot.theater.Actor#calculateSize()
	 */
	public void calculateSize() {
        //setSize(image.getWidth(null), image.getHeight(null));
        setSize(image.getWidth(dummy), image.getHeight(dummy));
    }
}

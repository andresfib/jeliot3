package jeliot.theater;

import java.awt.Graphics;
import java.awt.Image;

/**
  * @author Pekka Uronen
  *
  * created         22.9.1999
  */
public class ConstantBox extends Actor {

    private Image image;
    
    public ConstantBox(Image image) {
		this.image = image;
		setShadow(4);
	}

    public void paintActor(Graphics g) {
        g.drawImage(image, 0, 0, null);
    }
    
    public void calculateSize() {
        setSize(image.getWidth(null), image.getHeight(null));
    }
    
}

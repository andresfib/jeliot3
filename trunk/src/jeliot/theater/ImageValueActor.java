package jeliot.theater;

import java.awt.Graphics;
import java.awt.Image;

/**
  * @author Pekka Uronen
  *
  * created         23.9.1999
  */
public class ImageValueActor extends ValueActor {

    private Image image;

    public ImageValueActor(Image image) {
        this.image = image;
	}

    public void paintValue(Graphics g) {
        g.drawImage(image, 0, 0, null);
    }

    protected void calcLabelPosition() { }
    
    public void calculateSize() {
        setSize(image.getWidth(null), image.getHeight(null));
    }

}

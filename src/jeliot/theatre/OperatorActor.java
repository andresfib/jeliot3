package jeliot.theatre;

import java.awt.*;
import java.awt.image.*;
import jeliot.lang.*;

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

}

package jeliot.theatre;

import java.awt.*;
import java.awt.image.*;
import java.util.*;


/**
  * @author Pekka Uronen
  *
  * created         23.8.1999
  */
public class ImageLoader {

    /** Maps image names to loaded images. */ 
    private Hashtable images = new Hashtable();

    /** Maps images to their dark counterpants. */
    private Hashtable darks = new Hashtable();

    /** Maps logical image names to their real names. */
    private Hashtable mapping = new Hashtable(); {
        mapping.put("Splash screen", "splash.jpg");
        mapping.put("Panel", "curtain.gif");
        mapping.put("Panel-left", "curtleft.gif");
        mapping.put("Panel-right", "curtright.gif");
        mapping.put("Background", "misc040.jpg");
        mapping.put("Jeliot-icon", "jelicon.gif");
        mapping.put("Hand",     "hand1.gif");
        mapping.put("Fist-1",   "hand2.gif");
        mapping.put("Fist-2",   "hand3.gif");     
    }

    private Component comp = new Panel();
    private Toolkit toolkit = Toolkit.getDefaultToolkit();
    private MediaTracker tracker = new MediaTracker(comp);

    ImageFilter darkFilter = new RGBImageFilter() {
        {
            canFilterIndexColorModel = true;
        }

        public int filterRGB(int x, int y, int rgb) {
            int b = ( rgb & 0xFF ) / 2;
            int g = ( (rgb >> 8) & 0xFF ) / 2;
            int r = ( (rgb >> 16) & 0xFF ) / 2;
            rgb = (r << 16) | (g << 8) | b; 
            return ( (0xFF000000 | rgb) );
        }   
    };

    public Image getLogicalImage(String name) {
        String realName = (String)mapping.get(name);
        return getImage(realName);
    }
    
    public Image getImage(String name) {
        Image image = (Image)images.get(name);
        if (image == null) {
            image = toolkit.getImage("images/"+name);
            tracker.addImage(image, 0);
		    try {
		        tracker.waitForID(0);
		    }
    		catch (InterruptedException e) { }
		    images.put(name, image);
        }
        return image;
    }

    public Image darken(Image image) {
        Image dark = (Image)darks.get(image);
        if (dark == null) {
            ImageProducer producer = new FilteredImageSource(
                    image.getSource(), darkFilter);
            dark = comp.createImage(producer);
            tracker.addImage(dark, 0);
    		try {
    		    tracker.waitForID(0);
    		}
    		catch (InterruptedException e) { }
		    darks.put(image, dark);
        }
        return dark;
	}
}

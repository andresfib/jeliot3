package jeliot.theater;

import java.awt.Component;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Panel;
import java.awt.Toolkit;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.awt.image.RGBImageFilter;
import java.util.Hashtable;
import java.util.Locale;
import java.util.ResourceBundle;


/**
  * @author Pekka Uronen
  * @author Niko Myller
  */

public class ImageLoader {

    /**
     * The resource bundle for theater package.
     */
    static private ResourceBundle bundle = ResourceBundle.getBundle(
                                      "jeliot.theater.resources.properties",
                                      Locale.getDefault());

    /** Maps image names to loaded images. */
    private Hashtable images = new Hashtable();

    /** Maps images to their dark counterpants. */
    private Hashtable darks = new Hashtable();

	/* SHOULD BE REPLACED WITH RESOURCE BUNDLE! */
    // Maps logical image names to their real names.
    /*
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
    */

    /**
     *
     */
    private Component comp = new Panel();
    
    /**
	 *
	 */
	private Toolkit toolkit = Toolkit.getDefaultToolkit();
    
    /**
	 *
	 */
	private MediaTracker tracker = new MediaTracker(comp);

    /**
	 *
	 */
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


    /**
	 * @param name
	 * @return
	 */
	public Image getLogicalImage(String name) {
        //String realName = (String)mapping.get(name);
        //return getImage(realName);
        return getImage(bundle.getString(name));
    }


    /**
	 * @param name
	 * @return
	 */
	public Image getImage(String name) {
        Image image = (Image)images.get(name);
        if (image == null) {
            image = toolkit.getImage(bundle.getString("directory.images")+name);
            tracker.addImage(image, 0);
            try {
                tracker.waitForID(0);
            }
            catch (InterruptedException e) { }
            images.put(name, image);
        }
        return image;
    }

    /**
	 * @param image
	 * @return
	 */
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

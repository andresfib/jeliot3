package jeliot.theater;

import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.awt.image.RGBImageFilter;
import java.net.URL;
import java.util.Hashtable;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;
import javax.swing.JPanel;


/**
 * This class handles the image loading and caching for the animation.
 * 
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

//DOC: Document!
    
    /**
     * 
     */
    private JPanel comp = new JPanel();
    
    /**
     * 
     */
    private MediaTracker tracker = new MediaTracker(comp);
    
    /**
	 *
	 */
	private Toolkit toolkit = Toolkit.getDefaultToolkit();
    
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
		//return getImage(bundle.getString(name));
        URL imageURL = Thread.currentThread().getContextClassLoader().getResource(bundle.getString("directory.images")+bundle.getString(name));
        if (imageURL == null) {
            imageURL = (this.getClass().getClassLoader().getResource(bundle.getString("directory.images")+name));
        }
        return getImage(imageURL); 
    }


    /**
	 * @param name
	 * @return
	 */
	public Image getImage(String name) {
        Image image = (Image)images.get(name);
        if (image == null) {
            //image = toolkit.getImage(bundle.getString("directory.images")+name);
            //System.out.println(imageURL);
            //image = toolkit.getImage(imageURL);
            URL imageURL = Thread.currentThread().getContextClassLoader().getResource(bundle.getString("directory.images")+name);
            if (imageURL == null) {
                imageURL = (this.getClass().getClassLoader().getResource(bundle.getString("directory.images")+name));
            }
        	image = new ImageIcon(imageURL).getImage();
            images.put(name, image);
        }
        return image;
    }

    /**
	 * @param name
	 * @return
	 */
	public Image getImage(URL name) {
        Image image = (Image)images.get(name);
        if (image == null) {
        	//image = toolkit.getImage(name);
        	image = new ImageIcon(name).getImage();
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
            dark = toolkit.createImage(producer);
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

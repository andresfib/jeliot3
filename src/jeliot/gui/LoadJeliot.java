package jeliot.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.swing.JWindow;

import jeliot.theatre.ImageLoader;

/**
 * LoadJeliot displays a splash screen and starts the Jeliot application.
 * SHOULD NOT BE CURRENTLY IN USE IN JELIOT 3!
 *
 * @author Pekka Uronen
 */
public class LoadJeliot {

    /**
    * The resource bundle
    */
    static private ResourceBundle bundle = ResourceBundle.getBundle(
                                      "jeliot.gui.resources.properties",
                                      Locale.getDefault());

    /**
     * ImageLoader -object that loads all the images.
     */
    ImageLoader iLoad = new ImageLoader();

    /**
     * Jeliot window.
     */
    JWindow window;

    /**
     * Initializes the Jeliot's window.
     * Initializes the jeliot.Jeliot object.
     */
    public void start() {
        // Get the splash screen image
        final Image image = iLoad.getImage(bundle.getString("image.splash_screen"));

        // create the splash screen window
        Component splash = new Component() {
            public void paint(Graphics g) {
               g.drawImage(image, 0, 0, this);
            }
        };
        window = new JWindow();
        window.getContentPane().add(splash);

        // Set the window size to conform to the image and put the
        // window the center of the screen
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screen = toolkit.getScreenSize();
        int iw = image.getWidth(window);
        int ih = image.getHeight(window);

        window.setBounds((screen.width - iw)/2,
               (screen.height - ih)/2, iw, ih);
        window.setVisible(true);
        window.toFront();

        try {
            Class jeliotClass = Class.forName("jeliot.Jeliot");
            Runnable jeliot = (Runnable)jeliotClass.newInstance();
            jeliot.run();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        window.dispose();

    }


    /**
     * Starts the jeliot program.
     *
     * @param   args    Command line arguments for the program.
     * @throws  IOException If there is a problem in the opening of the file.
     */
    public static void main(String args[]) throws IOException {
        Properties prop = System.getProperties();
        String udir = prop.getProperty("user.dir");

        File f = new File(udir);
        f = new File(f.getParent(), "examples");
        prop.put("user.dir", f.toString());

        new LoadJeliot().start();
    }

}

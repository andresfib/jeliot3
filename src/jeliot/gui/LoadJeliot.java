package jeliot.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.JWindow;

import jeliot.Jeliot;
import jeliot.theater.ImageLoader;


/**
 * This class is not used in the current version of Jeliot.
 * LoadJeliot displays a splash screen and starts the Jeliot application.
 *
 * @author Pekka Uronen
 * @author Niko Myller
 */
public class LoadJeliot {

    /**
    * The resource bundle for gui package
    */
    static private ResourceBundle bundle = ResourceBundle.getBundle(
                                      "jeliot.gui.resources.messages",
                                      Locale.getDefault());

    /**
     * ImageLoader -object that loads all the images.
     */
    ImageLoader iLoad = new ImageLoader();

    /**
     * Initializes the Jeliot's splash screen window.
     * Initializes the jeliot.Jeliot object.
     */
    public void start(/*Runnable*/final Jeliot jeliot) {
        // Get the splash screen image
        final Image image = iLoad.getImage(bundle.getString("image.splash_screen"));

        // create the splash screen window
        Component splash = new Component() {
            public void paint(Graphics g) {
               g.drawImage(image, 0, 0, this);
            }
        };
        final JWindow window = new JWindow();
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
            //Thread.sleep(1000);
            javax.swing.SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    jeliot.run();
                    window.dispose();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }   
    }
}


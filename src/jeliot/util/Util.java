/*
 * Created on Nov 2, 2004
 */
package jeliot.util;

import java.awt.Color;
import java.io.File;
import java.net.URL;
import java.util.Random;

/**
 * @author nmyller
 */
public class Util {

    /**
     * Comment for <code>userPath</code>
     */
    private static File userPath;

    /**
     * 
     */
    private Util() {
    }

    public static boolean visualizeStringsAsObjects() {
        return ResourceBundles.getJeliotUserProperties().getBooleanProperty(
                "show_strings_as_objects");
    }

    /**
     * @return
     */
    public static File createUserPath() {
        if (userPath == null || !userPath.exists()) {
            //We take the first user home path as the one to be used.
            String path = System.getProperty("user.home").split(
                    System.getProperty("path.separator"))[0];

            if (!path.endsWith(System.getProperty("file.separator"))) {
                path += System.getProperty("file.separator");
            }
            path += ".jeliot" + System.getProperty("file.separator");
            userPath = new File(path);
            if (!userPath.exists()) {
                userPath.mkdir();
            }
            //DebugUtil.printDebugInfo(userPath.getAbsolutePath());
        }
        return userPath;
    }

    public static URL getResourceURL(String resource, Class loader) {
        URL imageURL = Thread.currentThread().getContextClassLoader()
                .getResource(resource);
        if (imageURL == null) {
            imageURL = (loader.getClassLoader().getResource(resource));
        }
        return imageURL;
    }
    public static Color getRandomColor() {
    	Random component = new Random();
    	return new Color (component.nextInt(255),component.nextInt(255),component.nextInt(255));     	
    }
}

import java.net.URL;

import bluej.extensions.BlueJ;
import bluej.extensions.Extension;
import bluej.extensions.ExtensionException;
import bluej.extensions.event.PackageEvent;
import bluej.extensions.event.PackageListener;

/**
 * 
 * @author apineau
 *
 * Jeliot extension for BlueJ
 * 
 * 
 * Main Class
 */
public class JeliotExtension extends Extension implements PackageListener {

    /**
     * When this method is called, the extension may start its work.
     */
    public void startup(BlueJ bluej) {
        bluej.setMenuGenerator(new MenuBuilder(bluej));
        bluej.addPackageListener(this);
    }

    /**
     * The package has been opened. Print the name of the project it is part of.
     */
    public void packageOpened(PackageEvent ev) {
        try {
            String packageName = ev.getPackage().getProject().getName();
            System.out.println("Project " + packageName + " opened.");
        } catch (ExtensionException e) {
            System.out.println("Project closed by BlueJ");
        }
    }

    /**
     * The package is closing.
     */
    public void packageClosing(PackageEvent ev) {
        try {
            String packageName = ev.getPackage().getProject().getName();
            System.out.println("Project " + packageName + " closed.");
        } catch (ExtensionException e) {
            System.out.println("Project closed by BlueJ");
        }        
    }

    /**
     * This method must decide if the Jeliot Extension is compatible with the 
     * current release of the BlueJ Extensions API
     */
    public boolean isCompatible() {
        return true;
    }

    /**
     * version number of the extension
     */
    public String getVersion() {
        return ("3.5.2");
    }

    /**
     * Returns the user-visible name of this extension
     */
    public String getName() {
        return ("Jeliot Extension");
    }

    public void terminate() {
        System.out.println("Jeliot extension terminates");
    }

    public String getDescription() {
        return "Jeliot 3 is a Program Visualization application. You can load the active project into Jeliot and animate it!";
    }

    /**
     * Returns a URL where you can find info on this extension.
     * The real problem is making sure that the link will still be alive in three years...
     */
    public URL getURL() {
        try {
            return new URL("http://cs.joensuu.fi/jeliot/");
        } catch (Exception eee) {
            // There is no reason at all that this should trow exception...
            System.out.println("Simple extension: getURL: Exception=" + eee.getMessage());
            return null;
        }
    }
}

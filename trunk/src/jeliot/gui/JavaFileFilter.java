package jeliot.gui;

import java.io.File;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.filechooser.FileFilter;

public class JavaFileFilter extends FileFilter {

    /**
     * The resource bundle
     */
    static private ResourceBundle bundle = ResourceBundle.getBundle(
                                      "jeliot.gui.resources.properties",
                                      Locale.getDefault());

    public boolean accept(File f) {
    if(f != null) {
        if(f.isDirectory()) {
        return true;
        }
        String extension = getExtension(f);
        if(extension != null) {
                if (extension.toLowerCase().equals(bundle.getString("extension.java"))) {
                    return true;
                }
        };
    }
    return false;
    }

    /**
     * Return the extension portion of the file's name .
     *
     */
     public String getExtension(File f) {
    if(f != null) {
        String filename = f.getName();
        int i = filename.lastIndexOf('.');
        if(i>0 && i<filename.length()-1) {
        return filename.substring(i+1).toLowerCase();
        };
    }
    return null;
    }

    /**
     * Returns the human readable description of this filter. For
     * example: "JPEG and GIF Image Files (*.jpg, *.gif)"
     */
    public String getDescription() {
    return bundle.getString("extension.java.description");
    }
}

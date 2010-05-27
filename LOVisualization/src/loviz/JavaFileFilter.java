/* Copyright 2005 by Mordechai (Moti) Ben-Ari. See copyright.txt. */
package loviz;
import java.io.File;
import javax.swing.filechooser.*;

/*
 * JavaFileFilter:
 *   A file filter for .java files
*/
class JavaFileFilter extends FileFilter {

    public boolean accept(File f) {
        if (f.isDirectory())
            return true;
        else {
            String e = f.getName().toUpperCase();
            return e.endsWith(".JAVA"); 
        }
    }

    public String getDescription() {
        return "Java files";
    }
}

/*
 * Created on Nov 2, 2004
 */
package jeliot.util;

import java.io.File;

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

}
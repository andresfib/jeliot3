/*
 * Copyright (c) 2004 Roland Küstermann. All Rights Reserved.
 */
package jeliot;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.security.AllPermission;
import java.security.CodeSource;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.security.Policy;
import java.util.Properties;

import javax.swing.SwingUtilities;

import jeliot.tracker.Tracker;

/**
 * This is an extension of the application class of Jeliot 3 that
 * adds features for JavaWS and Url Loading
 *
 */
public class Il3JeliotPlugin extends Jeliot {

    public Il3JeliotPlugin(String udir, boolean experiment) {
        super(udir, experiment);
    }

    /**
     * get Program from url
     */
    public void setProgram(final URL u) {
        SwingUtilities.invokeLater(new Runnable(
        ) {
            public void run() {
                try {
                    System.out.println("Reading from u = " + u);
                    BufferedReader bin = new BufferedReader(new InputStreamReader(u.openStream()));
                    String line = null;
                    StringBuffer content = new StringBuffer();
                    while ((line = bin.readLine()) != null)
                        content.append (line).append ("\n");
                    bin.close();
                    if (content.length() > 0)
                        gui.setProgram(content.toString());
                } catch (IOException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        });
    }

    public static void main(String[] args) {
        /**
         * JavaWS: classes loaded afterwards in a signed package have to receive the same privileges as those from signed archive
         */

        Policy.setPolicy(new Policy() {
            public PermissionCollection getPermissions(CodeSource codesource) {
                Permissions perms = new Permissions();
                perms.add(new AllPermission());
                return (perms);
            }

            public void refresh() {
            }
        });

        Properties prop = System.getProperties();
        String udir = prop.getProperty("user.dir");

        if (args.length >= 2) {
            Tracker.setTrack(Boolean.valueOf(args[1]).booleanValue());
        }

        boolean experiment = false;
        if (args.length >= 3) {
            experiment = Boolean.valueOf(args[2]).booleanValue();
        }

        //Just for tracking the user
        File f = new File(udir);
        Tracker.openFile(f);

        //f = new File(f, "examples");
        //prop.put("user.dir", f.toString());

        final Il3JeliotPlugin jeliot = new Il3JeliotPlugin(udir, experiment);

        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                jeliot.run();
            }
        });

        if (args.length >= 1) {
            File file = new File(udir);
            file = new File(file, "examples");
            final File file1 = new File(file, args[0]);
            if (file.exists()) {
                javax.swing.SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        jeliot.setProgram(file1);
                    }
                });
            } else {
                try {
                    URL u = new URL(URLDecoder.decode(args[0]));
                    jeliot.setProgram(u);
                } catch (MalformedURLException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        }
    }

}

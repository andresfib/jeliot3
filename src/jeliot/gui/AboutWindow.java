package jeliot.gui;

import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;

/**
 *
 * The design of the help window that maybe will be
 * added inside Jeliot's next version.
 *
 * @author Pekka Uronen
 *
 */
public class AboutWindow extends JFrame {


    /**
     * The resource bundle
     */
    static private ResourceBundle bundle = ResourceBundle.getBundle(
                                      "jeliot.gui.resources.properties",
                                      Locale.getDefault());

    /**
     * The tabbed pane where about info and GPL  will be shown.
     */
    private JEditorPane aboutPane = new JEditorPane();
    private JScrollPane jsp;

    /**
     * The Constructor constructs the HelpWindow.
     * Constructs the JFrame.
     * Sets inside the JFrame JScrollPane with JEditorPane editorPane.
     * Sets the size of the JFrame as 400 x 600
     */
    public AboutWindow(Image icon, String udir) {
        super();
        setTitle(bundle.getString("window.about.title"));
        setIconImage(icon);

        aboutPane.setEditable(false);

        jsp = new JScrollPane(aboutPane);
        jsp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        try {
            File f = new File(udir, bundle.getString("window.about.content"));
            showURL(f.toURI().toURL());
        } catch (Exception e) { e.printStackTrace(); }

        getContentPane().add(jsp);
        setSize(600, 600);
    }

    /**
     * Shows the given url in the editor pane.
     *
     * @param   url The given url will be showed in JEditorPane editorPane.
     */
    public void showURL(URL url) {
        try {
            aboutPane.setPage(url);
            //show();
        } catch (IOException e) {
            System.err.println(bundle.getString("bad.URL") + " " + url);
        }
    }
}

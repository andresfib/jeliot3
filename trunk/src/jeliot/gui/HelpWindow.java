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
 * The design of the help window that maybe will be added
 * inside Jeliot's next version.
 *
 * @author Pekka Uronen
 *
 */
public class HelpWindow extends JFrame {

    /**
     * The editor pane where the help will be shown.
     */
    private JEditorPane editorPane = new JEditorPane();
    private JScrollPane jsp;

    /**
     * The Constructor constructs the HelpWindow.
     * Constructs the JFrame.
     * Sets inside the JFrame JScrollPane with JEditorPane editorPane.
     * Sets the size of the JFrame as 400 x 600
     */
    public HelpWindow(Image icon, String udir) {
        super("Help");
        setIconImage(icon);

        editorPane.setEditable(false);

        jsp = new JScrollPane(editorPane);
        jsp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        try {
            showURL((new URL("file://" + udir + "\\docs\\help.html")).toString(), editorPane);
        }catch (Exception e) { e.printStackTrace(); }

        getContentPane().add(jsp);
        setSize(600, 600);
    }


    /**
     * Shows the given url in the editor pane.
     *
     * @param   url The given url will be showed in JEditorPane editorPane.
     */

    public void showURL(String url, JEditorPane pane) {
        try {
            pane.setPage(url);
            //show();
        }
        catch (IOException e) {
            System.err.println("Attempted to read a bad URL: " + url);
        }
    }

}

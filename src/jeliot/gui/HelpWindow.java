package jeliot.gui;

import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;

import javax.swing.*; 
import javax.swing.event.*; 

/**
 * NOT CURRENTLY USED IN JELIOT!
 * The design of the help window that maybe will be added inside Jeliot's next version.
 *
 * @author Pekka Uronen
 *
 */
public class HelpWindow extends JFrame {

	/**
	 * The editor pane where the help will be shown.
	 */
    private JEditorPane editorPane = new JEditorPane();

	/**
	 * The Constructor constructs the HelpWindow.
	 * Constructs the JFrame.
	 * Sets inside the JFrame JScrollPane with JEditorPane editorPane.
	 * Sets the size of the JFrame as 400 x 600
	 */
    public HelpWindow() {
        super("Help");
        editorPane.setEditable(false);
        setContentPane(new JScrollPane(editorPane));
        setSize(400, 600);
    }



	/**
	 * Shows the given url in the editor pane.
	 *
	 * @param	url	The given url will be showed in JEditorPane editorPane.
	 */
    public void showURL(URL url) {
        try {
            editorPane.setPage(url);
            show();
        } 
        catch (IOException e) {
            System.err.println("Attempted to read a bad URL: " + url);
        } 
    }
}

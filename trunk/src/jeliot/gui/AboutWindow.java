package jeliot.gui;

import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;
import java.util.*;

import javax.swing.*; 
import javax.swing.event.*; 

/**
 * NOT CURRENTLY USED IN JELIOT!
 * The design of the help window that maybe will be added inside Jeliot's next version.
 *
 * @author Pekka Uronen
 *
 */
public class AboutWindow extends JFrame {

	/**
	 * The tabbed pane where about info and GPL  will be shown.
	 */
    private JEditorPane aboutPane = new JEditorPane();
    private JScrollPane jsp = new JScrollPane();

	/**
	 * The Constructor constructs the HelpWindow.
	 * Constructs the JFrame.
	 * Sets inside the JFrame JScrollPane with JEditorPane editorPane.
	 * Sets the size of the JFrame as 400 x 600
	 */
    public AboutWindow(Image icon, String udir) {
        super("About");
        // frameInit();

        setIconImage(icon);

        aboutPane.setEditable(false);
        try {
            showURL((new URL("file://" + udir + "\\docs\\about.html")).toString(), aboutPane);
        } catch (Exception e) { e.printStackTrace(); }

        jsp.add(aboutPane);
        //jsp.setScrollbarPolicy(JScrollbar.ALWAYS_VERTICALSCROLLBAR);
        getContentPane().add(jsp);

        /*
        addWindowListener(
            new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    dispose();
                }
            }
        );
        */
        setSize(600, 600);
        //setContentPane(tabbedPane);
        //pack();
        //show(); 
    }
    


    

	/**
	 * Shows the given url in the editor pane.
	 *
	 * @param	url	The given url will be showed in JEditorPane editorPane.
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

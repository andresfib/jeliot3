/*
 * Created on 28.10.2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package jeliot.gui;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import jeliot.mcode.Util;
import jeliot.util.ResourceBundles;


/**
 * When creating a subclass of infoWindow you should create a public
 * constructor that populates the udir and fileName fields and calls
 * reload() method.
 * 
 * @author Niko Myller
 */
public abstract class InfoWindow extends JFrame implements HyperlinkListener {
    
    /**
     * The resource bundle for gui package.
     */
    static protected ResourceBundle messageBundle = ResourceBundles.getGuiMessageResourceBundle();

    /**
     * The pane where helping information will be shown.
     */
    protected JEditorPane editorPane = new JEditorPane();

    /**
     * The pane that handles the scrolling of the editor pane showing the content.
     */
    protected JScrollPane jsp;

    
    /**
     * User directory where Jeliot was loaded.
     */
    protected String udir;    
    
    /**
     * File name where the content should be read.
     */
    protected String fileName;
    
    /**
     * 
     *
     */
    public InfoWindow() {
        super();
        
        editorPane.setEditable(false);
        editorPane.addHyperlinkListener(this);

        jsp = new JScrollPane(editorPane);
        jsp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        getContentPane().add(jsp);

    }

    
    /**
     * 
     */
    public void reload() {
        try {
            File f = new File(udir, fileName);
            showURL(f.toURI().toURL());
        } catch (Exception e) {
            if (Util.DEBUGGING) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Shows the given url in the editor pane.
     *
     * @param   url The document in the url will be showed in JEditorPane editorPane.
     */
    public void showURL(URL url) {
        try {
            editorPane.setPage(url);
        } catch (IOException e) {
            System.err.println(messageBundle.getString("bad.URL") + " " + url);
        }
    }

    /* (non-Javadoc)
     * @see javax.swing.event.HyperlinkListener#hyperlinkUpdate(javax.swing.event.HyperlinkEvent)
     */
    public void hyperlinkUpdate(HyperlinkEvent e) {
        if (e.getEventType().toString().equals(HyperlinkEvent.EventType.ACTIVATED.toString())) {
            showURL(e.getURL());
        }
    }    
}

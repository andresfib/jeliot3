package jeliot.gui;

import java.awt.Image;

/**
 *
 * The design of the about window
 * 
 * @author Niko Myller
 * @author Andrés Moreno
 *
 */
public class AboutWindow extends InfoWindow {

    /**
     * constructs the AboutWindow by creating a JFrame.
     * Sets inside the JFrame JScrollPane with JEditorPane editorPane.
     * Sets the size of the JFrame as 600 x 600
     * @param icon Icon to be shown in the upper right corner of the window.
     * @param udir directory of the current invocation 
     */
    public AboutWindow(Image icon, String udir) {
        super();
        setTitle(messageBundle.getString("window.about.title"));
        setIconImage(icon);

        this.udir = udir;
        this.fileName = messageBundle.getString("window.about.content");
        reload();

        getContentPane().add(jsp);
        setSize(600, 600);
    }

}
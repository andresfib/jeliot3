package jeliot.gui;

import java.awt.Image;

/**
 *
 * The design of the help window.
 *
 * @author Niko Myller
 * @author Andrés Moreno
 *
 */
public class HelpWindow extends InfoWindow {

    /**
     * constructs the HelpWindow by creating a JFrame.
     * Sets inside the JFrame JScrollPane with JEditorPane editorPane.
     * Sets the size of the JFrame as 400 x 600
     * @param icon Icon to be shown in the upper right corner of the window.
     * @param udir directory of the current invocation 
     */
    public HelpWindow(Image icon, String udir) {
        super();
        setTitle(messageBundle.getString("window.help.title"));
        setIconImage(icon);

        this.udir = udir;
        this.fileName = messageBundle.getString("window.help.content");
        
        reload();
        setSize(600, 600);
    }
}
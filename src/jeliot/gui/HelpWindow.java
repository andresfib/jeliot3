package jeliot.gui;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;

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
     * The resource bundle
     */
    static private ResourceBundle bundle = ResourceBundle.getBundle(
                                      "jeliot.gui.resources.properties",
                                      Locale.getDefault());

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
        super();
        setTitle(bundle.getString("window.help.title"));
        setIconImage(icon);

        editorPane.setEditable(false);

        jsp = new JScrollPane(editorPane);
        jsp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        try {
            File f = new File(udir, bundle.getString("window.help.content"));
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
            editorPane.setPage(url);
            //show();
        } catch (IOException e) {
            System.err.println(bundle.getString("bad.URL") + " " + url);
        }
    }

}

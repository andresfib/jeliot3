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
 * The design of the about window
 * 
 * @author Niko Myller
 * @author Andrés Moreno
 *
 */
public class AboutWindow extends JFrame {

	/**
	 * The resource bundle for gui package
	 */
	static private ResourceBundle bundle =
		ResourceBundle.getBundle(
			"jeliot.gui.resources.properties",
			Locale.getDefault());

	/**
	 * The pane where info about the program and Licensing will be shown.
	 */
	private JEditorPane aboutPane = new JEditorPane();

	/**
	 * The pane that handles the scrolling of the editor pane showing the content.
	 */
	private JScrollPane jsp;

	/**
	 * constructs the AboutWindow by creating a JFrame.
	 * Sets inside the JFrame JScrollPane with JEditorPane editorPane.
	 * Sets the size of the JFrame as 400 x 600
	 * @param icon Icon to be shown in the upper right corner of the window.
	 * @param udir directory of the current invocation 
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
		} catch (Exception e) {
			e.printStackTrace();
		}

		getContentPane().add(jsp);
		setSize(600, 600);
	}

	/**
	 * Shows the given url in the editor pane.
	 *
	 * @param url The document in the url will be showed in JEditorPane editorPane.
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

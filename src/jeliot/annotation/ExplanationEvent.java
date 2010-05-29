package jeliot.annotation;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.ResourceBundle;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

import jeliot.util.ResourceBundles;



public class ExplanationEvent {
	static private ResourceBundle messageBundle = ResourceBundles.getAnnotationMessageResourceBundle();
	Dimension d_screen = Toolkit.getDefaultToolkit ().getScreenSize();
	/*
	 *Set coordinates for message dialog to make it closed to the top-right of the screen.After
	 *trying several fractions for the width of the screen,it is proved the fraction should be bigger than 
	 *3/5,and smaller than 2/3.
	 */ 
	double x1= 5*d_screen.getWidth()/8;
	double y1= d_screen.getHeight()/22;
	int x= new Double(x1).intValue();
	int y= new Double(y1).intValue();

	//private JButton button = new JButton(messageBundle.getString("message.button1"));

	public void explanationDisplay(String name,String explanation){
		//First part:dialog part
		Object[] options = {messageBundle.getString("message.button")};
		//Object[] options = {messageBundle.getString("message.button"),messageBundle.getString("message.button1")};
		JOptionPane pane = new JOptionPane (name, JOptionPane.INFORMATION_MESSAGE, JOptionPane.OK_OPTION, null, options); 

		JDialog dialog = pane.createDialog(null,messageBundle.getString("dialog.message.title"));
		Container content = dialog.getContentPane( );

		//Second part:text area for more information
		JTextArea text = new JTextArea (explanation, 8, 40);

		//allow Wrapped text to fit in width
		text.setLineWrap (true);
		//wrapped at word boundaries
		text.setWrapStyleWord (true);
		text.setEditable(false);


		JScrollPane scroller =
			new JScrollPane (text, 
					ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, 
					ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		/* try to make JscrollPane translucent.There are three steps:
		 * --set scroller translucent;
		 * --set getviewport translucent;
		 * --set text on the scroller translucent;
		 * However,these don't work.
		    scroller.setOpaque(false);
            scroller.getViewport().setOpaque(false);
		    text.setOpaque(false);*/

		//mix the first and second part 
		InfoPanel mix = new InfoPanel(content,scroller);
		dialog.setContentPane(mix);

		dialog.pack();
		/* Setlocation() method only has been put before Setvisible() method 
        because the size of the dialog has not been determined.*/
		dialog.setLocation(x, y);
		dialog.setVisible(true);


	} 


}


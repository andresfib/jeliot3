package jeliot.annotation;


import javax.swing.*;

import java.awt.*;
import jeliot.util.ResourceBundles;
import java.util.ResourceBundle;


public class AnnotationEngine {
    static private ResourceBundle messageBundle = ResourceBundles.getTheaterMessageResourceBundle();


	private String name;
	/*private boolean isInConstructor()throws Exception{
		
		return true;
	}*/
	public void setConstructorCall(String methodCall){
		this.name = methodCall;
	}
	public String getConstructorCall(){
		return name;
	}
	/*public void createTabbedPane(){
	ImageIcon icon = createImageIcon("/images/middle.gif");
    JFrame frame = new JFrame("Tabbed Pane Frame");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    JTabbedPane tab = new JTabbedPane();
    frame.add(tab, BorderLayout.CENTER);
    JButton button = new JButton("1");
    tab.addTab("Tab 1", icon, button);
    button = new JButton("2");
    tab.addTab("Tab 2", icon, button);

    frame.setSize(400,400);
    frame.setVisible(true);
  }
	 protected static ImageIcon createImageIcon(String path) {
	        java.net.URL imgURL = AnnotationEngine.class.getResource(path);
	        if (imgURL != null) {
	            return new ImageIcon(imgURL);
	        } else {
	            System.err.println("Couldn't find file: " + path);
	            return null;
	        }
	    }*/
	public void explanationMCDisplay(){
		
		//First part:dialog part
		JOptionPane pane = new JOptionPane (name+"(): "+messageBundle.getString("message.constructor_call"), JOptionPane.INFORMATION_MESSAGE);
        JDialog dialog = pane.createDialog (null, messageBundle.getString("dialog.message.title"));
        Container container = dialog.getContentPane( );
        
        //Second part:text area for more information
        JTextArea text = new JTextArea (messageBundle.getString("message.constructor_explanation"), 8, 40);
       //allow Wrapped text to fit in width
        text.setLineWrap (true);
        //wrapped at word boundaries
        text.setWrapStyleWord (true);
        text.setEditable(false);

        JScrollPane scroller =
            new JScrollPane (text, 
                             ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, 
                             ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        //mix the first and second part 
        InfoPanel mix = new InfoPanel (container, scroller);
        dialog.setContentPane (mix);
        //Window to be sized to fit the preferred size 
        dialog.pack( );
        dialog.setVisible(true);
  
    } 
}
	

	/*public void explanationVDDisplay(){
		JOptionPane.showMessageDialog(null,"The following step is a variable declaration","Explanation",JOptionPane.PLAIN_MESSAGE);  }

	public void explanationAADisplay(){
		JOptionPane.showMessageDialog(null,"The following step is a array allocation","Explanation",JOptionPane.PLAIN_MESSAGE);  }

	public void explanationAACDisplay(){
		JOptionPane.showMessageDialog(null,"The following step is about that array can be accessed","Explanation",JOptionPane.PLAIN_MESSAGE);  }
	
	public void explanationRDisplay(){
		JOptionPane.showMessageDialog(null,"The following step indicates it will return a value","Explanation",JOptionPane.PLAIN_MESSAGE);  }

	public void explanationSADisplay(){
		JOptionPane.showMessageDialog(null,"The following step is about class allocation","Explanation",JOptionPane.PLAIN_MESSAGE);  }
	*/


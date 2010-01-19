package jeliot.annotation;


import javax.swing.*;

import java.awt.*;


import jeliot.util.ResourceBundles;
import java.util.ResourceBundle;


public class AnnotationEngine {
    static private ResourceBundle messageBundle = ResourceBundles.getTheaterMessageResourceBundle();
	
    /**
	 * This class is used to show the pass the different events to the InfoPanel.
	 */
    ExplanationEvent explanationevent = new ExplanationEvent();

	private String name;
	private String value;
	private String variable;
	
	//private Value[] arguments;
	/*private boolean isInConstructor()throws Exception{
		
		return true;
	}*/
	public void setConstructorCall(String methodCall){
		this.name = methodCall;
	}
	public String getConstructorCall(){
		return name + "( ):" + messageBundle.getString("message.constructor_call");
	}
    public void setArgument(String val,String var){
        this.value = val;
        this.variable = var;
    }
    public String getArgument(){
    	return value + messageBundle.getString("message.argument") + variable + ".";
    }


	/**
	 * TabbedPane is not fit in this situation.
	 */
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
    
    //This method is used to give different events to InfoPanel. 
    public void explainConstructor(String name){
    	setConstructorCall(name);
    	String a = getConstructorCall();
    	String b = messageBundle.getString("message.constructor_explanation");
    	 
    	explanationevent.explanationDisplay(a,b);

    }
    public void explainArgument(String val,String var){
    	setArgument(val,var);
    	
    	String a = getArgument();
    	String b = messageBundle.getString("message.argument_explanation");
    	
    	explanationevent.explanationDisplay(a,b);

    }
	/*public void explanationMCDisplay(){
		
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
  
    } */

	


		

}
	/*public void explanationAADisplay(){
		JOptionPane.showMessageDialog(null,"The following step is a array allocation","Explanation",JOptionPane.PLAIN_MESSAGE);  }

	public void explanationAACDisplay(){
		JOptionPane.showMessageDialog(null,"The following step is about that array can be accessed","Explanation",JOptionPane.PLAIN_MESSAGE);  }
	
	public void explanationRDisplay(){
		JOptionPane.showMessageDialog(null,"The following step indicates it will return a value","Explanation",JOptionPane.PLAIN_MESSAGE);  }

	public void explanationSADisplay(){
		JOptionPane.showMessageDialog(null,"The following step is about class allocation","Explanation",JOptionPane.PLAIN_MESSAGE);  }
	*/


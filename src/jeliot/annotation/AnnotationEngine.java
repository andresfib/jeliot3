package jeliot.annotation;


import javax.swing.*;

import java.awt.*;


import jeliot.lang.Value;
import jeliot.lang.Variable;
import jeliot.mcode.MCodeUtilities;
import jeliot.util.ResourceBundles;
import java.util.ResourceBundle;


public class AnnotationEngine {
    static private ResourceBundle messageBundle = ResourceBundles.getTheaterMessageResourceBundle();
	
    /**
	 * This class is used to show the pass the different events to the InfoPanel.
	 */
    ExplanationEvent explanationevent = new ExplanationEvent();

	private String constructorname;
	private Value argumentvalue;
	private String variable;
	private String object;
	private String objectfield;
	
	private String value;
	
	
	//private Value[] arguments;
	/*private boolean isInConstructor()throws Exception{
		
		return true;
	}*/
	public void setConstructorCall(String methodCall){
		this.constructorname = methodCall;
	}
	public String getConstructorCall(Value[] v){
		//Determining if the constructor is a non-parameter one. 
		if(v.length != 0)
		   return constructorname + "( ):" + messageBundle.getString("message.constructor_call");
		else
		   return constructorname + "( ):" + messageBundle.getString("message.constructor_call")+messageBundle.getString("message.nonparamconstructor_call");
	}
	
	
    public void setArgument(Value val,String var){
        this.argumentvalue = val;
        this.variable = var;
    }
    public String getArgument(){
 
    	   return argumentvalue.toString() + messageBundle.getString("message.argument") + variable + "().";

    }
    
    
    public void setObject(String name){
    	this.object = name;
    	
    }
    public String getObject(){
    	return object + " " + messageBundle.getString("message.object");
    }
    
    
    public void setObjectField(String name){
    	this.objectfield = name;
    }
    public String getObjectField(){
    	return messageBundle.getString("message.objectfield")+ objectfield + ".";
    }
    
    public void setArrow(String v){
    	
    	this.value = v;
    }
    public String getArrow(){
    	return  value + " " +messageBundle.getString("message.arrow");
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
    
    //This method is used to give information of Constructor to InfoPanel. 
    public void explainConstructor(Value[] value,String name){
    	
    	
    	setConstructorCall(name);
    	String a = getConstructorCall(value);
    	String b = messageBundle.getString("message.constructor_explanation");
    	 
    	explanationevent.explanationDisplay(a,b);

    }
    //This method is used to give information of arguments to Ifo Panel.
    public void explainArgument(Value val,String var){
    	
    	
    	String type = val.getType();
    	//Determining if the argument is a primitive type.
        if (MCodeUtilities.isPrimitive(type)) {
        	setArgument(val,var);    	
            String a = getArgument();
        	String b = messageBundle.getString("message.argument_explanation");
        	explanationevent.explanationDisplay(a,b); 
        	}  
        else { 
           String c = messageBundle.getString("message.pointerargument");
           String d = messageBundle.getString("message.pointerarg_explanation");
           explanationevent.explanationDisplay(c,d);
            }

    }
    //This method is to show information when object is instantiated.
    public void explainObject(String name){
    	setObject(name);
    	String a = getObject();
    	String b = messageBundle.getString("message.object_explanation");
    	explanationevent.explanationDisplay(a,b);
    }
    public void explainObjectField(String name){
    	setObjectField(name);
    	String a = getObjectField();
    	String b = messageBundle.getString("message.objectfield_explanation");
    	explanationevent.explanationDisplay(a,b);	
    	
    }
    //This is for explaining arrow when it happens.
    public void explainArrow(String value){
    	setArrow(value);
    	String a = getArrow();
    	String b = messageBundle.getString("message.arrow_explanation");
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


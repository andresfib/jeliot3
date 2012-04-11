package jeliot.annotation;



import jeliot.lang.Value;
import jeliot.lang.Variable;
import jeliot.mcode.MCodeUtilities;
import jeliot.util.ResourceBundles;
import jeliot.util.UserProperties;

import java.util.ResourceBundle;


public class AnnotationEngine {
	static private ResourceBundle messageBundle = ResourceBundles.getAnnotationMessageResourceBundle();

	/**
	 * This class is used to pass the different events to the InfoPanel.
	 */
	ExplanationEvent explanationevent = new ExplanationEvent();

	/**
	 *This is determined whether showing the message dialog or not,show is the trigger. 
	 */
	private UserProperties jeliotUserProperties = ResourceBundles
	.getJeliotUserProperties();
	private Boolean show = jeliotUserProperties.getBooleanProperty("show_annotations");

	//for explaining constructor
	private String constructorname;

	//for explaining arguments
	private String argumentexplanation;
	private String argumentshow;
	private String variable;

	//for explain object
	private String object;

	//for explain field
	private String objectfield;	

	//for explain arrow
	private String value;

	//for explaining reference to the object of the class
	private String reference;

	/*private boolean isInConstructor()throws Exception{

		return true;
	}*/ 
	public void setConstructorCall(String methodCall){
		this.constructorname = methodCall;
	}
	public String getConstructorCall(Value[] v){
		//Determining if the constructor is a non-parameter one. 
		if(v.length != 0)

		   return messageBundle.getString("message.constructor_call")+ " " + constructorname +" "+messageBundle.getString("message.constructor_call2");

			//return constructorname + "( ):" + messageBundle.getString("message.constructor_call");

		else

		   return messageBundle.getString("message.constructor_call")+ " " + constructorname + "."+messageBundle.getString("message.nonparamconstructor_call");

			//return constructorname + "( ):" + messageBundle.getString("message.constructor_call")+messageBundle.getString("message.nonparamconstructor_call");

	}



    
       
    public void setObject(String name){
    	this.object = name;
    	
    }
    public String getObject(boolean a){
    	if(a == true)
    	   return object + " " + messageBundle.getString("message.object");
    	else 
    	   return object + " " + messageBundle.getString("message.objectdelete");
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
    	return  messageBundle.getString("message.arrow1")+" "+value+" "+messageBundle.getString("message.arrow2");
    }
    
    public void setReference(String ref){
    	this.reference = ref;
    }
    public String getReference(){
    	return reference;
    }

	public void setArgument(Variable[] val,int n,String var){
		String arguments = messageBundle.getString("message.argument1");
		this.variable = var;
		this.argumentexplanation = messageBundle.getString("message.argument_explanation");
		String pointerargument = messageBundle.getString("message.pointerargument");
		for(int i=0;i<n;i++){
			String type = val[i].getType();
			//Determining if the argument is a primitive type.
			if (MCodeUtilities.isPrimitive(type)) {
				Value argumentvalue = val[i].getValue();
				String value = argumentvalue.toString();
				arguments = arguments + " " + value + ",";
				this.argumentshow = arguments;
			}
			else{       	
				arguments += pointerargument;
				this.argumentexplanation += messageBundle.getString("message.pointerarg_explanation");
			}
		}
	}
	public String getArgument(){

		return argumentshow + " "+ messageBundle.getString("message.argument2")+ " " + variable + "().";

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

	//This method is used to give information of Constructor to Info Panel. 
	public void explainConstructor(Value[] value,String name){

		if(show){
			setConstructorCall(name);
			String a = getConstructorCall(value);
			String b = messageBundle.getString("message.constructor_explanation");

			explanationevent.explanationDisplay(a,b);
		}
		else 
			return ;
	}
	//This method is used to give information of arguments to Info Panel.
	public void explainArgument(Variable[] val,Value[] args,String var){


		if(show){
			//determining whether arguments are not null 
			if (args != null && args.length > 0){
				setArgument(val,args.length,var);
				String a = getArgument();      
				String b = argumentexplanation.toString();
				explanationevent.explanationDisplay(a,b);
				/*String type = val[i].getType();
    	//Determining if the argument is a primitive type.
        if (MCodeUtilities.isPrimitive(type)) {
        	setArgument(val[i],var);    	
            String a = getArgument();
        	String b = messageBundle.getString("message.argument_explanation");
        	explanationevent.explanationDisplay(a,b); 
        	}  
        else {

           String c = messageBundle.getString("message.pointerargument");
           String d = messageBundle.getString("message.pointerarg_explanation");
           explanationevent.explanationDisplay(c,d);
            }
    		}*/
			}
		}
		else 
			return ;
	}
	//This method is to show information when object is instantiated.
	public void explainObject(String name){
		if(show){
			setObject(name);
			String a = getObject(true);
			String b = messageBundle.getString("message.object_explanation");
			explanationevent.explanationDisplay(a,b);
		}
		else 
			return ;
	}
	//This method is to explain field when it is allocated. 
	public void explainObjectField(String name){
		if(show){
			setObjectField(name);
			String a = getObjectField();
			String b = messageBundle.getString("message.objectfield_explanation");
			explanationevent.explanationDisplay(a,b);	
		}
	}
	/*This is for explaining arrow when it happens.When arrow appears,
    that means method can be accessed by the reference to the object of the class.*/
	public void explainArrow(String value){
		if(show){
			setArrow(value);
			String a = getArrow();
			String b = messageBundle.getString("message.arrow_explanation");
			explanationevent.explanationDisplay(a,b);
		}	
	}

	//when return reference happens,show explanation.
	public void explainReturnReference(){
		if(show){

			String a = messageBundle.getString("message.returnreference");
			String b = messageBundle.getString("message.returnref_explanation");
			explanationevent.explanationDisplay(a,b);
		}
	}



	//when reference to the object of the class is assigned,explanation will be happened.
	public void explainAssignReference(String type){
		if(show){
			if (MCodeUtilities.isPrimitive(type) || type.equals("null")) {
			}
			else{
				String a = messageBundle.getString("message.assignreference");
				String b = messageBundle.getString("message.assignref_explanation");
				if(type.charAt(0)=='['){        		
					a = messageBundle.getString("message.arrayinstantiated");
					b = messageBundle.getString("message.arrayinstan_explanation");}
				explanationevent.explanationDisplay(a,b);
			}
		}
	}

	//When the object of the class or array is deleted,message dialog will be shown.
	public void explainGarbage(String name){
		if(show){
			setObject(name);
			String a = getObject(false);
			String b = messageBundle.getString("message.objectdelete_explanation");
			// Symbol '[' means array.If the array is deleted,assign different arguments. 
			if(name.charAt(0)=='['){
				a = messageBundle.getString("message.arraydeleted");
				b = messageBundle.getString("message.arraydeleted_explanation");}
			explanationevent.explanationDisplay(a,b);
		}
	}

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


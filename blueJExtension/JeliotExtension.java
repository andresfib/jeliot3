import bluej.extensions.*;
import bluej.extensions.event.*;
import jeliot.*;

import javax.swing.*;
import java.net.URL;
import java.awt.event.*;
import java.io.*;
//import java.lang.String;
import java.util.Hashtable;




/**
 * 
 * @author apineau
 *
 * Jeliot extension for BlueJ
 * 
 * 
 * Main Class
 */
public class JeliotExtension extends Extension implements PackageListener{
	
	
	/**
     * When this method is called, the extension may start its work.
     */
    public void startup (BlueJ bluej) {
        bluej.setMenuGenerator(new MenuBuilder(bluej));
        bluej.addPackageListener(this);
    }
      
    /**
     * The package has been opened. Print the name of the project it is part of.
     */
    public void packageOpened ( PackageEvent ev ) {
        try {
            System.out.println ("Project " + ev.getPackage().getProject().getName() + " opened.");
        } catch (ExtensionException e) {
            System.out.println("Project closed by BlueJ");
        }
    }  
  
    /**
     * The package is closing.
     */
    public void packageClosing ( PackageEvent ev ) {
    }  
    
    /**
     * This method must decide if the Jeliot Extension is compatible with the 
     * current release of the BlueJ Extensions API
     */
    public boolean isCompatible () { 
        return true; 
    }

    /**
     * version number of the extension
     */
    public String  getVersion () { 
        	return ("3.5.0"); 
    }

    /**
     * Returns the user-visible name of this extension
     */
    public String  getName () { 
        return ("Jeliot Extension");  
    }

    public void terminate() {
        System.out.println ("Jeliot extension terminates");
    }
    
    public String getDescription () {
        return ("Jeliot 3 is a Program Visualization application. You can load the active project into Jeliot and animate it!!");
    }

    /**
     * Returns a URL where you can find info on this extension.
     * The real problem is making sure that the link will still be alive in three years...
     */
    public URL getURL () {
        try {
        return new URL("http://cs.joensuu.fi/jeliot/");
        } catch ( Exception eee ) {
            // There is no reason at all that this should trow exception...
            System.out.println ("Simple extension: getURL: Exception="+eee.getMessage());
            return null;
        }
    }
}

/* This class binds different menus to different parts of BlueJ
 * It is important to remember the rules you have to follow.
 * - getToolsMenuItem can be called at any time.
 * - It must generate a new JMenuItem every time.
 * - No reference to the JMenuItem should be stored in the extension.
 * - You must be quick in generating your menu.
 */
class MenuBuilder extends MenuGenerator {
    private ToolsAction aToolsAction;//object that instantiates the jeliot window
    private BPackage curPackage;//wrapper of the current blueJ package
    private BClass curClass;//wrapper of the current bluej class
    private BObject curObject;//wrapper of the current bluej object
    private BlueJ bluej;//proxy object that provides services to blueJ extension


    /**
     *Constructor
     */
    MenuBuilder(BlueJ bluej) {
    	this.bluej = bluej;
        aToolsAction = new ToolsAction("Run Jeliot", bluej);
    }

    public JMenuItem getToolsMenuItem(BPackage aPackage) {
        return new JMenuItem(aToolsAction);
    }

    /** 
     * A utility method which prints the objects involved in the current
     * menu invocation.
     */
    private void printCurrentStatus(String header) {
        try {
            if (curObject != null)
                curClass = curObject.getBClass();
            if (curClass != null)
                curPackage = curClass.getPackage();
                
            System.out.println(header);
            if (curPackage != null)
                System.out.println("  Current Package=" + curPackage);
            if (curClass != null)
                System.out.println("  Current Class=" + curClass);
            if (curObject != null)
                System.out.println("  Current Object=" + curObject);
        } catch (Exception exc) { 
        	System.out.println("JeliotExtension:PrintCurrentStatusError");
			}
    }

    
    /**
     * Value associated to a given key of the hashtable
     * 
     */
    class Value {
    	private String className;//name of the class object
    	private String[] parameters;//parameters used by the construtor 
    	
    	/**
    	 * 
    	 * Constructor
    	 * 
    	 * @param className name of the class object
    	 * @param parameters parameters of its constructor
    	 */
    	public Value(String className, String[] parameters){
    		this.className = className;
    		this.parameters = parameters;
    	}
    	
    	public String[] getParameters(){
    		return parameters;
    	}
    	
    	
    	public void setParameters(String[] newParameters){
    		parameters = newParameters;
    	}
    	
    	public String getClassName(){
    		return className;
    	}
    	
    	
    	/**
    	 * returns a string containing all parameters and puting a coma between each parameter
    	 * 
    	 * @return 
    	 */
    	public String printParameters(){
    		String param = new String("");
    		if (parameters == null) {
    			return param;
    		}
    		if (parameters.length==1){ 
    			return parameters[0];
    		}
    		else {  	
    			for(int i=0; i<=parameters.length - 2; i++) {
    				param += "," + parameters[i];
    			}
    			param += parameters[parameters.length - 1];
    		}
    		return param;
    	}
    }
    
    
    /**
     * The nested classe that instantiates the Jeliot window.
     */
    class ToolsAction extends AbstractAction implements CompileListener, bluej.extensions.event.InvocationListener{
    	private BlueJ bluej;//proxy object
    	private Jeliot jeliot;//our jeliot application
    	private boolean launched;//tells if jeliot has been launched or not
    	private Hashtable hash;//hash table in which we put object instances created with BlueJ
    	
    	/**
    	 * Constructor, adds a "menuname" entry to the tool menu
    	 */
    	public ToolsAction(String menuName, BlueJ bluej) {
    		this.bluej = bluej;
    		putValue(AbstractAction.NAME, menuName);
    		this.bluej.addCompileListener(this);
    		this.bluej.addCompileListener(this);
    		this.bluej.addInvocationListener(this);
    		hash = new Hashtable();
    	}
    	
    	/**
    	 *action performed on click on the menu
    	 *
    	 */
    	public void actionPerformed(ActionEvent anEvent) {
    		try {
    			
    			//we start jeliot
    			jeliot = Jeliot.start(new String[] {generateJeliotString(), "true"});
    			launched = true;
    			while (jeliot == null) { try{ Thread.sleep(10); } catch (Exception e) { } }
    			jeliot.getGUI().getFrame().addWindowListener(new WindowAdapter() { public void windowClosing(WindowEvent e) {launched = false;} });
    			System.out.println("listener attached\n");
    			printCurrentStatus("Jeliot launched");//print in the debug file
    		} catch (Exception e) { 
    			printCurrentStatus("error while launching jeliot");
    			e.printStackTrace(System.out);			
    		}
    	}
    	
    	/**
    	 * Create a string which contains the source code for jeliot
    	 */
    	public String generateJeliotString() {
    		//String projectName = new String();
    		String source = new String();//source code that will be used by jeliot
    		String path = new String();
    		BPackage currentPackage;
    		BClass currentClass;
    		BPackage[] packages;
    		BClass[] classes;
    		int sizePackages;
    		int sizeClasses;
    		int i = 0;
    		int j = 0;
    		File inputFile;
    		File outputFile;  
    		  		
    		//we copy the content of each java file of the project  in the source string
    		//using a bufferedReader
    		
    		BPackage bpackage = bluej.getCurrentPackage();
    		if (bpackage != null) { // there may be no package open at all
    			try {
    				
    				path = bpackage.getDir().getPath();
    				
    				//print the path in the debug file
    				printCurrentStatus(path);
    		
    				packages = bpackage.getProject().getPackages();
    				sizePackages = packages.length;
    				while (i < sizePackages) {//for each package in the project 
    					try {
    						currentPackage = packages[i];
    						classes = currentPackage.getClasses();
    						sizeClasses = classes.length;
    						
    						//for all classes in each package
    						while (j < sizeClasses) {
    							
    							//print the path in the debug file	
    							printCurrentStatus(classes[j].getJavaFile().getPath());
    							/*
    							try {
    								if (classes[j].getSuperclass()!= null)
    									printCurrentStatus("class \n");
    								else
    									printCurrentStatus("abstract class or interface \n");
    							}
    			
    							catch( ProjectNotOpenException p) {System.out.println("project error");}
    							catch( ClassNotFoundException e) {System.out.println("class error");}
    							*/
    							//create a bufferdReader 
    							BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(classes[j].getJavaFile().getPath())));
    							String line = new String();
    							
    							//we put each line in the source string
    							while  ((line = in.readLine()) != null) { 
    								//printCurrentStatus(line);
    								source += "\n" + line; 
    								}
    							// and we go to the next file
    							j++;
    						}
    						//we check the next package
    						i++;
    						
    					}catch (PackageNotFoundException pnfe) {
    						printCurrentStatus("jeliot extension error2: no name for the project");
    					}    				
    				}  				     			
    			} catch(ProjectNotOpenException e) {
    				printCurrentStatus("jeliot extension error3: The project has been unexpectedly closed!");
    			} catch (IOException e){
    				printCurrentStatus("jeliot extension error4: error while creating jeliot.txt file");	
    			} catch (PackageNotFoundException e){
    				printCurrentStatus("jeliot extension error5: package not found");
    			}
    		}
    		return source;
    	}	
    	
    	// and the methods which will be called in the main class when
    	// each of the different menus are about to be invoked.
    	public void notifyPostToolsMenu(BPackage bp, JMenuItem jmi) {
    		System.out.println("Post on Tools menu");
    		curPackage = bp ; 
    		curClass = null ; 
    		curObject = null;
    	}
    	
    	/**
         * 
         * Events that occured on compiling with BlueJ
         */
    	public void compileError(CompileEvent event){}
    	
    	public void compileFailed(CompileEvent event){}	
    	
    	//When a compilation starts, we empty the hashtable
    	public void compileStarted(CompileEvent event){
    		//System.out.println("Compilation starts and received by jeliot extension\n");
    		hash.clear();
    		
    	}
    	
    	public void compileSucceeded(CompileEvent event){	
    		//we change the source code inside jeliot
    		jeliot.setProgram(generateJeliotString());
    		System.out.println("files compiled, jeliot extension ok\n");
    	}
    	
    	public void compileWarning(CompileEvent event){}
    	
    	
    	/**
    	 * 
    	 * Invocation on a blueJ object
    	 */	
    	public void invocationFinished(bluej.extensions.event.InvocationEvent event){
    		
    		System.out.println("event: " + event.toString() + "\n");
    		System.out.println("class name: " + event.getClassName() + "\n");
    		System.out.println("object name: " + event.getObjectName() + "\n");
    		System.out.println("method name: " + event.getMethodName() + "\n");
    		
    		
    		//we print the parameters in the debugfile
    		//if it´s a constructor with parameters
    		if (event.getClassName()!=null && event.getParameters() != null){
    			System.out.println("new object with parameters:" +event.getParameters()[0]+"\n");
    			
    			for(int i=0; i<event.getParameters().length;i++) {
    				System.out.println("constructor parameter[" + i + "]" + event.getParameters()[i] + "\n");
    			}
    		}
    		if (event.getParameters() == null) System.out.println("new object without parameter\n");
    		
    		//if it´s a constructor, we add it in the hashtable 
    		if (event.getClassName()!=null && event.getMethodName()==null){
    			hash.put(event.getObjectName(), new Value(event.getClassName(), event.getParameters()));
    			System.out.println("object " + event.getObjectName() + " inserted\n");
    			
    		}
    		
    		//we call the main method of an object
    		if (event.getMethodName()=="main") {
    			String paramMethod = new String("");//string of the parameters
    			System.out.println("main method called\n");//print in the debug file
    			
    			//we get the parameters of the main method
    			//all paremeters are contained in one string: event.getParameters()
    			
    			//we launch the animation
    			jeliot.recompile(generateJeliotString(), event.getClassName()+".main(new String[]"+event.getParameters()[0]+");");		
    		}
    		
    		//it´s a method called on an object
    		if (event.getMethodName()!=null && event.getMethodName()!="main"){//we call a method of an existing object
    			String paramMethod = new String("");//string of the parameters
    			
    			//we get the object from the hashtable
    			Object obj = hash.get((Object)event.getObjectName());
    			Value val = (Value)obj;
    			System.out.println("call of method\n");
   
    			//we get the parameters of the method called
    			//we check if there is at least one parameter, it returns null otherwise
    			if (event.getParameters()!=null){
    				
    				
    				//we write the parameters in a string, all parameters are separated by coma
    				
    				//1 parameter	
    				if (event.getParameters().length==1){ 
    					paramMethod = event.getParameters()[0];
    				}
    				//several parameters
    				else {  	
    					for(int i=0; i<=event.getParameters().length - 2; i++) {
    						paramMethod += event.getParameters()[i] + ",";
    					}
    					paramMethod += event.getParameters()[event.getParameters().length - 1];
    				}
    			}  
    			
    			//trace in the debug file
    			System.out.println("(new "+val.getClassName()+"(" +val.printParameters()+"))."+event.getMethodName()+"("+paramMethod+")");
    			
    			//we check if jeliot is already launched or not
    			//if yes, we launch the animation, otherwise we do nothing
    			if (launched) {
    			jeliot.compile("(new "+val.getClassName()+"(" +val.printParameters()+"))."+event.getMethodName()+"("+paramMethod+");");
    			System.out.println("jeliot opened\n");
    			}
    			else {System.out.println("jeliot closed\n");}
    		}
    	}
    		
    }	
}
import bluej.extensions.*;
import bluej.extensions.event.*;
import jeliot.*;

import javax.swing.*;
import java.net.URL;
import java.awt.event.*;
import java.io.*;
import java.lang.String;




/**
 * 
 * @author apineau
 *
 * Jeliot extension for BlueJ
 * 
 * 
 * Main Class
 */
public class JeliotExtension extends Extension implements PackageListener {
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
        return ("version 1.0");  
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
        return ("A Jeliot extension inside your BlueJ program");
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
        aToolsAction = new ToolsAction("Use Jeliot", bluej);
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
     * The nested classe that instantiates the Jeliot window.
     */
    class ToolsAction extends AbstractAction {
    	private BlueJ bluej;//proxy object
    	private String source;
    	
    	/**
    	 * Constructor, adds a "menuname" entry to the tool menu
    	 */
    	public ToolsAction(String menuName, BlueJ bluej) {
    		this.bluej = bluej;
    		putValue(AbstractAction.NAME, menuName);
    	}
    	
    	/**
    	 *action performed on click on the menu
    	 *
    	 */
    	public void actionPerformed(ActionEvent anEvent) {
    		try {
    			Jeliot.start(new String[] {generateJeliotString(), "true"});//
    			printCurrentStatus("Jeliot launched");//print in the debug file
    		} catch (Exception e) { 
    			printCurrentStatus("error while launching jeliot");
    		}
    	}
    	
    	/**
    	 * get the name of the current project
    	 */
    	public String generateJeliotString() {
    		//String projectName = new String();
    		source = new String();
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
    				/*
    				//we create the jeliot file in which we will put all the classes
    				outputFile = new File(path + "\\jeliot.txt");
    				
    				//we delete the file if it already exists
    				if (outputFile.exists()) outputFile.delete();
    				*/
    				
    				//projectName = bpackage.getProject().getName();
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
    							  
    							//create a bufferdReader 
    							BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(classes[j].getJavaFile().getPath())));
    							String line = new String();
    							
    							//we put each line in the source string
    							while  ((line = in.readLine()) != null) { 
    								printCurrentStatus(line);
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
    }
}
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Hashtable;

import javax.swing.AbstractAction;
import javax.swing.JMenuItem;

import jeliot.Jeliot;
import bluej.extensions.BClass;
import bluej.extensions.BObject;
import bluej.extensions.BPackage;
import bluej.extensions.BlueJ;
import bluej.extensions.ClassNotFoundException;
import bluej.extensions.MenuGenerator;
import bluej.extensions.PackageNotFoundException;
import bluej.extensions.ProjectNotOpenException;
import bluej.extensions.event.CompileEvent;
import bluej.extensions.event.CompileListener;
import bluej.extensions.event.InvocationEvent;
import bluej.extensions.event.InvocationListener;

/** 
 * This class binds different menus to different parts of BlueJ
 * It is important to remember the rules you have to follow.
 * - getToolsMenuItem can be called at any time.
 * - It must generate a new JMenuItem every time.
 * - No reference to the JMenuItem should be stored in the extension.
 * - You must be quick in generating your menu.
 */
public class MenuBuilder extends MenuGenerator {

    /**
     * object that instantiates the jeliot window
     */
    private ToolsAction aToolsAction;

    /**
     * MenuItem that is used to run Jeliot.
     */
    private JMenuItem aMenuItem;

    /**
     * wrapper of the current blueJ package
     */
    private BPackage curPackage;

    /**
     * wrapper of the current bluej class
     */
    private BClass curClass;

    /**
     * wrapper of the current bluej object
     */
    private BObject curObject;

    /**
     * proxy object that provides services to blueJ extension
     */
    private BlueJ bluej;

    /**
     * Constructor
     */
    MenuBuilder(BlueJ bluej) {
        this.bluej = bluej;
        aToolsAction = new ToolsAction("Run Jeliot", bluej);
    }

    public JMenuItem getToolsMenuItem(BPackage aPackage) {
        aMenuItem = new JMenuItem(aToolsAction);
        return aMenuItem;
    }

    /** 
     * A utility method which prints the objects involved in the current
     * menu invocation.
     */
    private void printCurrentStatus(String header) {
        try {
            if (curObject != null) {
                curClass = curObject.getBClass();
            }

            if (curClass != null) {
                curPackage = curClass.getPackage();
            }
            System.out.println(header);
            if (curPackage != null) {
                System.out.println("  Current Package=" + curPackage);
            }
            if (curClass != null) {
                System.out.println("  Current Class=" + curClass);
            }
            if (curObject != null) {
                System.out.println("  Current Object=" + curObject);
            }
        } catch (Exception exc) {
            System.out.println("JeliotExtension: PrintCurrentStatusError");
        }
    }

    /**
     * Value associated to a given key of the hashtable
     * 
     */
    class Value {

        /**
         * name of the class object
         */
        private String className;

        /**
         * parameters used by the construtor
         */
        private String[] parameters;

        /**
         * 
         * Constructor
         * 
         * @param className name of the class object
         * @param parameters parameters of its constructor
         */
        public Value(String className, String[] parameters) {
            this.className = className;
            this.parameters = parameters;
        }

        public String[] getParameters() {
            return parameters;
        }

        public void setParameters(String[] newParameters) {
            parameters = newParameters;
        }

        public String getClassName() {
            return className;
        }

        /**
         * returns a string containing all parameters and puting a coma between each parameter
         * 
         * @return 
         */
        public String printParameters() {
            String param = new String("");
            if (parameters == null) {
                return param;
            }
            if (parameters.length == 1) {
                return parameters[0];
            }
            if (parameters.length == 2) {
                return parameters[0] + ", " + parameters[1];
            }

            else {
                param += parameters[0];
                for (int i = 1; i <= parameters.length - 1; i++) {
                    param += "," + parameters[i];
                }
            }
            return param;
        }
    }

    /**
     * The nested classe that instantiates the Jeliot window.
     */
    class ToolsAction extends AbstractAction implements CompileListener, InvocationListener {

        /**
         * proxy object
         */
        private BlueJ bluej;

        /**
         * our jeliot application
         */
        private Jeliot jeliot;

        /**
         * tells if jeliot has been launched or not
         */
        private boolean launched = false;

        /**
         * hash table in which we put object instances created with BlueJ
         */
        private Hashtable hash;

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
                if (!launched) {

                    //we start jeliot
                    jeliot = Jeliot.start(new String[] { generateJeliotString(), "true"});
                    launched = true;
                    jeliot.getGUI().getFrame().addWindowListener(new WindowAdapter() {

                        public void windowClosing(WindowEvent e) {
                            launched = false;
                        }

                    });
                    System.out.println("listener attached\n");
                    printCurrentStatus("Jeliot launched");//print in the debug file
                }
            } catch (Exception e) {
                printCurrentStatus("error while launching jeliot");
                e.printStackTrace(System.out);
            }
        }

        /**
         * Create a string which contains the source code for jeliot
         */
        public String generateJeliotString() {

            String source = "";

            //we copy the content of each java file of the project  in the source string
            //using a bufferedReader

            BPackage bpackage = bluej.getCurrentPackage();
            if (bpackage != null) { // there may be no package open at all
                try {

                    String path = bpackage.getDir().getPath();

                    //print the path in the debug file
                    printCurrentStatus(path);

                    BPackage[] packages = bpackage.getProject().getPackages();
                    int sizePackages = packages.length;
                    for (int i = 0; i < sizePackages; i++) {//for each package in the project 
                        BPackage currentPackage = packages[i];
                        BClass[] classes = currentPackage.getClasses();
                        int sizeClasses = classes.length;

                        //for all classes in each package
                        for (int j = 0; j < sizeClasses; j++) {

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
                            String className = "";
                            try {
                                className = classes[j].getJavaClass().getName();
                            } catch (ClassNotFoundException e) {
                                className = classes[j].getJavaFile().getName().substring(0,
                                        classes[j].getJavaFile().getName().length() - 5);
                            }
                            
                            if (!className.equals("Lue")) {
                                BufferedReader in = new BufferedReader(new InputStreamReader(
                                        new FileInputStream(classes[j].getJavaFile().getPath())));

                                String line = null;

                                //we put each line in the source string
                                while ((line = in.readLine()) != null) {
                                    //printCurrentStatus(line);
                                    source += "\n" + line;
                                }
                            }

                        }// and we go to the next file
                    }//we check the next package
                } catch (ProjectNotOpenException e) {
                    printCurrentStatus("jeliot extension: The project has been unexpectedly closed!");
                } catch (IOException e) {
                    printCurrentStatus("jeliot extension: error while creating jeliot.txt file");
                } catch (PackageNotFoundException e) {
                    printCurrentStatus("jeliot extension: package not found");
                }
            }
            return source;
        }

        // and the methods which will be called in the main class when
        // each of the different menus are about to be invoked.
        public void notifyPostToolsMenu(BPackage bp, JMenuItem jmi) {
            System.out.println("Post on Tools menu");
            curPackage = bp;
            curClass = null;
            curObject = null;
        }

        /**
         * 
         * Events that occured on compiling with BlueJ
         */
        public void compileError(CompileEvent event) {}

        public void compileFailed(CompileEvent event) {}

        //When a compilation starts, we empty the hashtable
        public void compileStarted(CompileEvent event) {
            //System.out.println("Compilation starts and received by jeliot extension\n");
            hash.clear();
        }

        public void compileSucceeded(CompileEvent event) {
            //we change the source code inside jeliot
            jeliot.setProgram(generateJeliotString());
            System.out.println("files compiled, jeliot extension ok\n");
        }

        public void compileWarning(CompileEvent event) {}

        /**
         * 
         * Invocation on a blueJ object
         */
        public void invocationFinished(InvocationEvent event) {

            System.out.println("event: " + event.toString() + "\n");
            System.out.println("class name: " + event.getClassName() + "\n");
            System.out.println("object name: " + event.getObjectName() + "\n");
            System.out.println("method name: " + event.getMethodName() + "\n");

            //we print the parameters in the debugfile
            //if it´s a constructor with parameters
            if (event.getClassName() != null && event.getParameters() != null) {
                System.out.println("new object with parameters:" + event.getParameters()[0] + "\n");

                for (int i = 0; i < event.getParameters().length; i++) {
                    System.out.println("constructor parameter[" + i + "]"
                            + event.getParameters()[i] + "\n");
                }
            }
            if (event.getParameters() == null)
                System.out.println("new object without parameter\n");

            //if it´s a constructor, we add it in the hashtable 
            if (event.getClassName() != null && event.getMethodName() == null) {
                hash.put(event.getObjectName(), new Value(event.getClassName(), event
                        .getParameters()));
                System.out.println("object " + event.getObjectName() + " inserted\n");

            }

            //we call the main method of an object
            if (event.getMethodName() == "main") {
                String paramMethod = new String("");//string of the parameters
                System.out.println("main method called\n");//print in the debug file

                //we get the parameters of the main method
                //all paremeters are contained in one string: event.getParameters()

                //we launch the animation
                jeliot.recompile(generateJeliotString(), event.getClassName()
                        + ".main(new String[]" + event.getParameters()[0] + ");");
            }

            //it´s a method called on an object
            if (event.getMethodName() != null && event.getMethodName() != "main") {//we call a method of an existing object
                String paramMethod = new String("");//string of the parameters

                //we get the object from the hashtable
                Object obj = hash.get((Object) event.getObjectName());
                Value val = (Value) obj;
                System.out.println("call of method\n");

                //we get the parameters of the method called
                //we check if there is at least one parameter, it returns null otherwise
                if (event.getParameters() != null) {

                    //we write the parameters in a string, all parameters are separated by coma

                    //1 parameter   
                    if (event.getParameters().length == 1) {
                        paramMethod = event.getParameters()[0];
                    }
                    //several parameters
                    else {
                        for (int i = 0; i <= event.getParameters().length - 2; i++) {
                            paramMethod += event.getParameters()[i] + ",";
                        }
                        paramMethod += event.getParameters()[event.getParameters().length - 1];
                    }
                }

                //trace in the debug file
                System.out.println("(new " + val.getClassName() + "(" + val.printParameters()
                        + "))." + event.getMethodName() + "(" + paramMethod + ")");

                //we check if jeliot is already launched or not
                //if yes, we launch the animation, otherwise we do nothing
                if (launched) {
                    jeliot.compile("(new " + val.getClassName() + "(" + val.printParameters()
                            + "))." + event.getMethodName() + "(" + paramMethod + ");");
                    System.out.println("jeliot opened\n");
                } else {
                    System.out.println("jeliot closed\n");
                }
            }
        }

    }
}
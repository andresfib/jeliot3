/* Jeliot 3.4.1 */

/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version. This program is distributed in
 * the hope that it will be useful but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more
 * details. You should have received a copy of the GNU General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston,
 * MA 02111-1307, USA.
 */

package jeliot;

import java.awt.Image;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import jeliot.calltree.TreeDraw;
import jeliot.gui.CodePane2;
import jeliot.gui.HistoryView;
import jeliot.gui.JeliotWindow;
import jeliot.gui.LoadJeliot;
import jeliot.launcher.Launcher;
import jeliot.mcode.CallTreeMCodeInterpreter;
import jeliot.mcode.Highlight;
import jeliot.mcode.InterpreterError;
import jeliot.mcode.MCodeInterpreter;
import jeliot.mcode.MCodeUtilities;
import jeliot.mcode.TheaterMCodeInterpreter;
import jeliot.theater.ActorFactory;
import jeliot.theater.AnimationEngine;
import jeliot.theater.Director;
import jeliot.theater.ImageLoader;
import jeliot.theater.Theater;
import jeliot.theater.ThreadController;
import jeliot.tracker.Tracker;

/**
 * This is the application class of Jeliot 3 that binds
 * together the theatre, the GUI and the DynamicJava Java source
 * interpreter.
 *
 * @author Pekka Uronen
 * @author Niko Myller
 */
public class Jeliot {

    //  DOC: Document!
    
    /**
     * The resource bundle for gui package
     */
    static private ResourceBundle bundle = ResourceBundle.getBundle(
            "jeliot.gui.resources.properties", Locale.getDefault());
    /**
     * 
     */
    private Pattern p = Pattern.compile("import\\s+jeliot.io.*\\s*;");
    /**
     * 
     */
    private static boolean noSystemExit = false;

    /**
     *
     */
    Launcher launcher = null;

    /**
     *
     */
    BufferedReader ecode = null;

    /**
     *
     */
    PrintWriter pr = null;

    /**
     *
     */
    MCodeInterpreter mCodeInterpreterForTheater = null;

    MCodeInterpreter mCodeInterpreterForCallTree = null;

    /**
     *
     */
    String sourceCode = "";

    /**
     *
     */
    String methodCall = "";

    /**
     *
     */
    boolean compiled = false;

    /**
     * The graphical user inteface.
     */
    protected JeliotWindow gui;

    /**
     * Theatre object for showing the animation.
     */
    private Theater theatre = new Theater();

    private TreeDraw callTree = new TreeDraw();

    /**
     * A thread controller object for handling concurrency and
     * starting and pausing the animation.
     */
    private ThreadController controller;

    /**
     * Animation engine to show the animations.
     */
    private AnimationEngine engine = new AnimationEngine(theatre);

    /**
     * A code pane for showing and highlighting the program source
     * while the program is being animated.
     */
    private CodePane2 codePane = new CodePane2();

    private HistoryView hv = new HistoryView(codePane);
    
    /**
     * A director for animating the program.
     */
    private Director director;

    /**
     * An image loader that takes care of loading the required
     * images.
     */
    private ImageLoader iLoad = new ImageLoader();

    private boolean experiment = false;
    
    /**
     * The only constructor of the Jeliot 3.
     * Loads Theatre theatre -object's background.
     * Initializes JeliotWindow gui -object with parameters this, CodePane codepane, Theatre theatre,
     * AnimationEngine engine, ImageLoader iLoad
     * 
     * @param udir
     * 
     */
    public Jeliot(String udir, boolean experiment) {
        this.experiment = experiment;
        theatre.setBackground(iLoad.getLogicalImage("image.panel"));
        
        //Just to track the animation happenings
        Tracker.setTheater(theatre);
        Tracker.setCodePane2(codePane);
        
        gui = new JeliotWindow(this, codePane, theatre, engine, iLoad, udir, callTree, hv);
    }

    /**
     * @return
     */
    public static boolean isnoSystemExit() {
        return noSystemExit;
    }

    /**
     * Sets up the user interface.
     */
    public void run() {
        gui.setUp();
    }

    /**
     * 
     *
     * @param sourceCode The program source code as a String.
     * @param methodCall The main method call as a String.
     */
    public void setSourceCode(String sourceCode, String methodCall) {

        if (p.matcher(sourceCode).find()) {
            this.sourceCode = sourceCode;
        } else {
            this.sourceCode = "import jeliot.io.*;\n\n" + sourceCode;
            gui.getCodePane().getTextArea().setText(this.sourceCode);
        }                
                
        this.methodCall = methodCall;

        compiled = false;

        //recompile();

        /*
         this.ecode = null;
         
         if (launcher != null) {
         launcher.stopThread();
         synchronized(launcher){
         launcher.notify();
         }
         launcher = null;
         }
         
         launcher = new Launcher(new BufferedReader(
         new StringReader(this.sourceCode)));
         
         launcher.setCompiling(true);
         launcher.start();
         launcher.setMethodCall(this.methodCall);
         try {
         Thread.sleep(500);
         } catch (InterruptedException e) {
         e.printStackTrace();
         }
         
         launcher.setCompiling(false);
         synchronized(launcher){
         launcher.notify();
         }
         
         ecode = launcher.getReader();
         pr = launcher.getInputWriter();
         
         
         compiled = true;
         */
    }

    /**
     * 
     */
    public void compile() {

        if (!compiled) {

            this.ecode = null;

            if (launcher != null) {
                launcher.stopThread();
                synchronized (launcher) {
                    launcher.notify();
                }
                launcher = null;
            }

            launcher = new Launcher(new BufferedReader(new StringReader(this.sourceCode)));
            launcher.setMethodCall(this.methodCall);

            launcher.setCompiling(true);
            //            launcher.setExecuting(true);

            launcher.start();

            //             synchronized(launcher){
            //                 launcher.notify();
            //             }

            ecode = launcher.getReader();
            pr = launcher.getInputWriter();
            MCodeUtilities.clearRegisteredSecondaryMCodeConnections();

            codePane.installProgram(this.sourceCode);

            compiled = true;
        }

    }

    /**
     * Initializes the compiled program to be animated.
     */
    public void rewind() {

        compiled = false;

        //clear the remnants of previous animation
        theatre.cleanUp();
        callTree.initialize();
        hv.initialize();

        //create director and the other equipment
        ActorFactory af = new ActorFactory(iLoad);

        //ScriptWriter sw = new ScriptWriter(engine, theatre, af);

        director = new Director(theatre, codePane, this, engine);
        director.setActorFactory(af);

        mCodeInterpreterForTheater = new TheaterMCodeInterpreter(ecode, director, gui.getProgram(), pr);
        director.setInterpreter(mCodeInterpreterForTheater);

        try {
            PipedReader pr = new PipedReader();
            PipedWriter pw = new PipedWriter(pr);
            MCodeUtilities.addRegisteredSecondaryMCodeConnections(new PrintWriter(pw));
            mCodeInterpreterForCallTree = new CallTreeMCodeInterpreter(new BufferedReader(pr), callTree, gui.getProgram(), this, gui.getTabNumber(bundle.getString("tab.title.call_tree")));
        } catch (Exception e) {
            e.printStackTrace();
        }

        //find the main method
        //Enumeration enum = program.getClasses();
        //PClass c = (PClass)enum.nextElement();
        //PMethod m = c.getMainMethod();
        //director.setMainMethod(m);

        // create the main loop for visualization
        controller = new ThreadController(new Runnable() {
            public void run() {
                try {
                    director.direct();
                    gui.animationFinished();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        
        Thread t = new Thread(new Runnable() {
            public void run() {
                try {
                    mCodeInterpreterForCallTree.execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
        
        engine.setController(controller);
        director.setController(controller);
    }

    /**
     * Starts the animation in step mode.
     * Called by gui. 
     */
    public void step() {
        director.setStep(true);
        controller.start();
    }

    /**
     * Starts the animation in play mode.
     * Called by gui. 
     */
    public void play() {
        director.setStep(false);
        controller.start();
    }

    /**
     * Pauses the animation.
     * Called by gui. 
     */
    public void pause() {
        controller.pause();
    }

    /**
     * Called by director when a step is completed.
     */
    public void directorPaused() {
        gui.pauseAnimation();
    }

    /**
     * Called by director when it freezes to accept input.
     */
    public void directorFreezed() {
        gui.freezeAnimation();
    }

    /**
     * Called by director when it resumes from waiting for input.
     */
    public void directorResumed() {
        gui.resumeAnimation();
    }

    /**
     * Called by the director when user's program outputs a string.
     *
     * @param str String that is outputted.
     */
    public void output(String str) {
        gui.output(str);
    }

    /**
     * @param e
     */
    public void showErrorMessage(InterpreterError e) {
        Tracker.writeToFile("Error", e.getMessage(), System.currentTimeMillis());
        gui.showErrorMessage(e);
    }

    /**
     * @return
     */
    public boolean showMessagesInDialogs() {
        return gui.showMessagesInDialogs();
    }

    /**
     * @param line
     */
    public void runUntil(int line) {
        director.runUntil(line);
    }

    /**
     * 
     */
    public void runUntilDone() {
        gui.runUntilDone();
    }

    /**
     * 
     * @param program
     */
    public void setProgram(String program) {
        gui.setProgram(program);
    }

    /**
     * 
     * @param f
     */
    public void setProgram(File f) {
        gui.setProgram(f);
    }
    
    public boolean isExperiment() {
        return experiment;
    }
    
    /**
     * 
     * @param highlight
     * @param tabNumber
     */
    public void highlightTabTitle(boolean highlight, int tabNumber) {
        gui.highlightTabTitle(highlight, tabNumber);
    }
    
    public void addImageToHistory(Image i, Highlight h) {
    	hv.addImage(i, h);
    }
    
    /**
     * @param args
     * @throws IOException
     */
    public static void main(String args[]) throws IOException {

        Properties prop = System.getProperties();
        String udir = prop.getProperty("user.dir");

        if (args.length >= 2) {
            Tracker.setTrack(Boolean.valueOf(args[1]).booleanValue());
        }
       
        boolean experiment = false;
        if (args.length >= 3) {
            experiment = Boolean.valueOf(args[2]).booleanValue();
        }
        
        //Just for tracking the user
        File f = new File(udir);
        Tracker.openFile(f);
        
        //f = new File(f, "examples");
        //prop.put("user.dir", f.toString());

        final Jeliot jeliot = new Jeliot(udir, experiment);

        (new LoadJeliot()).start(new Runnable() {
            public void run() {
                jeliot.run();
            }
        });
        
        if (args.length >=1) {
            File file = new File(udir);
            file = new File(file, "examples");
            final File file1 = new File(file, args[0]);
            if (file.exists()) {
                javax.swing.SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        jeliot.setProgram(file1);
                    }
                });
            }
        }
        
    }

    /**
     * 
     * @param args
     * @return
     * @throws IOException
     */
    public static Jeliot start(String args[]) throws IOException {

        Properties prop = System.getProperties();
        String udir = prop.getProperty("user.dir");
        
        if (args.length >= 2) {
            Jeliot.noSystemExit = Boolean.valueOf(args[1]).booleanValue();
        }
        
        //File f = new File(udir);
        //f = new File(f, "examples");
        //prop.put("user.dir", f.toString());

        boolean experiment = false;
        if (args.length >= 4) {
            experiment = Boolean.valueOf(args[3]).booleanValue();
        }
        
        final Jeliot jeliot = new Jeliot(udir, experiment);

        Runnable r = null;

        
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                jeliot.run();
            }
        });
        if (args.length >= 3) {
            File file = new File(udir);
            file = new File(file, "examples");
            final File file1 = new File(file, args[2]);
            if (file.exists()) {
                javax.swing.SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        jeliot.setProgram(file1);
                    }
                });
            }
        } else if (args.length >= 1) {
            if (!args[0].equals("")) {
                final String program = args[0];
                javax.swing.SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        jeliot.setProgram(program);
                    }
                });
            }
        }
        
        return jeliot;
    }

    public static void close() {
        Tracker.writeToFile("JeliotClose", System.currentTimeMillis());
        Tracker.closeFile();
    }
    
    public static String readFile(File f) {
        String str = "";
        
        try {
            BufferedReader br = new BufferedReader(new FileReader(f));
            String line;
                while ((line = br.readLine()) != null) {
                    str += line + "\n";
                }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e1) {
            e1.printStackTrace();
            return null;
        }
        return str;
    }
    
}

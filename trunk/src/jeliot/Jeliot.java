/* Jeliot 3.2 */

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

import java.io.BufferedReader;
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
import jeliot.gui.CodePane;
import jeliot.gui.JeliotWindow;
import jeliot.launcher.Launcher;
import jeliot.mcode.CallTreeMCodeInterpreter;
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
    private JeliotWindow gui;

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
    private CodePane codePane = new CodePane();

    /**
     * A director for animating the program.
     */
    private Director director;

    /**
     * An image loader that takes care of loading the required
     * images.
     */
    private ImageLoader iLoad = new ImageLoader();

    /**
     * The only constructor of the Jeliot 3.
     * Loads Theatre theatre -object's background.
     * Initializes JeliotWindow gui -object with parameters this, CodePane codepane, Theatre theatre,
     * AnimationEngine engine, ImageLoader iLoad
     * 
     * @param udir
     * 
     */
    public Jeliot(String udir) {
        theatre.setBackground(iLoad.getLogicalImage("image.panel"));
        gui = new JeliotWindow(this, codePane, theatre, engine, iLoad, udir, callTree);
    }

    /**
     * @return
     */
    public static boolean isSystemExit() {
        return !noSystemExit;
    }

    /**
     * Sets up the user interface.
     */
    public void run() {
        gui.setUp();
    }

    /**
     * Creates the Lexer and Java11Parser.
     * Compiles a program that is given in a Reader object.
     *
     * @param sourceCode The program source code as a String.
     * @param methodCall The main method call as a String.
     */
    public void setSourceCode(String sourceCode, String methodCall) {
        // create the lexer and the parser
        //Lex.Lexer l = new Lex.Lexer(r, false);
        //Java11Parser g = new Java11Parser(l);

        // parse the program

        // the try-catch structure is because of the braindead habit of
        // the lexer to report exceptions throwing ERRORS!

        //try {
        //Symbol symbol = g.parse();
        //this.program = (PCompilationUnit)symbol.value;
        //}
        //catch (Error error) {
        //String msg = error.getMessage();
        //throw new SyntaxErrorException(msg, 0, 0);
        //}

        // make a compile-time check
        //NameSpace space = new NameSpace(program);
        //TypeChecker check = new TypeChecker(space);
        //program.acceptVisitor(check);

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
     * @param highlight
     * @param tabNumber
     */
    public void highlightTabTitle(boolean highlight, int tabNumber) {
        gui.highlightTabTitle(highlight, tabNumber);
    }
    
    /**
     * @param args
     * @throws IOException
     */
    public static void main(String args[]) throws IOException {

        Properties prop = System.getProperties();
        String udir = prop.getProperty("user.dir");

        //File f = new File(udir);
        //f = new File(f, "examples");
        //prop.put("user.dir", f.toString());

        final Jeliot jeliot = new Jeliot(udir);

        javax.swing.SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                jeliot.run();
            }
        });
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

        //File f = new File(udir);
        //f = new File(f, "examples");
        //prop.put("user.dir", f.toString());

        final Jeliot jeliot = new Jeliot(udir);

        javax.swing.SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                jeliot.run();
            }
        });

        if (args.length >= 1) {
            if (args.length >= 2) {
                Jeliot.noSystemExit = Boolean.getBoolean(args[1]);
            }
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

}

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

/**
* This is the main package of the jeliot.
*/
package jeliot;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.Properties;

import jeliot.mcode.*;
import jeliot.gui.CodePane;
import jeliot.gui.JeliotWindow;
import jeliot.launcher.Launcher;
import jeliot.theater.*;

//import Lex.*;
//import java_cup.runtime.*;
//import jeliot.parser.*;


/**
* This is the application class of Jeliot 3
* that binds together the theatre and GUI.
*
* @author Pekka Uronen
* @modified Niko Myller
*/
public class Jeliot {

    Launcher launcher = null;
    BufferedReader ecode = null;
    PrintWriter pr = null;
    Interpreter mCodeInterpreter = null; 
    String sourceCode = "";
    String methodCall = "";
    boolean compiled = false;

    /** The program that was last compiled. */
    //private PCompilationUnit program;

    /** The graphical user inteface. */
    private JeliotWindow gui;

    /** Theatre object for showing the animation. */
    private Theater theatre = new Theater();

    /** A thread controller object for handling concurrency and
      * starting and pausing the animation. */
    private ThreadController controller;

    /** Animation engine to show the animations. */
    private AnimationEngine engine = new AnimationEngine(theatre);

    /** A code pane for showing and highlighting the program source
      * while the program is being animated. */
    private CodePane codePane = new CodePane();

    /** A director for animating the program. */
    private Director director;

    /** An image loader that takes care of loading the required
      * images. */
    private ImageLoader iLoad = new ImageLoader();


    /**
    * The only constructor of the Jeliot 3.
    * Loads Theatre theatre -object's background.
    * Initializes JeliotWindow gui -object with parameters this, CodePane codepane, Theatre theatre,
    * AnimationEngine engine, ImageLoader iLoad */
    public Jeliot(String udir) {
        theatre.setBackground(iLoad.getLogicalImage("image.panel"));
        gui = new JeliotWindow(this, codePane, theatre, engine, iLoad, udir);
    }

    /** Sets up the user interface.
      */
    public void run() {
        gui.setUp();
    }

    //public void createLauncher(Reader r){
        //launcher= new Launcher(r);
    //}

    /**
    * Creates the Lexer and Java11Parser.
    * Compiles a program that is given in a Reader object.
    *
    * @param    reader  Gives the program to be compiled.
    * @throws   Exception   If the lexer throws an error the this Exception is throwed.
      */
    public void setSourceCode(String sourceCode, String methodCall) throws Exception {
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

        this.sourceCode = sourceCode;
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

    public void compile() {

        if (!compiled) {

            this.ecode = null;

            if (launcher != null) {
                launcher.stopThread();
                synchronized(launcher) {
                    launcher.notify();
                }
                launcher = null;
            }

            launcher = new Launcher(new BufferedReader(
                                   new StringReader(this.sourceCode)));
            launcher.setMethodCall(this.methodCall);

            launcher.setCompiling(true);
//            launcher.setExecuting(true);

            launcher.start();

//             synchronized(launcher){
//                 launcher.notify();
//             }

            ecode = launcher.getReader();
            pr = launcher.getInputWriter();

            codePane.installProgram(this.sourceCode);

            compiled = true;
        }

    }

    /** Initializes the compiled program to be animated.
      */
    public void rewind() {

        compiled = false;

        //clear the remnants of previous animation
        theatre.cleanUp();

        //create director and the other equipment
        ActorFactory af = new ActorFactory(iLoad);

        //ScriptWriter sw = new ScriptWriter(engine, theatre, af);

        director = new Director(theatre, codePane, this, engine);
        director.setActorFactory(af);
        
        mCodeInterpreter = new Interpreter(ecode, director, gui.getProgram(), pr);
        
        director.setInterpreter(mCodeInterpreter);

        //find the main method
        //Enumeration enum = program.getClasses();
        //PClass c = (PClass)enum.nextElement();
        //PMethod m = c.getMainMethod();
        //director.setMainMethod(m);

        // create the main loop for visualization
        controller = new ThreadController(
            new Runnable() {
                public void run() {
                    try {
                        director.direct();
                        gui.animationFinished();
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        );

        engine.setController(controller);
        director.setController(controller);
    }

    /** Called by gui. Starts the animation in step mode.
      */
    public void step() {
        director.setStep(true);
        controller.start();
    }

    /** Called by gui. Starts the animation in play mode.
      */
    public void play() {
        director.setStep(false);
        controller.start();
    }

    /** Called by gui. Pauses the animation.
     */
    public void pause() {
        controller.pause();
    }

    /** Called by director when a step is completed.
      */
    public void directorPaused() {
        gui.pauseAnimation();
    }

    /** Called by director when it freezes to accept input.
      */
    public void directorFreezed() {
        gui.freezeAnimation();
    }

    /** Called by director when it resumes from waiting for input.
      */
    public void directorResumed() {
        gui.resumeAnimation();
    }

    /** Called by the director when user's program outputs a string.
    *
    * @param    str String that is outputted.
      */
    public void output(String str) {
        gui.output(str);
    }

    public void showErrorMessage(InterpreterError e) {
        gui.showErrorMessage(e);
    }

    public boolean showMessagesInDialogs() {
        return gui.showMessagesInDialogs();
    }

    public void runUntil(int line) {
        director.runUntil(line);
    }

    public void runUntilDone() {
        gui.runUntilDone();
    }


    /** Called by gui to get a tree view of the program.
    *
    * @return   The treeview of the program. Used for the debugging.
    */
    //public ProgramView getTree() {
        //return new ProgramView(program);
    //}

    public static void main(String args[]) throws IOException {

        Properties prop = System.getProperties();
        String udir = prop.getProperty("user.dir");

        File f = new File(udir);
        f = new File(f, "examples");
        prop.put("user.dir", f.toString());

        final Jeliot jeliot = new Jeliot(udir);

        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
               jeliot.run();
            }
        });
    }

}

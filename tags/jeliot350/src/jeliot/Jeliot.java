/* Jeliot 3.4.2 */

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
import java.io.IOException;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

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
import jeliot.util.DebugUtil;

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
     * The resource bundle for gui package
     */
    static private ResourceBundle bundle2 = ResourceBundle.getBundle(
            "jeliot.gui.resources.messages", Locale.getDefault());

    /**
     * user home directory
     */
    private String udir = ".";

    /**
     * Default I/O package 
     */
    private String IOPackage;

    /**
     * 
     */
    private Pattern IOPackagePattern;

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
    BufferedReader ecodeReader = null;

    /**
     *
     */
    PrintWriter inputWriter = null;

    /**
     *
     */
    MCodeInterpreter mCodeInterpreterForTheater = null;

    /**
     * 
     */
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
    Thread callTreeThread;

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

    /**
     * 
     */
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

    /**
     * 
     */
    private HistoryView hv;

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
     * 
     */
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
    public Jeliot(String defaultIO) {
        setIOPackageName(defaultIO);
        
        //Set LookAndFeel
        try {
            //UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.MetalLookAndFeel");
        } catch (InstantiationException e) { //
        } catch (ClassNotFoundException e) { //
        } catch (UnsupportedLookAndFeelException e) { //
        } catch (IllegalAccessException e) { //
        }

        theatre.setBackground(iLoad.getLogicalImage("image.panel"));
        hv = new HistoryView(codePane, udir);
        
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
     * @param srcCode The program source code as a String.
     * @param methodCall The main method call as a String.
     */
    public void setSourceCode(String srcCode, String methodCall) {

        if (hasIOImport(srcCode)) {
            this.sourceCode = srcCode;
        } else {
            this.sourceCode = getImportIOStatement() + "\n\n" + srcCode;
            gui.getCodePane().getTextArea().setText(this.sourceCode);
        }

        this.methodCall = methodCall;
        codePane.installProgram(this.sourceCode);
        compiled = false;
    }

    /**
     * This method handles the compilation process and
     * it should not be called directly!
     */
    public void compile() {

        if (!compiled) {

            this.ecodeReader = null;

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

            launcher.start();

            ecodeReader = launcher.getReader();
            inputWriter = launcher.getInputWriter();
            MCodeUtilities.clearRegisteredSecondaryMCodeConnections();

            compiled = true;
        }

    }

    /**
     * Can be called outside Jeliot to make Jeliot compile the source code
     * and call the method that is given. If null is provided standard main method
     * is tried to be found and called. If it is not found, error message is shown.
     * 
     * @param methodCall
     */
    public void compile(String methodCall) {
        gui.tryToEnterAnimate(methodCall);
    }

    /**
     * Can be called outside Jeliot to make Jeliot compile the given source code
     * and call the method that is given. If null is provided standard main method
     * is tried to be found and called. If it is not found, error message is shown.
     * 
     * @param programCode Source code of the program
     * @param methodCall Method call that is made first in the program
     */
    public void recompile(String programCode, String methodCall) {
        setProgram(programCode);
        gui.tryToEnterAnimate(methodCall);
    }

    /**
     *
     */
    public boolean playAnimation() {
        if (gui.getPlayButton().isEnabled()) {
            gui.getPlayButton().doClick();
            return true;
        }
        return false;
    }

    public void cleanUp() {
        //clear the remnants of previous animation
        theatre.cleanUp();
        callTree.initialize();
        hv.initialize();
    }
    
    /**
     * Initializes the compiled program to be animated.
     */
    public void rewind() {

        compiled = false;

        //create director and the other equipment
        ActorFactory af = new ActorFactory(iLoad);

        //ScriptWriter sw = new ScriptWriter(engine, theatre, af);

        director = new Director(theatre, codePane, this, engine);
        director.setActorFactory(af);

        mCodeInterpreterForTheater = new TheaterMCodeInterpreter(ecodeReader, director, gui
                .getProgram(), inputWriter);
        director.setInterpreter(mCodeInterpreterForTheater);

        try {
            PipedReader pr = new PipedReader();
            PipedWriter pw = new PipedWriter(pr);
            MCodeUtilities.addRegisteredSecondaryMCodeConnections(new PrintWriter(pw));
            mCodeInterpreterForCallTree = new CallTreeMCodeInterpreter(new BufferedReader(pr),
                    callTree, gui.getProgram(), this, gui.getTabNumber(bundle2
                            .getString("tab.title.call_tree")));
        } catch (Exception e) {
            if (DebugUtil.DEBUGGING) {
                e.printStackTrace();
            }
        }

        // create the main loop for visualization
        controller = new ThreadController(new Runnable() {

            public void run() {
                try {
                    //This should be used when the thread killing works.
                    if (director.direct()) {
                        gui.animationFinished();
                    }

                    //director.direct();
                    //gui.animationFinished();
                } catch (Exception e) {
                    if (DebugUtil.DEBUGGING) {
                        e.printStackTrace();
                    }
                }
            }
        });

        controller.addPauseListener(gui);
        
        callTreeThread = new Thread(new Runnable() {

            public void run() {
                try {
                    mCodeInterpreterForCallTree.execute();
                } catch (Exception e) {
                    if (DebugUtil.DEBUGGING) {
                        e.printStackTrace();
                    }
                }
            }
        });
        callTreeThread.start();

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
        Tracker.writeToFile("Output", str, System.currentTimeMillis());
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

    /**
     * 
     * @return
     */
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

    /**
     * 
     * @param i
     * @param h
     */
    public void addImageToHistory(Image i, Highlight h) {
        hv.addImage(i, h);
    }

    /**
     * 
     * @return
     */
    public JeliotWindow getGUI() {
        return gui;
    }

    /**
     * 
     *
     */
    public void stopThreads() {
        //This kills the Animation and Call Tree threads.
        if (mCodeInterpreterForTheater != null) {
            mCodeInterpreterForTheater.setRunning(false);
        }
        if (mCodeInterpreterForCallTree != null) {
            mCodeInterpreterForCallTree.setRunning(false);
        }
        if (controller != null) {
            controller.quit();
        }

        //This should kill the Launcher thread
        if (launcher != null) {
            launcher.stopThread();
        }

        BufferedReader r = MCodeUtilities.getReader();
        MCodeUtilities.setReader(null);
        if (r != null) {
            synchronized (r) {
                r.notifyAll();
            }
            try {
                r.close();
            } catch (IOException e1) {}
        }
        r = null;
        if (ecodeReader != null) {
            synchronized (ecodeReader) {
                ecodeReader.notifyAll();
            }
            try {
                ecodeReader.close();
            } catch (IOException e) {
                if (DebugUtil.DEBUGGING) {
                    e.printStackTrace();
                }
            }
        }
        if (inputWriter != null) {
            synchronized (inputWriter) {
                inputWriter.notifyAll();
            }
            inputWriter.flush();
            inputWriter.close();
        }

        PrintWriter w = MCodeUtilities.getWriter();
        MCodeUtilities.setWriter(null);
        if (w != null) {
            synchronized (w) {
                w.notifyAll();
            }
            w.flush();
            w.close();
        }
        w = null;

        if (launcher != null) {
            synchronized (launcher) {
                launcher.notify();
            }
        }

        launcher = null;
        controller = null;
        callTreeThread = null;
        MCodeUtilities.clearRegisteredSecondaryMCodeConnections();
        inputWriter = null;
        ecodeReader = null;
    }

    /**
     * @param args is a String array that contains parameter values for Jeliot.
     * First cell contains a file name that is wanted to be loaded into Jeliot from examples directory.
     * Second cell contains a boolean value ("true" or "false") that tells if Jeliot tracker should be used.
     * Third cell contains a boolean value ("true" or "false") that tells that experimental settings should be loaded.
     */
    public void handleArgs(String args[]) {
        Properties prop = System.getProperties();
        udir = prop.getProperty("user.dir");

        if (args.length >= 2) {
            if (args[1] != null) {
                File f = new File(udir);
                Tracker.openFile(f);
                Tracker.setTrack(Boolean.valueOf(args[1]).booleanValue());
            }
        }

        experiment = false;
        if (args.length >= 3) {
            if (args[2] != null) {
                experiment = Boolean.valueOf(args[2]).booleanValue();
            }
        }

        if (args.length >= 1) {
            if (args[0] != null) {
                File file = new File(udir);
                file = new File(file, "examples");
                final File file1 = new File(file, args[0]);
                if (file.exists()) {
                    SwingUtilities.invokeLater(new Runnable() {

                        public void run() {
                            setProgram(file1);
                        }
                    });
                }
            }
        }
    }

    /**
     * @param args is a String array that contains parameter values for Jeliot.
     * First cell contains a file name that is wanted to be loaded into Jeliot from examples directory.
     * Second cell contains a String representation of a boolean value ("true" or "false") that tells if Jeliot tracker should be used.
     * Third cell contains a String representation of a boolean value ("true" or "false") that tells that experimental settings should be loaded.
     */
    public static void main(String args[]) {
        Jeliot jeliot = new Jeliot("jeliot.io.*");
        jeliot.handleArgs(args);
        LoadJeliot.start(jeliot);
    }

    /**
     * This is meant to be used outside Jeliot to launch an instance of Jeliot.
     * 
     * @param args is a String array that contains parameter values for Jeliot.
     * First cell is a program source code that should be loaded into Jeliot.
     * Second cell is a String representation of a boolean value ("true" or "false") that tells if a system exit call is not desired when Jeliot is closed.
     * Third cell is a file name that is wanted to be loaded into Jeliot from examples directory.
     * Fourth cell is is a String representation of a boolean value ("true" or "false") that experimental settings should be loaded.
     * Fifth cell is a String representation of a boolean value ("true" or "false") that tells if Jeliot tracker should be used.
     *
     * @return instance of Jeliot
     */
    public static Jeliot start(String args[]) {

        final Jeliot jeliot = new Jeliot("jeliot.io.*");

        //Do the mapping to other resources.
        String[] arguments = new String[3];

        //System exit or frame disposing
        if (args.length >= 2) {
            noSystemExit = Boolean.valueOf(args[1]).booleanValue();
        }

        //File name is set
        if (args.length >= 3) {
            arguments[0] = args[2];
        }

        //Should experimental settings be used
        if (args.length >= 4) {
            arguments[2] = args[3];
        }

        //If tracker should be used or not
        if (args.length >= 5) {
            arguments[1] = args[4];
        }

        //If there is a source code attached file name is ommitted
        if (args.length >= 1) {
            if (args[0] != null && args[0].trim().length() != 0) {
                arguments[0] = null;
            }
        }

        jeliot.handleArgs(arguments);

        javax.swing.SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                jeliot.run();
            }
        });
        //LoadJeliot.start(jeliot);

        if (args.length >= 1) {
            if (args[0] != null && args[0].trim().length() != 0) {
                final String sourceCode = args[0];
                SwingUtilities.invokeLater(new Runnable() {

                    public void run() {
                        jeliot.setProgram(sourceCode);
                    }
                });
            }
        }
        return jeliot;
    }

    /**
     * 
     * @return
     */
    public String getIOPackageName() {
        return IOPackage;
    }

    /**
     * 
     * @param io_package
     */
    public void setIOPackageName(String IOPackage) {
        this.IOPackage = IOPackage;
        this.IOPackagePattern = Pattern.compile("import\\s+" + this.IOPackage + "\\s*;");
    }

    /**
     * 
     * @param src
     * @return
     */
    public boolean hasIOImport(String src) {
        return this.IOPackagePattern.matcher(src).find();
    }

    /**
     * 
     * @return
     */
    public String getImportIOStatement() {
        return "import " + getIOPackageName() + ";";
    }

    /**
     * 
     * @return
     */
    public boolean isHistoryViewVisible() {
        return hv.isVisible();
    }
    
    public HistoryView getHistoryView() {
        return this.hv;
    }
    
    /**
     * Called when Jeliot is closed.
     * Clean up.
     */
    public void close() {
        hv.close();
        stopThreads();
        director = null;
        gui = null;
        theatre = null;
        Tracker.writeToFile("JeliotClose", System.currentTimeMillis());
        Tracker.closeFile();
    }
}
package jeliot.launcher;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;

import jeliot.mcode.*;
import jeliot.util.ResourceBundles;
import generic.Interpreter;

/**
 * Launcher creates a new thread to handle the DynamicJava Interpreter's
 * intepretation. It also handles the piped streams between the two threads
 * one running the Jeliot's GUI and other one DynamicJava.
 * 
 * @author Niko Myller
 */
public class Launcher extends Thread {

    //  DOC: document!
    /**
     *
     */
    private PipedWriter pipedWriter = null;

    /**
     *
     */
    private PipedReader pipedReader = null;

    /**
     *
     */
    private PrintWriter writer = null;

    /**
     *
     */
    private BufferedReader reader = null;

    /**
     *
     */
    private boolean running = true; //indicates if interpreterThread is running

    /**
     * Pipe communicating Director ->DynamicJava
     * For Input Requests!!!!!!
     */
    private PipedWriter putInput = null;

    /**
     *
     */
    private PipedReader getInput = null;

    /**
     *
     */
    private PrintWriter inputWriter = null;

    /**
     *
     */
    private BufferedReader inputReader = null;

    /**
     *
     */
    private String methodCall = null;

    /**
     *
     */
    private InputStream r = null;

    /**
     *
     */
    private Interpreter interpreter = null;

    /**
     *
     */
    private boolean compiling = false;

    /**
     * @return
     */
    protected Interpreter createInterpreter() throws Exception{
    	// TODO: Added. create Factory, handle exception
    	String interpreterClass = ResourceBundles.getLangInterpreterInfo().getStringProperty("interpreter.class");
        Interpreter result =  (Interpreter)Class.forName(interpreterClass).newInstance();
               
        return result;    	
    }
    
    // TODO: added
    private void init(InputStream input) throws Exception
    {
    	this.r = input;
    	interpreter = createInterpreter();
        makePipedStreams();
            
        MCodeUtilities.initialize();

    }
    
    /**
     * @param input
     */
   /* public Launcher(Reader input) {
    	init(input);
    }*/
    // TODO: added
    public Launcher(InputStream input) throws Exception
    {
    	init(input);
    }
    
    // TODO: added    
    public Launcher(String input) throws Exception
    {
    	//init(new BufferedReader(new InputStreamReader(new ByteArrayInputStream(input.getBytes() )))); 
    	init(new ByteArrayInputStream(input.getBytes() ));
    }

    /**
     * 
     */
    public void makePipedStreams() {
        pipedWriter = new PipedWriter();
        writer = new PrintWriter(pipedWriter, true);
        putInput = new PipedWriter();
        inputWriter = new PrintWriter(putInput, true);

        try {
            pipedReader = new PipedReader(pipedWriter);

            getInput = new PipedReader(putInput);
        } catch (IOException e) {}

        reader = new BufferedReader(pipedReader);
        inputReader = new BufferedReader(getInput);

        MCodeUtilities.setWriter(writer);
        MCodeUtilities.setReader(inputReader);
        MCodeUtilities.setAccessingThread(this);
    }

    /**
     * @param methodCall
     */
    public void setMethodCall(String methodCall) {
        this.methodCall = methodCall;
    }

    /**
     * 
     */
    public void compile() {
        interpreter.interpret(r, "buffer");
    }

    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    public void run() {
        try {
            Object o = null;
            while (running && this == Thread.currentThread()) {
                if (compiling) {
                    compile();

                    // TODO: added. Only need to interpret when a method exists, otherwise intrpret at the compile
                    // phase
                    o = interpreter.interpret(new ByteArrayInputStream(methodCall.getBytes()),
                    "buffer");                    
                    /*o = interpreter.interpret(new BufferedReader(new StringReader(methodCall)),
                            "buffer");*/

                    if (!(o instanceof StoppingRequestedError)) {
                        /*
                         * TODO: If we are allowing open scope execution of statements
                         * we should not send Code.END statements.
                         */
                        //MCodeUtilities.write("" + Code.END);
                    	MCodeUtilities.write("" + Code.END);
                    }
                    compiling = false;
                }
                if (!(o instanceof StoppingRequestedError)) {
                    synchronized (this) {
                        try {
                            this.wait();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
                if (o instanceof StoppingRequestedError) {
                    stopThread();
                }
            }
        } catch (StoppingRequestedError e) {

        }
    }

    /**
     * 
     */
    public void stopThread() {
        running = false;
    }

    /**
     * @param value
     */
    public void setCompiling(boolean value) {
        compiling = value;
    }

    /**
     * @return
     */
    public PrintWriter getWriter() {
        return writer;
    }

    /**
     * @return
     */
    public BufferedReader getReader() {
        return reader;
    }

    /**
     * @return
     */
    public PrintWriter getInputWriter() {
        return inputWriter;
    }
}
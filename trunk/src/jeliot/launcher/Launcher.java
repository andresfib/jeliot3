package jeliot.launcher;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;

import jeliot.mcode.*;
import koala.dynamicjava.interpreter.Interpreter;
import koala.dynamicjava.interpreter.TreeInterpreter;
import koala.dynamicjava.parser.wrapper.JavaCCParserFactory;

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
	private Reader r = null;
    
	/**
	 *
	 */
	private Interpreter interpreter = createInterpreter();

	/**
	 *
	 */
	private boolean compiling = false;
    
    //private boolean executing = false;
    
	/**
	 * @return
	 */
	protected Interpreter createInterpreter() {
		Interpreter result = new TreeInterpreter(new JavaCCParserFactory());
		return result;
	}

	/**
	 * @param input
	 */
	public Launcher(Reader input) {
		this.r = input;

		makePipedStreams();
	}

	/**
	 * 
	 */
	public void makePipedStreams() {
		pipedWriter = new PipedWriter();
		writer = new PrintWriter(pipedWriter);
		putInput = new PipedWriter();
		inputWriter = new PrintWriter(putInput);

		try {
			pipedReader = new PipedReader(pipedWriter);

			getInput = new PipedReader(putInput);
		} catch (IOException e) {
			//System.err.println("When creating Pipe reader:" + e);
		}

		reader = new BufferedReader(pipedReader);
		inputReader = new BufferedReader(getInput);

		MCodeUtilities.setWriter(writer);
		MCodeUtilities.setReader(inputReader);
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
	public void compile() /* throws InterpreterException */ {
		//System.out.println("Compiling");
		interpreter.interpret(r, "buffer"); // (stream,"buffer");
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {

		//System.out.println("Before Compilation");

		while (running && this == Thread.currentThread()) {
			if (compiling) {
				compile();

				//             synchronized(this) {
				//                 try {
				//                     this.wait();
				//                 } catch(InterruptedException e) {
				//                     throw new RuntimeException(e);
				//                 }
				//             }

				//         try {
				//             Thread.sleep(100);
				//         } catch (InterruptedException e) {
				//             throw new RuntimeException(e);
				//         }
				//         //System.out.println("After Compilation");

				//         if (running && this == Thread.currentThread()) {

				//System.out.println("Before interpretation");

				interpreter.interpret(
					new BufferedReader(new StringReader(methodCall)),
					"buffer");
				// (stream,"buffer");

				MCodeUtilities.write("" + Code.END);
				//System.out.println("After interpretation");
				compiling = false;
			}
			synchronized (this) {
				try {
					this.wait();
					//                    Thread.sleep(100);
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			}
		}

		//         try {
		//             Thread.sleep(100);
		//         } catch (InterruptedException e) {
		//             throw new RuntimeException(e);
		//         }
		//System.out.println("After execution");

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
    //     public void setExecuting(boolean value) {
    //         executing = value;
    //     }


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
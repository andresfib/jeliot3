package jeliot.launcher;


import koala.dynamicjava.parser.wrapper.*;
import koala.dynamicjava.interpreter.*;

import java.io.*;
import java.util.*;

import jeliot.ecode.Code;

//import jeliot.gui.JeliotWindow;

public class Launcher extends Thread {

    private PipedWriter pipedWriter = new PipedWriter();
    private PipedReader pipedReader = null;

    private PrintWriter writer = new PrintWriter(pipedWriter);
    private BufferedReader reader = null;
    private boolean running=true; //indicates if interpreterThread is running

    //private String s="Testing.java"; // = get filename //
    //private Reader s = null;
    //private Reader stream = null;

    private String methodCall=null;
    private Reader r = null;
    private Interpreter interpreter=createInterpreter();

    protected Interpreter createInterpreter() {
        Interpreter result = new TreeInterpreter(new JavaCCParserFactory());
        return result;
    }

    public Launcher(Reader input) {
        this.r = input;
        Code.setWriter(writer);

        try {
            pipedReader = new PipedReader(pipedWriter);
        }
        catch (IOException e) {
            System.err.println("When creating Pipe reader:" + e);
            // throw e;
        }

        //  try{
        //    r = new FileReader(s);
        //}
        //catch (IOException e) {
        //    System.err.println(s+" file not found\n" + e);
        //  throw e;
        //}
        reader = new BufferedReader(pipedReader);
//        interpreter.interpret(r,"buffer");// (stream,"buffer");
    }

    public void setMethodCall(String methodCall) {
        this.methodCall = methodCall;
    }

    public void compile() throws InterpreterException {
        interpreter.interpret(r,"buffer");// (stream,"buffer");
    }

    public void run() {

        while(running && this == Thread.currentThread()) {

            interpreter.interpret(new BufferedReader(
                                      new StringReader(methodCall)),
                                      "buffer");
                                      // (stream,"buffer");

            jeliot.ecode.Code.write(""+jeliot.ecode.Code.END);

            synchronized(this) {
                try {

                    this.wait();

                } catch(InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }

    }

    public void stopThread(){
        running = false;
    }

    public PrintWriter getWriter(){
        return writer;
    }

    public BufferedReader getReader(){
        return reader;
    }

}

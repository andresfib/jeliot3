/*
 * Created on Oct 19, 2004
 */
package jeliot.mcode;

/**
 * @author nmyller
 */
public class StoppingRequestedError extends Error {

    /**
     * 
     */
    public StoppingRequestedError() {
        super();
        printRequestStop();
    }

    /**
     * @param arg0
     */
    public StoppingRequestedError(String arg0) {
        super(arg0);
        printRequestStop();
    }

    /**
     * @param arg0
     */
    public StoppingRequestedError(Throwable arg0) {
        super(arg0);
        printRequestStop();
    }

    /**
     * @param arg0
     * @param arg1
     */
    public StoppingRequestedError(String arg0, Throwable arg1) {
        super(arg0, arg1);
        printRequestStop();
    }

    public void printRequestStop() {
        //TODO: comment next line on public releases.
        System.out.println("Stopping Requested: " + Thread.currentThread().getName());
        //Thread.dumpStack();        
    }
    
}

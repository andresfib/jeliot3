package jeliot.theatre;

/**
  * ThreadController allows the execution of the Runnable object
  * controlled by it to be paused and resumed in a safe way. The
  * controller gets a Runnable object in its constructor. After it has
  * been constructed, the controller can be called to start or pause
  * the execution of its runnable.
  *     <P>
  * Calling the pause() method does not pause the execution
  * immediately, but only when the checkPoint() method is next called
  * in the controlled thread.
  *     <P>
  * Warning! ThreadController does not check that the checkPoint()
  * method is called from correct thread.
  *
  * @author Pekka Uronen
  *
  * created         20.9.1999
  * checked         27.9.1999
  */
public class ThreadController {

    /** Possible states of the controller. */
    private static final int RUNNING    = 1;
    private static final int PAUSEREQ   = 2;
    private static final int PAUSED     = 0;

    /** Current state of the controller. */
    private int status;

    /** The Runnable object controled by this controller. */
    private Runnable runner;

    /** A thread in which the Runnable is executed. */
    private Thread thread;

    /** Constructs a new controller for given Runnable. */
    public ThreadController(Runnable runner) {
        this.runner = runner;
    }

    /** Starts or resumes the Runnable immediately in its own thread.
      */
    public synchronized void start() {
        switch (status) {
            case (PAUSED):
                if (thread == null) {
                    thread = new Thread(runner);
                    thread.start();
                }
                else {
                    notify();
                }
                status = RUNNING;
                break;
            default:
                throw new RuntimeException();
        }
    }


    /** Instructs the controller to pause execution in next check
      * point.
      */
    public synchronized void pause() {
        switch (status) {
            case (RUNNING):
                status = PAUSEREQ;
                break;
            default:
                throw new RuntimeException();
        }
    }

    /** Pauses the execution, if pause() method has been called since
      * previous checkpoint.
      */
    public synchronized void checkPoint(Controlled cont) {
        switch (status) {
            case (RUNNING):
                break;
            case (PAUSEREQ):
                status = PAUSED;
                if (cont != null) {
                    cont.suspend();
                }
                try {
                    wait();
                }
                catch (InterruptedException e) { }
                if (cont != null) {
                    cont.resume();
                }
                status = RUNNING;
                break;
            default:
                throw new RuntimeException();
        }
    }

    public synchronized void checkPoint() {
        checkPoint(null);
    }

}

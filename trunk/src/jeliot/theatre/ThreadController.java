package jeliot.theatre;

/**
  * <p>
  * <code>ThreadController</code> allows the execution of the
  * <code>Runnable</code> object controlled by it to be paused and
  * resumed in a safe way. The controller gets a <code>Runnable</code>
  * object in its constructor. After it has been constructed, the
  * controller can be called to start or pause the execution of its
  * runnable.
  * </p><p>
  * Calling the <code>pause</code> method does not pause the execution
  * immediately, but only when the <code>checkPoint</code> method is
  * next called in the controlled thread.
  * </p><p>
  * <b>Warning!</b> <code>ThreadController<code> does not check that
  * the <code>checkPoint</code> method is called from correct thread.
  *</P>
  *
  * @author Pekka Uronen
  * @author Niko Myller
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

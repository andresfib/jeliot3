package jeliot.theatre;

/**
  * @author Pekka Uronen
  * @author Niko Myller
  */
public interface Controlled {
    public abstract void suspend();
    public abstract void resume();
}

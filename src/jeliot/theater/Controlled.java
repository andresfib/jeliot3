package jeliot.theater;

/**
  * @author Pekka Uronen
  * @author Niko Myller
  */
public interface Controlled {
    public abstract void suspend();
    public abstract void resume();
}

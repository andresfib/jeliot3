package jeliot.theatre;

/**
  * @author Pekka Uronen
  *
  * created         22.9.1999
  */
public interface Controlled {
    public abstract void suspend();
    public abstract void resume();
}

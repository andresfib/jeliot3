package jeliot.lang;

//import jeliot.parser.*;
import jeliot.theater.*;

/**
  *
  * @author Pekka Uronen
  * @author Niko Myller
  */
public class Value implements Cloneable {

    /**
	 *
	 */
	private String type;

	/** A String describing the represented value. Can be, for
      * example Integer, Char or if the value is actually a reference
      * it will be the hashcode that identifies the object.
      */
    private String val;
    /**
	 *
	 */
	private ValueActor actor;

    /**
	 *
	 */
	private int id;

    /**
	 * @param val
	 * @param type
	 */
	public Value(String val, String type) {
        this.type = type;
        this.val = val;
    }

    /**
	 * @param val
	 * @param type
	 * @param id
	 */
	public Value(String val, String type, int id) {
        this.type = type;
        this.val = val;
        this.id = id;
    }

    /**
	 * @param id
	 */
	public void setId(int id) {
        this.id = id;
    }

    /**
	 * @return
	 */
	public int getId() {
        return id;
    }

    /**
	 * @param b
	 * @return
	 */
	public static Value newBoolean(boolean b) {
        String bool = b ? Boolean.TRUE.toString() : Boolean.FALSE.toString();
        return new Value(bool, boolean.class.toString());
    }

    /* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
        return type.toString() + " " + val.toString();
    }

    /**
	 * @return
	 */
	public ValueActor getActor() {
        return actor;
    }

    /**
	 * @return
	 */
	public String getValue() {
        return val;
    }

    /**
	 * @return
	 */
	public String getType() {
        return type;
    }

    /**
	 * @param actor
	 */
	public void setActor(ValueActor actor) {
        this.actor = actor;
    }

    /* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public Object clone() {
        try {
            return super.clone();
        }
        catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }
}

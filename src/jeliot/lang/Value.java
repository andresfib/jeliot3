package jeliot.lang;

//import jeliot.parser.*;
import jeliot.theatre.*;
import jeliot.FeatureNotImplementedException;

/**
  *
  * @author Pekka Uronen
  *
  * created         9.8.1999
  * modified        11.12.2002 by Niko Myller
  */
public class Value implements Cloneable {

    private String type;

    /** An object describing the represented value. May be, for
      * example Integer, Char or Reference.
      */
    private String val;
    private ValueActor actor;

    private int id;

    public Value(String val, String type) {
        this.type = type;
        this.val = val;
    }

    public Value(String val, String type, int id) {
        this.type = type;
        this.val = val;
        this.id = id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static Value newBoolean(boolean b) {
        String bool = b ? Boolean.TRUE.toString() : Boolean.FALSE.toString();
        return new Value(bool, boolean.class.toString());
    }

    public String toString() {
        return type.toString() + " " + val.toString();
    }

    public ValueActor getActor() {
        return actor;
    }

    public String getValue() {
        return val;
    }

    public String getType() {
        return type;
    }

    public void setActor(ValueActor actor) {
        this.actor = actor;
    }

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

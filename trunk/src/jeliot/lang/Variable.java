package jeliot.lang;

import jeliot.theater.*;

/**
  * Variable is an instance of a variable - a field or a local
  * variable. A new variable is created runtime every time a local
  * variable is declared.
  *
  * @author Pekka Uronen
  * @author Niko Myller
  */
public class Variable {

//  DOC: document!

    /**
     *
     */
    Value value;
    
    /**
	 *
	 */
	VariableActor actor;
    
    /**
	 *
	 */
	String type;
    
    /**
	 *
	 */
	String name;
    
    /**
	 *
	 */
	String modifier;

    /**
	 * 
	 */
	protected Variable() { };

    /**
	 * @param name
	 * @param type
	 */
	public Variable(String name, String type) {
        this.type = type;
        this.name = name;
//         value = new Value(Code.UNKNOWN, type);
    }

    /**
	 * @param value
	 */
	public void assign(Value value) {
        this.value = value;
    }

    /**
	 * @return
	 */
	public Value getValue() {
         return value;
    }

    /**
	 * @return
	 */
	public String getName() {
        return name;
    }

    /**
	 * @param actor
	 */
	public void setActor(VariableActor actor) {
        this.actor = actor;
//         value.setActor(actor.getValue());
    }

    /**
	 * @return
	 */
	public VariableActor getActor() {
        return actor;
    }

    /**
	 * @return
	 */
	public String getType() {
        return type;
    }

    /**
	 * @param type
	 */
	protected void setType(String type) {
        this.type = type;
    }

    /**
	 * @return
	 */
	public String getModifier() {
        return modifier;
    }

    /**
	 * @param modifier
	 */
	protected void setModifier(String modifier) {
        this.modifier = modifier;
    }

}

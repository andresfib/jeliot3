package jeliot.lang;

//import jeliot.parser.*;
import jeliot.theatre.*;

import jeliot.ecode.*;

/**
  * Variable is an instance of a variable - a field or a local
  * variable. A new variable is created runtime every time a local
  * variable is declared.
  *
  * @author Pekka Uronen
  *
  * created         9.8.1999
  * last modified   16.8.1999
  * modified        12.12.2002 by Niko Myller
  */
public class Variable {

//    PVariableDeclarator declarator;
    Value value;
    VariableActor actor;
    String type;
    String name;
    String modifier;

    protected Variable() { };

    public Variable(String name, String type) {
        this.type = type;
        this.name = name;
//         value = new Value(Code.UNKNOWN, type);
    }

    public void assign(Value value) {
        this.value = value;
    }

    public Value getValue() {
         return value;
    }

    public String getName() {
        return name;
    }

    public void setActor(VariableActor actor) {
        this.actor = actor;
//         value.setActor(actor.getValue());
    }

    public VariableActor getActor() {
        return actor;
    }

    public String getType() {
        return type;
    }

    protected void setType(String type) {
        this.type = type;
    }

    public String getModifier() {
        return modifier;
    }

    protected void setModifier(String modifier) {
        this.modifier = modifier;
    }

}

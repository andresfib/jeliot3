package jeliot.theatre;

import java.lang.reflect.*;
import jeliot.parser.*;
import jeliot.lang.*;
import jeliot.gui.*;
import jeliot.*;

/**
  * @author Pekka Uronen
  *
  * created         25.8.1999
  */
public abstract class Animator {

//    Instance instance;
    private Value[] args;
    private ValueActor[] argact;
    private Value returnValue;

    public void setArguments(Value[] args) {
        this.args = args;
    }

    public void setArgumentActors(ValueActor[] argact) {
        this.argact = argact;
    }

    public Value getReturnValue() {
        return returnValue;
    }
    
    public void setReturnValue(Value v) {
        this.returnValue = v;
    }

    public Value getArgument(int i) {
        return args[i];
    }

    protected Actor getArgumentActor(int i) {
        return argact[i];
    }

    public abstract void animate(Director director); 

}

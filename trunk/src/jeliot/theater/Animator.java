package jeliot.theater;

import jeliot.lang.Value;

/**
  * @author Pekka Uronen
  * @author Niko Myller
  */
public abstract class Animator {

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

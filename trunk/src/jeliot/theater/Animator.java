package jeliot.theater;

import jeliot.lang.Value;

/**
  * @author Pekka Uronen
  * @author Niko Myller
  */
public abstract class Animator {

    /**
	 *
	 */
	private Value[] args;
    /**
	 *
	 */
	private ValueActor[] argact;
    /**
	 *
	 */
	private Value returnValue;

    /**
	 * @param args
	 */
	public void setArguments(Value[] args) {
        this.args = args;
    }

    /**
	 * @param argact
	 */
	public void setArgumentActors(ValueActor[] argact) {
        this.argact = argact;
    }

    /**
	 * @return
	 */
	public Value getReturnValue() {
        return returnValue;
    }

    /**
	 * @param v
	 */
	public void setReturnValue(Value v) {
        this.returnValue = v;
    }

    /**
	 * @param i
	 * @return
	 */
	public Value getArgument(int i) {
        return args[i];
    }

    /**
	 * @param i
	 * @return
	 */
	protected Actor getArgumentActor(int i) {
        return argact[i];
    }

    /**
	 * @param director
	 */
	public abstract void animate(Director director);

}

package jeliot.theater;

import jeliot.lang.Value;

/**
 * 
 * @author Pekka Uronen
 * @author Niko Myller
 */
public abstract class InputValidator {

    /**
	 *
	 */
	private ThreadController controller;
    /**
	 *
	 */
	private Value value;
    
    /**
	 * @param controller
	 */
	public void setController(ThreadController controller) {
        this.controller = controller;
    }
    
    /**
	 * @param value
	 */
	public void accept(Value value) {
        this.value = value;
        controller.start();
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
	public boolean isOk() {
        return value != null;
    }
    
    /**
	 * @param text
	 */
	public abstract void validate(String text);
}

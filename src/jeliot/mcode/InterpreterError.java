package jeliot.mcode;

import jeliot.theater.*;

/**
 * 
 * @author Niko Myller
 */
public class InterpreterError {

	/**
	 * The detailed message
	 */
	protected String message;

	/**
	 *
	 */
	protected Highlight highlight;

	/**
     * Constructs an <code>InterpreterException</code> from a ParseError
     * 
	 * @param message
	 * @param h
	 */
	public InterpreterError(String message, Highlight h) {
		this.message = message;
		this.highlight = h;
	}

	/**
	 * @param message
	 */
	public InterpreterError(String message) {
		this(message, null);
	}

	/**
	 * @return
	 */
	public Highlight getHighlight() {
		return highlight;
	}

	/**
	 * Returns the detailed message
	 * @return
	 */
	public String getMessage() {
		return message;
	}
}

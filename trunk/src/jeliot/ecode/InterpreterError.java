package jeliot.ecode;

import jeliot.theatre.*;

public class InterpreterError {

    /**
     * The detailed message
     */
    protected String message;

    protected Highlight highlight;

    /**
     * Constructs an <code>InterpreterException</code> from a ParseError
     */
    public InterpreterError(String message, Highlight h) {
        this.message = message;
        this.highlight = h;
    }

    public InterpreterError(String message) {
        this(message, null);
    }

    public Highlight getHighlight() {
        return highlight;
    }

    /**
     * Returns the detailed message
     */
    public String getMessage() {
    return message;
    }
}

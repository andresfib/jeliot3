package jeliot.theatre;

import jeliot.lang.Value;

/**
  * @author Pekka Uronen
  *
  * created         4.10.1999
  */
public abstract class InputValidator {

    private ThreadController controller;
    private Value value;
    
    public void setController(ThreadController controller) {
        this.controller = controller;
    }
    
    public void accept(Value value) {
        this.value = value;
        controller.start();
    }
    
    public Value getValue() {
        return value;
    }
    
    public boolean isOk() {
        return value != null;
    }
    
    public abstract void validate(String text);
}

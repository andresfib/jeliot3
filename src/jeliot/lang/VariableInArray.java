package jeliot.lang;

import jeliot.parser.*;
import jeliot.theatre.*;

/**
  * Variable is an instance of a variable - a field or a local
  * variable. A new variable is created runtime every time a local
  * variable is declared.
  *
  * @author Pekka Uronen
  *
  * created         11.10.1999
  */
public class VariableInArray extends Variable {

    ArrayInstance array;

    public VariableInArray() {}

    public VariableInArray(ArrayInstance array, String componentType) {
        this.array = array;
        setType(componentType);
    }

}

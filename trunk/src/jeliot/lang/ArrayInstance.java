package jeliot.lang;

import java.lang.reflect.*;
import java.util.*;
import jeliot.FeatureNotImplementedException;
import jeliot.theatre.*;
import jeliot.ecode.*;

/**
  * @author         Pekka Uronen
  * created         2.10.1999
  * @modified       Niko Myller
  *                 7.5.2003
  */
public class ArrayInstance extends Instance {

    private Object array;
    private String componentType;
    private int[] dimensions;
    private ArrayActor arrayActor;

    public ArrayInstance(String hashCode, String componentType, int[] dimensions) {
        super(hashCode);

        String type = componentType;
        for (int i = 0; i < dimensions.length; i++) {
               type = "[" + type;
        }

        setType(type);

        this.componentType = componentType;
        this.array = Array.newInstance((new VariableInArray()).getClass(), dimensions);
        this.dimensions = dimensions;

        // The array will be initialized with neutral values (zeros or nulls).
        Value cloneValue = new Value("null", componentType);

        switch(ECodeUtilities.resolveType(componentType)) {

            case ECodeUtilities.BOOLEAN: {
                cloneValue = new Value(Boolean.FALSE.toString(), boolean.class.getName());
                break;
            }

            case ECodeUtilities.BYTE: {
                cloneValue = new Value("" + ((byte)0), byte.class.getName());
                break;
            }

            case ECodeUtilities.SHORT: {
                cloneValue = new Value("" + ((short)0), short.class.getName());
                break;
            }

            case ECodeUtilities.INT: {
                cloneValue = new Value("0", int.class.getName());
                break;
            }

            case ECodeUtilities.LONG: {
                cloneValue = new Value("" + (0l), long.class.getName());
                break;
            }

            case ECodeUtilities.CHAR: {
                cloneValue = new Value("" + '\0', char.class.getName());
                break;
            }

            case ECodeUtilities.FLOAT: {
                cloneValue = new Value("" + (0.0f), float.class.getName());
                break;
            }

            case ECodeUtilities.DOUBLE: {
                cloneValue = new Value("" + (0.0), double.class.getName());
                break;
            }

            case ECodeUtilities.STRING: {
                cloneValue = new Value("", double.class.getName());
                break;
            }
        }
            //case ECodeUtilities.REFERENCE: {
                //break;
            //}

        int n = dimensions.length;

        int[] index = new int[n];

        for (int i = 0; i < n; i++) {
            index[i] = 0;
        }

        do {

            Object tempArray = array;

            for (int i = 0; i < n - 1; i++) {
                tempArray = Array.get(tempArray, index[i]);
            }

            for (int i = 0; i < dimensions[n-1]; i++) {
                VariableInArray via = new VariableInArray(this, componentType);
                Array.set(tempArray, i, via);
                Value v = (Value) cloneValue.clone();
                via.assign(v);
            }

        } while (ArrayUtilities.nextIndex(index, dimensions));

/*
        for (int i = 0; i < length; ++i) {
            array[i] = new VariableInArray(this);
            array[i].assign(componentType.neutralValue());
        }
*/

    }

    public VariableInArray getVariableAt(int[] index) {
        Object tempArray = array;
        int n = index.length;
        for (int i = 0; i < n; i++) {
            if (i == n - 1) {
                return (VariableInArray) Array.get(tempArray, index[i]);
            } else {
                tempArray = Array.get(tempArray, index[i]);
            }
        }
        return null;
    }

    public int[] getDimensions() {
        return dimensions;
    }

    public int getDimensionNumber() {
        return dimensions.length;
    }

    public void setValueAt(int[] index, Value newValue) {
        Object tempArray = array;
        for (int i = 0; i < index.length; i++) {
            if (i == index.length - 1) {
                VariableInArray via = (VariableInArray) Array.get(tempArray, index[i]);
                via.assign(newValue);
            } else {
                tempArray = Array.get(tempArray, index[i]);
            }
        }
    }

    public int length() {
        return Array.getLength(array);
    }

    public void setArrayActor(ArrayActor aa) {
        this.arrayActor = aa;
        setActor(aa);
    }

    public ArrayActor getArrayActor() {
        return arrayActor;
    }

    public String getComponentType() {
        return componentType;
    }

}
package jeliot.lang;

import java.lang.reflect.Array;

import jeliot.mcode.*;
import jeliot.theater.*;

 /**
  * The objects of this class represents an array of n-dimensions.
  * TODO: Maybe this should be changed to be arrays of one dimension and the chain them properly.
  * @author Pekka Uronen
  * @author Niko Myller
  */
public class ArrayInstance extends Instance {

    /**
     * The array object.
     */
    private Object array;
    
    /**
     * The string presentation of the type of the component.
     */
    private String componentType;
    
    /**
     * The length of each dimension in the array.
     */
    private int[] dimensions;
    
    /**
     * The corresponding array actor.
     */
    private ArrayActor arrayActor;

    /**
     * Creates the array and sets inside the VariableInArray object
     * which again contain the Value objects of the corresponding component type.
     * 
     * @param hashCode the hashCode of the array.
     * @param componentType The component type of the array.
     * @param dimensions The lengths of the dimensions of the array.
     */
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

        switch(MCodeUtilities.resolveType(componentType)) {

            case MCodeUtilities.BOOLEAN: {
                cloneValue = new Value(Boolean.FALSE.toString(), boolean.class.getName());
                break;
            }

            case MCodeUtilities.BYTE: {
                cloneValue = new Value("" + ((byte)0), byte.class.getName());
                break;
            }

            case MCodeUtilities.SHORT: {
                cloneValue = new Value("" + ((short)0), short.class.getName());
                break;
            }

            case MCodeUtilities.INT: {
                cloneValue = new Value("0", int.class.getName());
                break;
            }

            case MCodeUtilities.LONG: {
                cloneValue = new Value("" + (0l), long.class.getName());
                break;
            }

            case MCodeUtilities.CHAR: {
                cloneValue = new Value("" + '\0', char.class.getName());
                break;
            }

            case MCodeUtilities.FLOAT: {
                cloneValue = new Value("" + (0.0f), float.class.getName());
                break;
            }

            case MCodeUtilities.DOUBLE: {
                cloneValue = new Value("" + (0.0), double.class.getName());
                break;
            }

            case MCodeUtilities.STRING: {
                cloneValue = new Value("", double.class.getName());
                break;
            }
            
            case MCodeUtilities.REFERENCE: {
            	cloneValue = new Reference(componentType);
                break;
            }
        }

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

    /**
     * Returns the array variable from the given index in the array.
     * @param index an array containing the indeces for all dimensions of the array.
     * @return The VariableInArray object from the given index of the array.
     */
    public VariableInArray getVariableAt(int[] index) {
        Object tempArray = array;
        int n = index.length;
        for (int i = 0; i < n; i++) {
            if (i == n - 1) {
                return (VariableInArray) Array.get(tempArray, index[i]);
            }
            tempArray = Array.get(tempArray, index[i]);
        }
        return null;
    }

    /**
     * Gives the length of the dimensions in the array.
     * @return The length of the dimensions in the array.
     */
    public int[] getDimensions() {
        return dimensions;
    }

    /**
     * Gives the number of dimensions in the array
     * @return The number of dimensions in the array
     */
    public int getDimensionNumber() {
        return dimensions.length;
    }

    /**
     * Assigns the given value (second parameter) into the
     * VariableInArray in the given index (first parameter) of the array.
     * @param index The index of the array
     * @param newValue The new value of the VariableInArray.
     */
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

    /**
     * The dimensions of the array.
     * Is this needed because there is the other method doing the same?
     * @return The dimensions of the array
     */
    public int length() {
        return Array.getLength(array);
    }

    /**
     * Sets the corresponding ArrayActor.
     * @param aa Array actor for this array.
     */
    public void setArrayActor(ArrayActor aa) {
        this.arrayActor = aa;
        setActor(aa);
    }

    /**
     * Returns the corresponding array actor.
     * @return the array actor of this array.
     */
    public ArrayActor getArrayActor() {
        return arrayActor;
    }

    /**
     * Returns String presentation of the component type of the array
     * @return String presentation of the component type.
     */
    public String getComponentType() {
        return componentType;
    }

}
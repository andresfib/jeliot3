package jeliot.mcode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.Vector;

import org.python.compiler.CodeEvaluator;
import org.python.core.PyComplex;
import org.python.core.PyFloat;
import org.python.core.PyInteger;
import org.python.core.PyLong;
import org.python.core.PyObject;

import org.python.parser.SimpleNode;
import org.python.parser.ast.Name;
import org.python.parser.ast.Str;

import jeliot.mcode.Code;
import jeliot.mcode.Highlight;
import jeliot.mcode.MCodeUtilities;
import jeliot.util.DebugUtil;
import jeliot.util.Util;

import jeliot.mcode.*;
import koala.dynamicjava.interpreter.EvaluationVisitor;
/**
 * This class contains helper methods for the MCode language
 * extraction and interpretation. 
 *  
 * @author Niko Myller
 * @author Andrés Moreno
 * 
 * @see koala.dynamicjava.interpreter.TreeInterpreter
 * @see koala.dynamicjava.interpreter.EvaluationVisitor
 * @see jeliot.mcode.MCodeInterpreter
 * @see jeliot.mcode.TheaterMCodeInterpreter
 * @see jeliot.mcode.CallTreeMCodeInterpreter
 */
public class MCodeUtilitiesBOld extends MCodeUtilities{ 

    /**
     * Should be never used. 
     */
    private MCodeUtilitiesBOld() {
    }

    /**
     * @param type
     * @return
     */
    public static int resolveType(String type) {
        if (type.equals(boolean.class.getName())
                || type.equals(Boolean.class.getName())) {

            return MCodeUtilitiesBOld.BOOLEAN;

        } else if (type.equals(byte.class.getName())
                || type.equals(Byte.class.getName())) {

            return MCodeUtilitiesBOld.BYTE;

        } else if (type.equals(short.class.getName())
                || type.equals(Short.class.getName())) {

            return MCodeUtilitiesBOld.SHORT;

        } else if (type.equals(int.class.getName())
                || type.equals(Integer.class.getName())) {

            return MCodeUtilitiesBOld.INT;

        } else if (type.equals(long.class.getName())
                || type.equals(Long.class.getName())) {

            return MCodeUtilitiesBOld.LONG;

        } else if (type.equals(char.class.getName())
                || type.equals(Character.class.getName())) {

            return MCodeUtilitiesBOld.CHAR;

        } else if (type.equals(float.class.getName())
                || type.equals(Float.class.getName())) {

            return MCodeUtilitiesBOld.FLOAT;

        } else if (type.equals(double.class.getName())
                || type.equals(Double.class.getName())) {

            return MCodeUtilitiesBOld.DOUBLE;

        } else if (type.equals(String.class.getName())
                || type.equals("L" + String.class.getName() + ";")) {

            return MCodeUtilitiesBOld.STRING;

        } else if (type.equals(Void.TYPE.getName()) || type.equals("V")) {

            return MCodeUtilitiesBOld.VOID;

        } else {

            return MCodeUtilitiesBOld.REFERENCE;
        }
    }

    public static boolean isNumeric(String type) {
        if (type.equals(byte.class.getName())
                || type.equals(Byte.class.getName())
                || type.equals(short.class.getName())
                || type.equals(Short.class.getName())
                || type.equals(int.class.getName())
                || type.equals(Integer.class.getName())
                || type.equals(long.class.getName())
                || type.equals(Long.class.getName())
                || type.equals(float.class.getName())
                || type.equals(Float.class.getName())
                || type.equals(double.class.getName())
                || type.equals(Double.class.getName())) {
            return true;
        } else {
            return false;
        }
    }

    /*
     public static int resolveType(Class type) {
     if (type.isPrimitive()) {
     if (type.toString().equals(boolean.class.toString())) {
     return ECodeUtilities.BOOLEAN;
     } else if (type.toString().equals(byte.class.toString())) {
     return ECodeUtilities.BYTE;
     } else if (type.toString().equals(short.class.toString())) {
     return ECodeUtilities.SHORT;
     } else if (type.toString().equals(int.class.toString())) {
     return ECodeUtilities.INT;
     } else if (type.toString().equals(long.class.toString())) {
     return ECodeUtilities.LONG;
     } else if (type.toString().equals(char.class.toString())) {
     return ECodeUtilities.CHAR;
     } else if (type.toString().equals(float.class.toString())) {
     return ECodeUtilities.FLOAT;
     } else if (type.toString().equals(double.class.toString())) {
     return ECodeUtilities.DOUBLE;
     } else {
     return ECodeUtilities.VOID;
     }
     } else if (type.toString().equals("".getClass().toString())) {
     return ECodeUtilities.STRING;
     } else {
     return ECodeUtilities.REFERENCE;
     }
     }
     */

    /**
     * @param type
     * @return
     */
    public static boolean isPrimitive(String type) {
        if (Util.visualizeStringsAsObjects()
                && resolveType(type) == MCodeUtilitiesBOld.STRING) {
            return false;
        }
        if (resolveType(type) != MCodeUtilitiesBOld.REFERENCE
                && resolveType(type) != MCodeUtilitiesBOld.VOID) {
            return true;
        }
        return false;
    }

    /**
     * @param type
     * @return
     */
    public static boolean isArray(String type) {
        if (type.indexOf("[") == -1) {
            return false;
        }
        return true;
    }

    /**
     * @param type
     * @return
     */
    public static int getNumberOfDimensions(String type) {
        int n = type.length();
        int dims = 0;
        for (int i = 0; i < n; i++) {
            if (type.substring(i, i + 1).equals("[")) {
                dims++;
            } else {
                return dims;
            }
        }
        return dims;
    }

    /**
     * @param type
     * @return
     */
    public static String resolveComponentType(String type) {
        if (isArray(type)) {
            String cType = replace(replace(type, "[", ""), ";", "");
            if (cType.substring(0, 1).equals("L")) {
                return cType.substring(1);
            }
            return cType;
        }
        return type;
    }

    /**
     * @param type
     * @return
     */
    public static String changeComponentTypeToPrintableForm(String type) {
        if (type.equals(boolean.class.getName()) || type.equals("Z")) {
            return boolean.class.getName();
        } else if (type.equals(byte.class.getName()) || type.equals("B")) {
            return byte.class.getName();
        } else if (type.equals(short.class.getName()) || type.equals("S")) {
            return short.class.getName();
        } else if (type.equals(int.class.getName()) || type.equals("I")) {
            return int.class.getName();
        } else if (type.equals(long.class.getName()) || type.equals("J")) {
            return long.class.getName();
        } else if (type.equals(char.class.getName()) || type.equals("C")) {
            return char.class.getName();
        } else if (type.equals(float.class.getName()) || type.equals("F")) {
            return float.class.getName();
        } else if (type.equals(double.class.getName()) || type.equals("D")) {
            return double.class.getName();
        } else if (type.equals("".getClass().getName())
                || type.equals("L" + "".getClass().getName())) {
            return "".getClass().getName();
        } else if (type.equals(Void.TYPE.getName()) || type.equals("V")) {
            return Void.TYPE.getName();
        } else {
            return type; //type.substring(1);
        }
    }


    /**
     * @param type
     * @return
     */
    public static String getDefaultValue(String type) {
        if (type.equals(boolean.class.getName())
                || type.equals((new Boolean(true)).getClass().getName())
                || type.equals("Z")) {

            return String.valueOf(false);

        } else if (type.equals(byte.class.getName())
                || type.equals((new Byte((byte) 0)).getClass().getName())
                || type.equals("B")) {

            return String.valueOf((byte) 0);

        } else if (type.equals(short.class.getName())
                || type.equals((new Short((short) 0)).getClass().getName())
                || type.equals("S")) {

            return String.valueOf((short) 0);

        } else if (type.equals(int.class.getName())
                || type.equals((new Integer(0)).getClass().getName())
                || type.equals("I")) {

            return String.valueOf(0);

        } else if (type.equals(long.class.getName())
                || type.equals((new Long(0)).getClass().getName())
                || type.equals("J")) {

            return String.valueOf(0L);

        } else if (type.equals(char.class.getName())
                || type.equals((new Character('\0')).getClass().getName())
                || type.equals("C")) {

            return String.valueOf('\u0000');

        } else if (type.equals(float.class.getName())
                || type.equals((new Float(0.0f)).getClass().getName())
                || type.equals("F")) {

            return String.valueOf(0.0f);

        } else if (type.equals(double.class.getName())
                || type.equals((new Double(0.0)).getClass().getName())
                || type.equals("D")) {

            return String.valueOf(0.0);

        } else if (type.equals("".getClass().getName())
                || type.equals("L".getClass().getName())) {

            return String.valueOf("null");

        } else if (type.equals(Void.TYPE.getName()) || type.equals("V")) {

            return String.valueOf("null");

        } else {

            return String.valueOf("null");

        }
    }

    public static boolean isSetPreparing()
    {
    	return (CodeEvaluator.isSetPreparing());
    }
    
   
    /*    public static void openMCodeFile(){
     try {
     
     outputFile = new File(filePath);
     //we delete the file if it already exists
     if (outputFile.exists()) outputFile.delete();
     }catch(Exception e){
     e.printStackTrace(System.out);    
     }
     
     
     }

    /**
     * 
     * @param o
     * @return
     */
    public static String getValue(Object o) {
        if (o == null) {
            return "null";
        }
        Object value = null;
        
    	if (o instanceof PyInteger) {
            value = ((PyInteger) o).getValue();            
        } else if (o instanceof PyLong) {
            value = ((PyObject)o).__str__().toString();            
        } else if (o instanceof PyFloat) {
            value = ((PyFloat) o).getValue();
        } else if (o instanceof PyComplex) {
            value = ((PyComplex) o).imag;
        }
    	if (value != null)
    		return value.toString();
    	
        if (o.getClass().isPrimitive() || String.class.isInstance(o)
                || Integer.class.isInstance(o) || Double.class.isInstance(o)
                || Byte.class.isInstance(o) || Long.class.isInstance(o)
                || Short.class.isInstance(o) || Boolean.class.isInstance(o)
                || Float.class.isInstance(o) || Character.class.isInstance(o)) {
            if (Util.visualizeStringsAsObjects() && String.class.isInstance(o)) {
                return o.toString() + "@"
                        + Integer.toHexString(System.identityHashCode(o));
            }
            return o.toString();
        }
        return Integer.toHexString(System.identityHashCode(o));
    }

    /**
     * 
     * @param str
     * @return
     */
    public static String[] getStringValues(String str) {
        if (str.lastIndexOf("@") >= 0) {
            String[] strs = new String[2];
            strs[0] = str.substring(0, str.lastIndexOf("@"));
            strs[1] = str.substring(str.lastIndexOf("@") + 1);
            return strs;
        } else {
            return null;
        }
    }


    /**
     * 
     * @param exp
     * @param visitor
     * @return
     */
    public static String stringConversion(SimpleNode exp, CodeEvaluator visitor) {
    	try{
        if (MCodeUtilitiesB.isConvertedToString(exp)) { //ask for type implements tree.Literal
            return String.valueOf(exp.accept(visitor));
        } else {
            //return MCodeGeneratorB.toStringCall(exp, visitor);
        	return null;
        }
    	}catch(Exception e)
    	{
    		System.out.println(e);
    		return null;
    	}
    }

    /**
     * 
     * @param exp
     * @return
     */
    public static boolean isConvertedToString(SimpleNode exp) {
    	boolean automaticStringConversion = (exp.getClass().getName().equals(Str.class.getName()));
    	return automaticStringConversion
        || (exp instanceof Name);
    	
        /*Class c = (Class) NodeProperties.getType(exp);
        boolean automaticStringConversion = (c.isPrimitive()
                || String.class.getName().equals(c.getName())
                || Integer.class.getName().equals(c.getName())
                || Double.class.getName().equals(c.getName())
                || Byte.class.getName().equals(c.getName())
                || Long.class.getName().equals(c.getName())
                || Short.class.getName().equals(c.getName())
                || Boolean.class.getName().equals(c.getName())
                || Float.class.getName().equals(c.getName()) || Character.class
                .getName().equals(c.getName()));

        return automaticStringConversion
                || (exp instanceof koala.dynamicjava.tree.Literal);*/
    }

    /**
     * return a full qualified classname, e.g. [C resulting from char[] will be [char, because C [] will be [C as well...
     * @param c
     * @return qualified classname
     */
    public static String getFullQualifiedClassname(Class c) {
        if (!c.isArray())
            return c.getName();
        return "[" + getFullQualifiedClassname(c.getComponentType());
    }
    
    /**
     * 
     * @param value
     * @param type
     * @return
     */
    public static String getLangValue(String value, String type) {
        if (Util.visualizeStringsAsObjects() && MCodeUtilitiesBOld.resolveType(type) == MCodeUtilitiesBOld.STRING) {
            String[] strs = MCodeUtilitiesBOld.getStringValues(value);
            if (strs != null) {
                return strs[0];
            }            
        }
        return value;
    }
}
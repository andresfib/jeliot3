package jeliot.ecode;

import jeliot.theatre.*;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.*;
import koala.dynamicjava.interpreter.EvaluationVisitor;

public class ECodeUtilities {

    //Unary expressions in Jeliot 3 visualization engine
    public static final int COMP = 4;           // Complement
    public static final int MINUS = 1;          // Minus
    public static final int MINUSMINUS = 3;     // PreDecrement
    public static final int NOT = 5;            // Logical Not
    public static final int PLUS = 0;           // Plus
    public static final int PLUSPLUS = 2;       // PreIncrement
    public static final int POSTMINUSMINUS = 7; // PostDecrement
    public static final int POSTPLUSPLUS = 6;   // PostIncrement

    //Binary expressions in Jeliot 3 visualization engine
    public static final int AND = 15;           // Bitwise And
    public static final int ANDAND = 18;        // Logical And
    public static final int DIV = 1;            // Division
    public static final int EQEQ = 13;          // Equality
    public static final int GT = 9;             // Greater Than
    public static final int GTEQ = 11;          // Greater Than Or Equal
    public static final int INSTANCEOF = 12;    // Instance Of
    public static final int LSHIFT = 5;         // Bitwise Left Shift
    public static final int LT = 8;             // Lesser Than
    public static final int LTEQ = 10;          // Lesser Than or Equal
    public static final int SUBSTRACT = 4;      // Substraction
    public static final int MOD = 2;            // Remainder
    public static final int MULT = 0;           // Multiplication
    public static final int NOTEQ = 14;         // Not Equality
    public static final int OR = 17;            // Bitwise Or
    public static final int OROR = 19;          // Logical Or
    public static final int ADD = 3;            // Addition
    public static final int RSHIFT = 6;         // Bitwise Right Shift
    public static final int URSHIFT = 7;        // Bitwise Unsigned Right Shift
    public static final int XOR = 16;           // Bitwise Xor
    public static final int LXOR = 20;          // Logical Xor

    //Types
    public static final int VOID = -1;
    public static final int BOOLEAN = 0;
    public static final int BYTE = 1;
    public static final int SHORT = 2;
    public static final int INT = 3;
    public static final int LONG = 4;
    public static final int CHAR = 5;
    public static final int FLOAT = 6;
    public static final int DOUBLE = 7;
    public static final int STRING = 8;
    public static final int REFERENCE = 9;

    //Highest code is now 54

    // private static PrintWriter writer=Launcher.getWriter();
    private static PrintWriter writer=null;

    private static BufferedReader reader=null;

    public static int resolveType(String type) {
        if (type.equals(boolean.class.getName()) ||
            type.equals((new Boolean(true)).getClass().getName()) ||
            type.equals("Z")) {

            return ECodeUtilities.BOOLEAN;

        } else if (type.equals(byte.class.getName()) ||
                   type.equals((new Byte((byte)0)).getClass().getName()) ||
                   type.equals("B")) {

            return ECodeUtilities.BYTE;

        } else if (type.equals(short.class.getName()) ||
                   type.equals((new Short((short)0)).getClass().getName()) ||
                   type.equals("S")) {

            return ECodeUtilities.SHORT;

        } else if (type.equals(int.class.getName()) ||
                   type.equals((new Integer(0)).getClass().getName()) ||
                   type.equals("I")) {

            return ECodeUtilities.INT;

        } else if (type.equals(long.class.getName()) ||
                   type.equals((new Long(0)).getClass().getName()) ||
                   type.equals("J")) {

            return ECodeUtilities.LONG;

        } else if (type.equals(char.class.getName()) ||
                   type.equals((new Character('\0')).getClass().getName()) ||
                   type.equals("C")) {

            return ECodeUtilities.CHAR;

        } else if (type.equals(float.class.getName()) ||
                   type.equals((new Float(0.0f)).getClass().getName()) ||
                   type.equals("F")) {

            return ECodeUtilities.FLOAT;

        } else if (type.equals(double.class.getName()) ||
                   type.equals((new Double(0.0)).getClass().getName()) ||
                   type.equals("D")) {

            return ECodeUtilities.DOUBLE;

        } else if (type.equals("".getClass().getName()) ||
                   type.equals("L".getClass().getName())) {

            return ECodeUtilities.STRING;

        } else if (type.equals(Void.TYPE.getName()) ||
                   type.equals("V")) {

            return ECodeUtilities.VOID;

        } else {

            return ECodeUtilities.REFERENCE;

        }
    }

//     public static int resolveType(Class type) {
//         if (type.isPrimitive()) {
//             if (type.toString().equals(boolean.class.toString())) {
//                 return ECodeUtilities.BOOLEAN;
//             } else if (type.toString().equals(byte.class.toString())) {
//                 return ECodeUtilities.BYTE;
//             } else if (type.toString().equals(short.class.toString())) {
//                 return ECodeUtilities.SHORT;
//             } else if (type.toString().equals(int.class.toString())) {
//                 return ECodeUtilities.INT;
//             } else if (type.toString().equals(long.class.toString())) {
//                 return ECodeUtilities.LONG;
//             } else if (type.toString().equals(char.class.toString())) {
//                 return ECodeUtilities.CHAR;
//             } else if (type.toString().equals(float.class.toString())) {
//                 return ECodeUtilities.FLOAT;
//             } else if (type.toString().equals(double.class.toString())) {
//                 return ECodeUtilities.DOUBLE;
//             } else {
//                 return ECodeUtilities.VOID;
//             }
//         } else if (type.toString().equals("".getClass().toString())) {
//             return ECodeUtilities.STRING;
//         } else {
//             return ECodeUtilities.REFERENCE;
//         }
//     }

    public static boolean isPrimitive(String type) {
        if (resolveType(type) != ECodeUtilities.REFERENCE) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isArray(String type) {
        if (type.indexOf("[") == -1) {
            return false;
        }
        return true;
    }

    public static int getNumberOfDimensions(String type) {
        int n = type.length();
        boolean stillArray = true;
        int dims = 0;
        for (int i = 0; i < n; i++) {
            if (type.substring(i, i+1).equals("[")) {
                dims++;
            } else {
                return dims;
            }
        }
        return dims;
    }

    public static String resolveComponentType(String type) {
        if (isArray(type)) {
            String cType = replace(replace(type, "[", ""), ";", "");
            if (cType.substring(0, 1).equals("L") {
                return cType.substring(1);
            } else {
                return cType;
            }
        } else {
            return null;
        }
    }

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
        } else if (type.equals("".getClass().getName()) || type.equals("L".getClass().getName())) {
            return "".getClass().getName();
        } else if (type.equals(Void.TYPE.getName()) || type.equals("V")) {
            return Void.TYPE.getName();
        } else {
            return type.substring(1);
        }
    }

    public static String replace(String from, String c, String with) {
        int index = from.indexOf(c);
        while(index != -1) {
            from = from.substring(0, index) +
            with +
            from.substring(index + 1, from.length());
            index = from.indexOf(c);
        }
        return from;
    }

    public static int resolveBinOperator(int operator) {
        switch (operator) {
            //Add expression (+ sign)
            case Code.AE: {
                return ADD;
            }

            //Substract expression (- sign)
            case Code.SE: {
                return SUBSTRACT;
            }

            //Greater than expression (> sign)
            case Code.GT: {
                return GT;
            }

            //Logical AND expression (&& sign)
            case Code.AND: {
                return ANDAND;
            }

            // Logical Xor Expression (^ sign)
            case Code.XOR: {
                return LXOR;
            }

            //Arithmetic multiplication (* sign)
            case Code.ME: {
                return MULT;
            }

            //Arithmetic division (/ sign)
            case Code.DE: {
                return DIV;
            }

            //Arithmetic remainder (% sign)
            case Code.RE: {
                return MOD;
            }

            // Or Expression (|| sign)
            case Code.OR: {
                return OROR;
            }

            // Equal Expression (== sign)
            case Code.EE: {
                return EQEQ;
            }

            // Not Equal Expression (!= sign)
            case Code.NE: {
                return NOTEQ;
            }

            // Less Expression (< sign)
            case Code.LE: {
                return LT;
            }

            // Less or Equal Expression (<= sign)
            case Code.LQE: {
                return LTEQ;
            }

            // Greater or Equal Expression (>= sign)
            case Code.GQT: {
                return GTEQ;
            }

            // Bitwise Or Expression (| sign)
            case Code.BITOR: {
                return OR;
            }

            // Bitwise Xor Expression (^ sign)
            case Code.BITXOR: {
                return XOR;
            }

            // Bitwise And Expression (& sign)
            case Code.BITAND: {
                return AND;
            }

            // Bitwise Left Shift Expression (<< sign)
            case Code.LSHIFT: {
                return LSHIFT;
            }

            // Bitwise Right Shift Expression (>> sign)
            case Code.RSHIFT: {
                return RSHIFT;
            }

            // Bitwise Unsigned Right Shift Expression (>>> sign)
            case Code.URSHIFT: {
                return URSHIFT;
            }

            //This is an error.
            default: {
                return -1;
            }
        }
    }

    public static int resolveUnOperator(int operator) {
        switch (operator) {

            //Logical NOT expression (! sign)
            case Code.NO: {
                return NOT;
            }

            //Aritmetic minus expression (- sign)
            case Code.MINUS: {
                return MINUS;
            }

            //Aritmetic plus expression (+ sign)
            case Code.PLUS: {
                return PLUS;
            }

            //PostIncrement expression (++ sign)
            case Code.PIE: {
                return POSTPLUSPLUS;
            }

            //PreIncrement expression (sign ++)
            case Code.PRIE: {
                return PLUSPLUS;
            }

            //PostDecrement expression (-- sign)
            case Code.PDE: {
                return POSTMINUSMINUS;
            }

            //PreDecrement expression (sign --)
            case Code.PRDE: {
                return MINUSMINUS;
            }

            //Complement expression (~ sign)
            case Code.COMP: {
                return COMP;
            }

            //This is an error.
            default: {
                return -1;
            }
        }
    }

    public static boolean isUnary(int operator) {
        switch (operator) {
            case Code.COMP:     //Complement expression
            case Code.MINUS:    //Unary minus expression
            case Code.PLUS:     //Unary plus expression
            case Code.NO:       //Unary not expression
            case Code.PIE:      //PostIncrement expression (++ sign)
            case Code.PRIE:     //PreIncrement expression (sign ++)
            case Code.PDE:      //PostDecrement expression (-- sign)
            case Code.PRDE: {   //PreDecrement expression (sign --)
                return true;
            }

            default: {
                return false;
            }
        }
    }

    public static boolean isBinary(int operator) {
        switch (operator) {

            case Code.BITOR:    // Bitwise Or Expression
            case Code.BITXOR:   // Bitwise Xor Expression
            case Code.BITAND:   // Bitwise And Expression
            case Code.LSHIFT:   // Bitwise Left Shift Expression
            case Code.RSHIFT:   // Bitwise Right Shift Expression
            case Code.URSHIFT:  // Bitwise Unsigned Right Shift Expression

            case Code.EE:       // Equal Expression
            case Code.NE:       // Not Equal Expression
            case Code.LE:       // Less Expression
            case Code.GT:       // Greater Than
            case Code.LQE:      // Less or Equal Expression
            case Code.GQT:      // Greater or Equal Expression

            case Code.A:        // Assignment Expression

            case Code.OR:       // Or Expression
            case Code.XOR:      // Xor Expression
            case Code.AND:      // And Expression

            case Code.ME:       // Multiplication Expression
            case Code.RE:       // Remainder (mod) Expression
            case Code.DE:       // Division Expression
            case Code.SE:       // Substract Expression
            case Code.AE: {     // Add Expression
                return true;
            }

            default: {
                return false;
            }
        }
    }

    public static Highlight makeHighlight(String h) {
        StringTokenizer st = new StringTokenizer(h, Code.LOC_DELIM);
        int bl = Integer.parseInt(st.nextToken());
        int bc = Integer.parseInt(st.nextToken());
        int el = Integer.parseInt(st.nextToken());
        int ec = Integer.parseInt(st.nextToken());
        return new Highlight(bl, bc, el, ec);
    }

    public static String getHashCode(String str) {
        int index = str.indexOf('@');
        if (index > 0) {
            str = str.substring(index+1);
        }
        return str;
    }

    public static void setWriter(PrintWriter w){
        writer=w;
    }

    //For output handling!!!!
    public static void setReader(BufferedReader r){
        reader=r;
    }

    public static void write(String str){
        if ( !EvaluationVisitor.isSetPreparing() ) {

            writer.println(str); // connected to jeliot

            //System.out.println(str);// Output to stdout ; debugging only
        }
    }

    public static Object readInt(){
    int result;
    try {

        result=Integer.parseInt(reader.readLine());
        return new Integer(result);
    } catch (Exception e) {
        return null;
        //ThrowException!!!!!!!!!!!!!!!
    }

    }

    public static Object readDouble(){
    double result;
    try {

        result=Double.parseDouble(reader.readLine());
        return new Double(result);
    } catch (Exception e) {
        return null;
        //ThrowException!!!!!!!!!!!!!!!
    }

    }

    public static String argToString(List argnames){
    //Change to extract elements from list and add delims

        if (!argnames.isEmpty()) {
            String result="";
            Iterator it= argnames.listIterator();
            while(it.hasNext()){
            result+=(String)it.next()+Code.LOC_DELIM;
        }
            return result.substring(0,result.length()-1);
        } else {
            return "";
        }
    }
    public static String arrayToString(Object[] array){
    //Displays the array as an string
        String result="";
        for (int i=0;i<array.length;i++) {
            result+=array[i];
            if (i<array.length-1)
                result+=Code.LOC_DELIM;
        }
        return result;
    }

}

package jeliot.ecode;

import jeliot.theatre.*;

import java.util.*;


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
    public static final int AND = 15;           // Bit And
    public static final int ANDAND = 18;        // Logical And
    public static final int DIV = 1;            // Division
    public static final int EQEQ = 13;          // Equality
    public static final int GT = 9;             // Greater Than
    public static final int GTEQ = 11;          // Greater Than Or Equal
    public static final int INSTANCEOF = 12;    // Instance Of
    public static final int LSHIFT = 5;         // Bit Left Shift
    public static final int LT = 8;             // Lesser Than
    public static final int LTEQ = 10;          // Lesser Than or Equal
    public static final int SUBSTRACT = 4;      // Substraction
    public static final int MOD = 2;            // Remainder
    public static final int MULT = 0;           // Multiplication
    public static final int NOTEQ = 14;         // Not Equal
    public static final int OR = 17;            // Bit Or
    public static final int OROR = 19;          // Logical Or
    public static final int ADD = 3;            // Addition
    public static final int RSHIFT = 6;         // Bit Right Shift
    public static final int URSHIFT = 7;        // Bit URight Shift
    public static final int XOR = 16;           // Bit Xor

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

    public static int resolveType(String type) {
        if (type.equals(boolean.class.getName())) {
            return ECodeUtilities.BOOLEAN;
        } else if (type.equals(byte.class.getName())) {
            return ECodeUtilities.BYTE;
        } else if (type.equals(short.class.getName())) {
            return ECodeUtilities.SHORT;
        } else if (type.equals(int.class.getName())) {
            return ECodeUtilities.INT;
        } else if (type.equals(long.class.getName())) {
            return ECodeUtilities.LONG;
        } else if (type.equals(char.class.getName())) {
            return ECodeUtilities.CHAR;
        } else if (type.equals(float.class.getName())) {
            return ECodeUtilities.FLOAT;
        } else if (type.equals(double.class.getName())) {
            return ECodeUtilities.DOUBLE;
        } else if (type.equals("".getClass().getName())) {
            return ECodeUtilities.STRING;
        } else if (type.equals(Void.TYPE.getName())) {
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
        if (type.equals(boolean.class.getName()) ||
            type.equals(byte.class.getName()) ||
            type.equals(short.class.getName()) ||
            type.equals(int.class.getName()) ||
            type.equals(long.class.getName()) ||
            type.equals(char.class.getName()) ||
            type.equals(float.class.getName()) ||
            type.equals(double.class.getName()) ||
            type.equals("".getClass().getName()) ||
            type.equals(Void.TYPE.getName())) {
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
            case OR:     {
                return OROR;
            }

            // Equal Expression (== sign)
            case EE:     {
                return EQEQ;
            }

            // Not Equal Expression (!= sign)
            case NE:     {
                return NOTEQ;
            }

            // Less Expression (< sign)
            case LE:     {
                return LT;
            }

            // Less or Equal Expression (<= sign)
            case LQE:    {
                return LTEQ;
            }

            // Greater or Equal Expression (>= sign)
            case GQT:    {
                return GTEQ;
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

            //This is an error.
            default: {
                return -1;
            }
        }
    }

    public static boolean isUnary(int operator) {
        switch (operator) {
            case Code.MINUS:    //Unary minus expression
            case Code.PLUS:     //Unary plus expression
            case Code.NO: {     //Not expression
                return true;
            }

            default: {
                return false;
            }
        }
    }

    public static boolean isBinary(int operator) {
        switch (operator) {
            case Code.OR:       // Or Expression
            case Code.EE:       // Equal Expression
            case Code.NE:       // Not Equal Expression
            case Code.LE:       // Less Expression
            case Code.GT:       // Greater Than
            case Code.LQE:      // Less or Equal Expression
            case Code.GQT:      // Greater or Equal Expression
            case Code.A:        // Assignment Expression
            case Code.ME:       // Multiplication Expression
            case Code.RE:       // Remainder (mod) Expression
            case Code.DE:       // Division Expression
            case Code.AND:      // And Expression
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

}
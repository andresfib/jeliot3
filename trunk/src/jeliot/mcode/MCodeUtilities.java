package jeliot.mcode;

import koala.dynamicjava.interpreter.EvaluationVisitor;
import koala.dynamicjava.tree.Node;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.*;

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
public class MCodeUtilities {

    /**
     * Should be never used. 
     */
    private MCodeUtilities() {}

    //Unary expressions in Jeliot 3 visualization engine
    /**
     * Complement
     */
    public static final int COMP = 4;

    /**
     * Minus
     */
    public static final int MINUS = 1;

    /**
     * PreDecrement
     */
    public static final int MINUSMINUS = 3;

    /**
     * Logical Not
     */
    public static final int NOT = 5;

    /**
     * Plus
     */
    public static final int PLUS = 0;

    /**
     * PreIncrement
     */
    public static final int PLUSPLUS = 2;

    /**
     * PostDecrement
     */
    public static final int POSTMINUSMINUS = 7;

    /**
     * PostIncrement
     */
    public static final int POSTPLUSPLUS = 6;

    //Binary expressions in Jeliot 3 visualization engine
    /**
     * Bitwise And
     */
    public static final int AND = 15;

    /**
     * Logical And
     */
    public static final int ANDAND = 18;

    /**
     * Division
     */
    public static final int DIV = 1;

    /**
     * Equality
     */
    public static final int EQEQ = 13;

    /**
     * Greater Than
     */
    public static final int GT = 9;

    /**
     * Greater Than Or Equal
     */
    public static final int GTEQ = 11;

    /**
     * Instance Of
     */
    public static final int INSTANCEOF = 12;

    /**
     * Bitwise Left Shift
     */
    public static final int LSHIFT = 5;

    /**
     * Lesser Than
     */
    public static final int LT = 8;

    /**
     * Lesser Than or Equal
     */
    public static final int LTEQ = 10;

    /**
     * Substraction
     */
    public static final int SUBSTRACT = 4;

    /**
     * Remainder
     */
    public static final int MOD = 2;

    /**
     * Multiplication
     */
    public static final int MULT = 0;

    /**
     *Not Equality
     */
    public static final int NOTEQ = 14;

    /**
     * Bitwise Or
     */
    public static final int OR = 17;

    /**
     * Logical Or
     */
    public static final int OROR = 19;

    /**
     * Addition
     */
    public static final int ADD = 3;

    /**
     * Bitwise Right Shift
     */
    public static final int RSHIFT = 6;

    /**
     * Bitwise Unsigned Right Shift
     */
    public static final int URSHIFT = 7;

    /**
     * Bitwise Xor
     */
    public static final int XOR = 16;

    /**
     * Logical Xor
     */
    public static final int LXOR = 20;

    //Types
    /**
     * Void type
     */
    public static final int VOID = -1;

    /**
     * Boolean type
     */
    public static final int BOOLEAN = 0;

    /**
     * Byte type
     */
    public static final int BYTE = 1;

    /**
     * Short type
     */
    public static final int SHORT = 2;

    /**
     * Int type
     */
    public static final int INT = 3;

    /**
     * Long type
     */
    public static final int LONG = 4;

    /**
     * Char type
     */
    public static final int CHAR = 5;

    /**
     * Float type
     */
    public static final int FLOAT = 6;

    /**
     * Double type
     */
    public static final int DOUBLE = 7;

    /**
     * String type
     */
    public static final int STRING = 8;

    /**
     * Referent type
     */
    public static final int REFERENCE = 9;

    //DOC: document!

    /**
     *
     */
    private static PrintWriter writer = null;

    // private static PrintWriter writer=Launcher.getWriter();

    /**
     *
     */
    private static BufferedReader reader = null;

    private static Vector registeredSecondaryMCodeConnections = new Vector();

    /**
     * Hack flag to get the output into see below
     */
    private static boolean redirectOutput = false;

    /**
     * Buffer to store the redirection orders
     * from parameters collected in TreeInterpreter
     */
    private static Vector redirectBuffer = new Vector();

    /**
     * Stack with the redirect buffers
     */
    public static Stack redirectBufferStack = new Stack();

    /**
     * 
     */
    public static Stack numParametersStack = new Stack();

    /**
     * 
     */
    private static int numParameters = 0;

    /**
     * @param type
     * @return
     */
    public static int resolveType(String type) {
        if (type.equals(boolean.class.getName())
                || type.equals((new Boolean(true)).getClass().getName()) || type.equals("Z")) {

            return MCodeUtilities.BOOLEAN;

        } else if (type.equals(byte.class.getName())
                || type.equals((new Byte((byte) 0)).getClass().getName()) || type.equals("B")) {

            return MCodeUtilities.BYTE;

        } else if (type.equals(short.class.getName())
                || type.equals((new Short((short) 0)).getClass().getName()) || type.equals("S")) {

            return MCodeUtilities.SHORT;

        } else if (type.equals(int.class.getName())
                || type.equals((new Integer(0)).getClass().getName()) || type.equals("I")) {

            return MCodeUtilities.INT;

        } else if (type.equals(long.class.getName())
                || type.equals((new Long(0)).getClass().getName()) || type.equals("J")) {

            return MCodeUtilities.LONG;

        } else if (type.equals(char.class.getName())
                || type.equals((new Character('\0')).getClass().getName()) || type.equals("C")) {

            return MCodeUtilities.CHAR;

        } else if (type.equals(float.class.getName())
                || type.equals((new Float(0.0f)).getClass().getName()) || type.equals("F")) {

            return MCodeUtilities.FLOAT;

        } else if (type.equals(double.class.getName())
                || type.equals((new Double(0.0)).getClass().getName()) || type.equals("D")) {

            return MCodeUtilities.DOUBLE;

        } else if (type.equals("".getClass().getName()) || type.equals("L".getClass().getName())) {

            return MCodeUtilities.STRING;

        } else if (type.equals(Void.TYPE.getName()) || type.equals("V")) {

            return MCodeUtilities.VOID;

        } else {

            return MCodeUtilities.REFERENCE;

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
        if (resolveType(type) != MCodeUtilities.REFERENCE) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * @param type
     * @return
     */
    public static boolean isArray(String type) {
        if (type.indexOf("[") == -1) { return false; }
        return true;
    }

    /**
     * @param type
     * @return
     */
    public static int getNumberOfDimensions(String type) {
        int n = type.length();
        boolean stillArray = true;
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
            } else {
                return cType;
            }
        } else {
            return null;
        }
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
        } else if (type.equals("".getClass().getName()) || type.equals("L" + "".getClass().getName())) {
            return "".getClass().getName();
        } else if (type.equals(Void.TYPE.getName()) || type.equals("V")) {
            return Void.TYPE.getName();
        } else {
            return type; //type.substring(1);
        }
    }

    /**
     * @param from
     * @param c
     * @param with
     * @return
     */
    public static String replace(String from, String c, String with) {
        int index = from.indexOf(c);
        int l = c.length();
        while (index != -1) {
            from = from.substring(0, index) + with + from.substring(index + l, from.length());
            index = from.indexOf(c);
        }
        return from;
    }

    /**
     * @param from
     * @param identifier
     * @return
     */
    public static int findNumber(String from, String identifier) {
        int number = 0;
        int index = from.toLowerCase().indexOf(identifier);
        if (index > -1) {
            String message = from.substring(index + identifier.length()).trim();
            int i = 1;
            while (true) {
                if (!Character.isDigit(message.substring(i - 1, i).charAt(0))) {
                    break;
                }
                i++;
            }
            if (i > 1) {
                number = Integer.parseInt(message.substring(0, i - 1));
            }
        }
        return number;
    }

    /**
     * @param operator
     * @return
     */
    public static int resolveBinOperator(int operator) {
        switch (operator) {
            //Add expression (+ sign)
            case Code.AE:
                {
                    return ADD;
                }

            //Substract expression (- sign)
            case Code.SE:
                {
                    return SUBSTRACT;
                }

            //Greater than expression (> sign)
            case Code.GT:
                {
                    return GT;
                }

            //Logical AND expression (&& sign)
            case Code.AND:
                {
                    return ANDAND;
                }

            // Logical Xor Expression (^ sign)
            case Code.XOR:
                {
                    return LXOR;
                }

            //Arithmetic multiplication (* sign)
            case Code.ME:
                {
                    return MULT;
                }

            //Arithmetic division (/ sign)
            case Code.DE:
                {
                    return DIV;
                }

            //Arithmetic remainder (% sign)
            case Code.RE:
                {
                    return MOD;
                }

            // Or Expression (|| sign)
            case Code.OR:
                {
                    return OROR;
                }

            // Equal Expression (== sign)
            case Code.EE:
                {
                    return EQEQ;
                }

            // Not Equal Expression (!= sign)
            case Code.NE:
                {
                    return NOTEQ;
                }

            // Less Expression (< sign)
            case Code.LE:
                {
                    return LT;
                }

            // Less or Equal Expression (<= sign)
            case Code.LQE:
                {
                    return LTEQ;
                }

            // Greater or Equal Expression (>= sign)
            case Code.GQT:
                {
                    return GTEQ;
                }

            // Bitwise Or Expression (| sign)
            case Code.BITOR:
                {
                    return OR;
                }

            // Bitwise Xor Expression (^ sign)
            case Code.BITXOR:
                {
                    return XOR;
                }

            // Bitwise And Expression (& sign)
            case Code.BITAND:
                {
                    return AND;
                }

            // Bitwise Left Shift Expression (<< sign)
            case Code.LSHIFT:
                {
                    return LSHIFT;
                }

            // Bitwise Right Shift Expression (>> sign)
            case Code.RSHIFT:
                {
                    return RSHIFT;
                }

            // Bitwise Unsigned Right Shift Expression (>>> sign)
            case Code.URSHIFT:
                {
                    return URSHIFT;
                }

            //This is an error.
            default:
                {
                    return -1;
                }
        }
    }

    /**
     * @param operator
     * @return
     */
    public static int resolveUnOperator(int operator) {
        switch (operator) {

            //Logical NOT expression (! sign)
            case Code.NO:
                {
                    return NOT;
                }

            //Aritmetic minus expression (- sign)
            case Code.MINUS:
                {
                    return MINUS;
                }

            //Aritmetic plus expression (+ sign)
            case Code.PLUS:
                {
                    return PLUS;
                }

            //PostIncrement expression (++ sign)
            case Code.PIE:
                {
                    return POSTPLUSPLUS;
                }

            //PreIncrement expression (sign ++)
            case Code.PRIE:
                {
                    return PLUSPLUS;
                }

            //PostDecrement expression (-- sign)
            case Code.PDE:
                {
                    return POSTMINUSMINUS;
                }

            //PreDecrement expression (sign --)
            case Code.PRDE:
                {
                    return MINUSMINUS;
                }

            //Complement expression (~ sign)
            case Code.COMP:
                {
                    return COMP;
                }

            //This is an error.
            default:
                {
                    return -1;
                }
        }
    }

    /**
     * @param operator
     * @return
     */
    public static boolean isUnary(int operator) {
        switch (operator) {
            case Code.COMP:
            //Complement expression
            case Code.MINUS:
            //Unary minus expression
            case Code.PLUS:
            //Unary plus expression
            case Code.NO:
            //Unary not expression
            case Code.PIE:
            //PostIncrement expression (++ sign)
            case Code.PRIE:
            //PreIncrement expression (sign ++)
            case Code.PDE:
            //PostDecrement expression (-- sign)
            case Code.PRDE:
                { //PreDecrement expression (sign --)
                    return true;
                }

            default:
                {
                    return false;
                }
        }
    }

    /**
     * @param operator
     * @return
     */
    public static boolean isBinary(int operator) {
        switch (operator) {

            case Code.BITOR:
            // Bitwise Or Expression
            case Code.BITXOR:
            // Bitwise Xor Expression
            case Code.BITAND:
            // Bitwise And Expression
            case Code.LSHIFT:
            // Bitwise Left Shift Expression
            case Code.RSHIFT:
            // Bitwise Right Shift Expression
            case Code.URSHIFT:
            // Bitwise Unsigned Right Shift Expression

            case Code.EE:
            // Equal Expression
            case Code.NE:
            // Not Equal Expression
            case Code.LE:
            // Less Expression
            case Code.GT:
            // Greater Than
            case Code.LQE:
            // Less or Equal Expression
            case Code.GQT:
            // Greater or Equal Expression

            case Code.A:
            // Assignment Expression

            case Code.OR:
            // Or Expression
            case Code.XOR:
            // Xor Expression
            case Code.AND:
            // And Expression

            case Code.ME:
            // Multiplication Expression
            case Code.RE:
            // Remainder (mod) Expression
            case Code.DE:
            // Division Expression
            case Code.SE:
            // Substract Expression
            case Code.AE:
                { // Add Expression
                    return true;
                }

            default:
                {
                    return false;
                }
        }
    }

    /**
     * @param h
     * @return
     */
    public static Highlight makeHighlight(String h) {
        StringTokenizer st = new StringTokenizer(h, Code.LOC_DELIM);
        int bl = Integer.parseInt(st.nextToken());
        int bc = Integer.parseInt(st.nextToken());
        int el = Integer.parseInt(st.nextToken());
        int ec = Integer.parseInt(st.nextToken());
        return new Highlight(bl, bc, el, ec);
    }

    /**
     * @param str
     * @return
     */
    public static String getHashCode(String str) {
        int index = str.indexOf('@');
        if (index > 0) {
            str = str.substring(index + 1);
        }
        return str;
    }

    /**
     * @param type
     * @return
     */
    public static String getDefaultValue(String type) {
        if (type.equals(boolean.class.getName())
                || type.equals((new Boolean(true)).getClass().getName()) || type.equals("Z")) {

            return String.valueOf(false);

        } else if (type.equals(byte.class.getName())
                || type.equals((new Byte((byte) 0)).getClass().getName()) || type.equals("B")) {

            return String.valueOf((byte) 0);

        } else if (type.equals(short.class.getName())
                || type.equals((new Short((short) 0)).getClass().getName()) || type.equals("S")) {

            return String.valueOf((short) 0);

        } else if (type.equals(int.class.getName())
                || type.equals((new Integer(0)).getClass().getName()) || type.equals("I")) {

            return String.valueOf(0);

        } else if (type.equals(long.class.getName())
                || type.equals((new Long(0)).getClass().getName()) || type.equals("J")) {

            return String.valueOf(0L);

        } else if (type.equals(char.class.getName())
                || type.equals((new Character('\0')).getClass().getName()) || type.equals("C")) {

            return String.valueOf('\u0000');

        } else if (type.equals(float.class.getName())
                || type.equals((new Float(0.0f)).getClass().getName()) || type.equals("F")) {

            return String.valueOf(0.0f);

        } else if (type.equals(double.class.getName())
                || type.equals((new Double(0.0)).getClass().getName()) || type.equals("D")) {

            return String.valueOf(0.0);

        } else if (type.equals("".getClass().getName()) || type.equals("L".getClass().getName())) {

            return String.valueOf("null");

        } else if (type.equals(Void.TYPE.getName()) || type.equals("V")) {

            return String.valueOf("null");

        } else {

            return String.valueOf("null");

        }
    }

    /**
     * @param w
     */
    public static void setWriter(PrintWriter w) {
        writer = w;
    }

    /**
     * For input handling.
     * @param r
     */
    public static void setReader(BufferedReader r) {
        reader = r;
    }

    /**
     * @param str
     */
    public static void write(String str) {
        StringTokenizer tokenizer = new StringTokenizer(str, Code.DELIM);
        int token = Integer.parseInt(tokenizer.nextToken());
        if (!EvaluationVisitor.isSetPreparing() || token == Code.ERROR) {
            str = MCodeUtilities.replace(str, "\n", "\\n");
            str = MCodeUtilities.replace(str, "\r", "");
            //System.out.println("Hello");
            if (!redirectOutput) {
                writer.println(str);
            } else {
                addToRedirectBuffer(str);
            }
            // connected to jeliot
            //System.out.println("Hello2");

            //System.out.println(str);// Output to stdout ; debugging only
        }
    }

    /**
     * @return
     */
    public static Object readInt() {
        int result;
        try {
            result = Integer.parseInt(reader.readLine());
            return new Integer(result);
        } catch (Exception e) {
            return null;
            //ThrowException!!!!!!!!!!!!!!!
        }
    }

    /**
     * @return
     */
    public static Object readDouble() {
        double result;
        try {
            result = Double.parseDouble(reader.readLine());
            return new Double(result);
        } catch (Exception e) {
            return null;
            //ThrowException!!!!!!!!!!!!!!!
        }
    }

    /**
     * @return
     */
    public static Object readChar() {
        char result;
        try {
            //Get the first character of the readed string
            result = (reader.readLine()).charAt(0);
            return new Character(result);
        } catch (Exception e) {
            return null;
            //ThrowException!!!!!!!!!!!!!!!
        }

    }

    /**
     * @return
     */
    public static Object readString() {
        String result;
        try {

            result = (reader.readLine());
            return new String(result);
        } catch (Exception e) {
            return null;
            //ThrowException!!!!!!!!!!!!!!!
        }

    }

    /**
     * @param argnames
     * @return
     */
    public static String argToString(List argnames) {
        //Change to extract elements from list and add delims

        if (!argnames.isEmpty()) {
            String result = "";
            Iterator it = argnames.listIterator();
            while (it.hasNext()) {
                result += (String) it.next() + Code.LOC_DELIM;
            }
            return result.substring(0, result.length() - 1);
        } else {
            return "";
        }
    }

    /**
     * @param array
     * @return
     */
    public static String parameterArrayToString(Object[] array) {
        String result = "";
        for (int i = 0; i < array.length; i++) {
            result += ((Class) array[i]).getName();
            if (i < array.length - 1)
                result += Code.LOC_DELIM;
        }

        return result;
    }

    /**
     * @param array
     * @return
     */
    public static String arrayToString(Object[] array) {
        //Displays the array as an string
        String result = "";
        for (int i = 0; i < array.length; i++) {
            result += array[i];
            if (i < array.length - 1)
                result += Code.LOC_DELIM;
        }
        return result;
    }

    /**
     * @return
     */
    public static boolean getRedirectOutput() {
        return redirectOutput;
    }

    /**
     * @param value
     */
    public static void setRedirectOutput(boolean value) {
        redirectOutput = value;
    }

    public static void writeRedirectBuffer(Vector redirectBuffer) {
        for (int i = 0; i < redirectBuffer.size(); i++) {
            write((String) redirectBuffer.get(i));
        }
    }

    public static void clearRedirectBuffer() {
        redirectBuffer.clear();
    }

    public static void addToRedirectBuffer(String string) {
        redirectBuffer.add(string);
    }

    /**
     * @return
     */
    public static int getNumParameters() {
        return numParameters;
    }

    /**
     * 
     */
    public static void incNumParameters() {
        numParameters++;
    }

    /**
     * 
     */
    public static void clearNumParameters() {
        numParameters = 0;
    }

    /**
     * 
     */
    public static Vector getRedirectBuffer() {
        return redirectBuffer;
    }

    public static void printToRegisteredSecondaryMCodeConnections(String str) {
        Iterator i = registeredSecondaryMCodeConnections.iterator();
        while (i.hasNext()) {
            ((PrintWriter) i.next()).println(str);
        }
    }
    
    public static void addRegisteredSecondaryMCodeConnections(PrintWriter pw) {
        if (!registeredSecondaryMCodeConnections.contains(pw)) {
            registeredSecondaryMCodeConnections.add(pw);
        }
    }
    
    public static void clearRegisteredSecondaryMCodeConnections() {
        Iterator i = registeredSecondaryMCodeConnections.iterator();
        while (i.hasNext()) {
            ((PrintWriter) i.next()).println("" + Code.END);
        }
        registeredSecondaryMCodeConnections.clear();
    }
    
    /**
     * 
     * @param o
     * @return
     */
    public static String getValue(Object o){   
        if (o == null) { 
            return "null"; 
        } 
        if (o.getClass().isPrimitive()
            || String.class.isInstance ( o )
            || Integer.class.isInstance( o )
        	|| Double.class.isInstance( o )
			|| Byte.class.isInstance( o )
			|| Long.class.isInstance( o )
			|| Short.class.isInstance ( o )
			|| Boolean.class.isInstance(o)
            || Float.class.isInstance(o) 
            || Character.class.isInstance(o)) {
                
            return o.toString();   
        } else { 
            return Integer.toHexString(o.hashCode());            
        } 
    }
    /**
     * Converts the node location into a string list. Each element is delimited
     * by Code.LOC_DELIM
     * @param node the node to visit
     */
	public static String locationToString(Node node) {
		return node.getBeginLine()
			+ Code.LOC_DELIM
			+ node.getBeginColumn()
			+ Code.LOC_DELIM
			+ node.getEndLine()
			+ Code.LOC_DELIM
			+ node.getEndColumn();
	}

    public static Object readLong() {
        long result;
        try {
            result = Long.parseLong(reader.readLine());
            return new Long(result);
        } catch (Exception e) {
            return null;
        }
    }

    public static Object readByte() {
        byte result;
        try {
            result = Byte.parseByte(reader.readLine());
            return new Byte(result);
        } catch (Exception e) {
            return null;
        }

    }

    public static Object readFloat() {
        float result;
        try {
            result = Float.parseFloat(reader.readLine());
            return new Float(result);
        } catch (Exception e) {
            return null;
        }
    }

    public static Object readBoolean() {
        boolean result;
        try {
            result = Boolean.getBoolean(reader.readLine());
            return new Boolean(result);
        } catch (Exception e) {
            return null;
        }
    }

    public static BufferedReader getReader () {
        return reader;
    }

    public static Object readShort() {
        short result;
        try {
            result = Short.parseShort(reader.readLine());
            return new Short(result);
        } catch (Exception e) {
            return null;
        }
    }
    
    static String filename = "untitled";
    
    public static void setFilename(String name) {
    	filename = name;
    }
    
    public static String getFilename() {
    	return filename;
    }
}

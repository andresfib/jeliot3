package jeliot.mcode;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * @author Niko Myller
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class Code {

	/**
	 * The resource bundle used in this package
	 */
	static private ResourceBundle bundle =
		ResourceBundle.getBundle(
			"jeliot.ecode.resources.properties",
			Locale.getDefault());

	//Useful constants
	/** Delimiter of the mcode expressions */
	public static final String DELIM = bundle.getString("delim");
	/** Delimiter of the mcode location expressions */
	public static final String LOC_DELIM = bundle.getString("delim.location");
	/** Symbol for an unknown value */
	public static final String UNKNOWN = bundle.getString("unknown_value");
	/** Symbol for no reference */
	public static final int NO_REFERENCE =
		Integer.parseInt(bundle.getString("no_reference"));
	/** Symbol for a non-final variable */
	public static final int NOT_FINAL =
		Integer.parseInt(bundle.getString("not_final"));
	/** Symbol for a final variable */
	public static final int FINAL = Integer.parseInt(bundle.getString("final"));
	/** Symbolic presentation for true value */
	public static final String TRUE = Boolean.TRUE.toString();
	/** Symbolic presentation for false value */
	public static final String FALSE = Boolean.FALSE.toString();
	/** Symbolic presentation for null reference */
	public static final String REFERENCE = "null";
	// Literal Type is not primitive!!

	//Auxiliary statements
	/** Left-hand side of the expression. */
	public static final int LEFT = 1;
	/** Right-hand side of the expression. */
	public static final int RIGHT = 2;
	/** Beginning of the expression */
	public static final int BEGIN = 3;
	/** Indicates that a value is going to me assigned to expression defined in referenced variable */
	public static final int TO = 4;
	/** Error statement */
	public static final int ERROR = 53;
	/** End of Program */
	public static final int END = 36;
	/**	Constructor call number to determine when the constructor call is finished. (SPECIAL) used for a hack */
	public static final int CONSCN = 72;
	

	// statements and expressions
	/** Assignment */
	public static final int A = 5;

	// binary arithmetic operations
	/** Add Expression */
	public static final int AE = 6;
	/** Substract Expression */
	public static final int SE = 7;
	/** Multiplication Expression */
	public static final int ME = 8;
	/** Division Expression */
	public static final int DE = 9;
	/** Remainder Expression */
	public static final int RE = 10;

	// Bitwise binary operations
	/** Bitwise OR operator */
	public static final int BITOR = 46;
	/** Bitwise XOR operator */
	public static final int BITXOR = 47;
	/** Bitwise AND operator */
	public static final int BITAND = 48;
	/** Bitwise left shift operator */
	public static final int LSHIFT = 49;
	/** Bitwise right shift operator */
	public static final int RSHIFT = 50;
	/** Bitwise unsigned right shift operator */
	public static final int URSHIFT = 51;

	// unary arithmetic expressions
	/** Plus Expression */
	public static final int PLUS = 11;
	/** Minus Expression */
	public static final int MINUS = 12;

	// unary arithmetic expressions (increments/decrements)
	/** Post Increment Expression */
	public static final int PIE = 13;
	/** Pre Increment Expression */
	public static final int PRIE = 14;
	/** Post Decrement Expression */
	public static final int PDE = 15;
	/** Pre Decrement Expression */
	public static final int PRDE = 16;

	// Bitwise unary operations
	/** Complement Expression */
	public static final int COMP = 45;

	// binary boolean  exps
	/** Xor Expression */
	public static final int XOR = 52;
	/** And Expression */
	public static final int AND = 17;
	/** Or Expression */
	public static final int OR = 18;
	/** Equal Expression */
	public static final int EE = 19;
	/** Not Equal Expression */
	public static final int NE = 20;
	/** Greater Than */
	public static final int GT = 21;
	/** Less Expression */
	public static final int LE = 22;
	/** Less or Equal Expression */
	public static final int LQE = 23;
	/** Greater or Equal Expression */
	public static final int GQT = 24;

	// unary boolen exps
	/** Boolean Not */
	public static final int NO = 25;

	// Literal constant and variable access
	/** Qualified Name */
	public static final int QN = 27;
	/** Literal */
	public static final int L = 28;

	// Statements and others
	/** Variable Declaration */
	public static final int VD = 26;
	/** Opening and closing a scope */
	public static final int SCOPE = 35;

	// Control Structures
	/** If Then Statement */
	public static final int IFT = 33;
	/** If Then Else Statement */
	public static final int IFTE = 34;
	/** Break statement */
	public static final int BR = 39;
	/** While statement */
	public static final int WHI = 40;
	/** For statement */
	public static final int FOR = 41;
	/** Continue statement */
	public static final int CONT = 42;
	/** Do-While statement */
	public static final int DO = 43;
	/** Switch block found */
	public static final int SWIBF = 68;
	/** Switch block begins */
	public static final int SWITCHB = 69;
	/** Switch */
	public static final int SWITCH = 70;

	//Input and output
	/**	Output statement */
	public static final int OUTPUT = 44;
	/** Input statement */ 
	public static final int INPUT = 54;
	/** Input statement */
	public static final int INPUTTED = 55;

	// Methods
	/** Static Method Call */
	public static final int SMC = 29;
	/** Parameter */
	public static final int P = 30;
	/** Parameters list */
	public static final int PARAMETERS = 31;
	/** Return Statement */
	public static final int R = 32;
	/** Static Method call closed */
	public static final int SMCC = 38;
	/** Method declaration */
	public static final int MD = 37;

	//Methods related to objects
	/** Object method call */
	public static final int OMC = 66;
	/** Object method call close */
	public static final int OMCC = 67;
	/** Simple class allocation */
	public static final int SA = 63;
	/** Simple class allocation close */
	public static final int SAC = 64;

	/** Object field access */
	public static final int OFA = 65;

	// Array handling
	/**	Array allocation */
	public static final int AA = 56;
	/** Array access */
	public static final int AAC = 57;
	/** Array length */
	public static final int AL = 71;

	// Class information
	/**	Starting the information for a class. */
	public static final int CLASS = 58; 
	/** Ends the information for a class. */	 
	public static final int END_CLASS = 59;
	/** Indicates a constructor in the class information */
	public static final int CONSTRUCTOR = 60;
	/** Indicates a method in the class information */
	public static final int METHOD = 61;
	/** Indicates a field in the class information */
	public static final int FIELD = 62;

	//Last int used = 72
}

//package jeliot.ecode;
/*
import jeliot.launcher.*;

import java.util.*;

// For debugging pourposes :)
public class Code{
    //Auxiliary statements
    public static final String LEFT="LEFT";
    public static final String RIGHT="RIGHT";
    public static final String BEGIN="BEGIN";
    public static final String TO="TO";
    //Useful constants
    public static final String DELIM=" ";
    public static final String LOC_DELIM=",";
    public static final String UNKNOWN="?";
    public static final long   NO_REFERENCE=0;
    public static final String NOT_FINAL="NOT FINAL";
    public static final String FINAL="FINAL";
    public static final String TRUE="true";
    public static final String FALSE="false";
    public static final String REFERENCE="null";
    //JAVA statements and expressions
    public static final String A="A";//Assignment
    public static final String AE="AE";//Add Expression
    public static final String SE="SE";//Substract Expression
    public static final String VD="VD";//Variable Declaration
    public static final String QN="QN";//Qualified Name
    public static final String L="L";//Literal
    public static final String SMC="SMC";//Static Method Call
    public static final String P="P";//Parameter
    public static final String PARAMETERS="PARAMETERS";//Parameters list
    public static final String R="R";// Return Statement
    public static final String NO="NO";//Boolean Not
    public static final String GT="GT";//Greater Than
    public static final String AND="AND";//And Expression
    public static final String IFT="IFT";//If Then Statement
    public static final String IFTE="IFTE";//IF Then Else Statement
    public static final String SCOPE="SCOPE";//Opening and closing a new Scope
    public static final String END="END";//End of Program
    public static final String MD="MD";//Method declaration

    //////////
    private static PrintWriter writer=Launcher.getWriter();
    public static void write(String str){
       writer.println(str);
   }
    public static String argToString(List argnames){
    //Change to extract elements from list and add delims
    if (!argnames.isEmpty()){
        String result=argnames.toString();
        int length=result.length();
        return result.substring(1,length-1);
    }
    else return "";
    }
}
*/

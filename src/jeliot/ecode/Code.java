package jeliot.ecode;
import java.io.*;
import jeliot.launcher.*;
import java.util.*;



public class Code {

    //Auxiliary statements
    public static final int LEFT=1;
    public static final int RIGHT=2;
    public static final int BEGIN=3;
    public static final int TO=4;

    //Useful constants
    public static final String DELIM="§";
    public static final String LOC_DELIM=",";
    public static final String UNKNOWN="?";
    public static final int NO_REFERENCE=0;
    public static final int NOT_FINAL=1;
    public static final int FINAL=0;
    public static final String TRUE=Boolean.TRUE.toString();
    public static final String FALSE=Boolean.FALSE.toString();
    public static final String REFERENCE="null"; // Literal Type is not primitive!!

    // JAVA statements and expressions
    public static final int A=5;            // Assignment

    // binary arithmetic operations
    public static final int AE=6;           // Add Expression
    public static final int SE=7;           // Substract Expression
    public static final int ME=8;           // Multiplication Expression
    public static final int DE=9;           // Division Expression
    public static final int RE=10;          // Remainder Expression

    // Bitwise binary operations
    public static final int BITOR=46;
    public static final int BITXOR=47;
    public static final int BITAND=48;
    public static final int LSHIFT=49;
    public static final int RSHIFT=50;
    public static final int URSHIFT=51;

    // unary arithmetic expressions
    public static final int PLUS=11;        // Plus Expression
    public static final int MINUS=12;       // Minus Expression

    // unary arithmetic expressions (increments/decrements)
    public static final int PIE=13;         // Post Increment Expression
    public static final int PRIE=14;        // Pre Increment Expression
    public static final int PDE=15;         // Post Decrement Expression
    public static final int PRDE=16;        // Pre Decrement Expression

    // Bitwise unary operations
    public static final int COMP=45;        // Complement Expression

    // binary boolean  exps
    public static final int XOR=52;         // Xor Expression
    public static final int AND=17;         // And Expression
    public static final int OR=18;          // Or Expression
    public static final int EE=19;          // Equal Expression
    public static final int NE=20;          // Not Equal Expression
    public static final int GT=21;          // Greater Than
    public static final int LE=22;          // Less Expression
    public static final int LQE=23;         // Less or Equal Expression
    public static final int GQT=24;         // Greater or Equal Expression

    // unary boolen exps
    public static final int NO=25;          // Boolean Not

    // Statements, control structures and others
    public static final int VD=26;          // Variable Declaration
    public static final int QN=27;          // Qualified Name
    public static final int L=28;           // Literal
    public static final int SMC=29;         // Static Method Call
    public static final int P=30;           // Parameter
    public static final int PARAMETERS=31;  // Parameters list
    public static final int R=32;           // Return Statement
    public static final int IFT=33;         // If Then Statement
    public static final int IFTE=34;        // If Then Else Statement
    public static final int SCOPE=35;       // Opening and closing a scope
    public static final int END=36;         // End of Program
    public static final int MD=37;          // Method declaration
    public static final int SMCC=38;        // Static Method call closed
    public static final int BR=39;          // Break statement
    public static final int WHI=40;         // While statement
    public static final int FOR=41;         // For statement
    public static final int CONT=42;        // Continue statement
    public static final int DO=43;          // DO-While statement
    public static final int OUTPUT=44;      // Output statement
    public static final int INPUT=54;       // Input statement
    public static final int INPUTTED=55;       // Input statement
    public static final int ERROR=53;       // Error statement

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

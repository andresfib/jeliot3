package jeliot.conan;

/**
 * Contains the information needed to detect 
 * the implicit super method call when interpreting
 * student source code.
 * @author amoreno
 *
 */
public class ConflictiveInheritance {
	//Used in djava.TreeInterpreter. 
	//Thus not valid/meaningful in MCodeInterpreter
	static boolean inImplicitSuperCall = false;
	//Used in djava.TreeInterpreter. 
	//Thus not valid/meaningful in MCodeInterpreter
	static String className;
	
	/**
	 * Sets the superclass the super method belongs to  
	 * @param cN
	 */
	public static void setClassName(String cN){
		className = cN;
	}

	/**
	 * 
	 * @return whether we are processing an implicit 
	 * super method call.
	 */
	public static boolean isInConflictiveImplicit() {
		// TODO Auto-generated method stub
		return inImplicitSuperCall;
	}
	
	/**
	 * Sets whether we have started(true) or finished (false) the implicit
	 * super method call.
	 * @param v
	 */
	public static void setInConflictiveImplicit(boolean v) {
		inImplicitSuperCall = v;
		if (v == true)
			ConAnUtilities.activateConflict("inheritance");
	}
	public static Object getClassName() {
		return className;
	}


}

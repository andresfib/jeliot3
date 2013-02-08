package jeliot.conan;

import java.util.List;

/**
 * Contains the information needed to detect 
 * the implicit super method call when interpreting
 * student source code.
 * @author amoreno
 *
 */
public class ConflictiveImplicit extends Conflict{
	/**
	 * Class that does not call its super class constructor 
	 */
	String className;
	/**
	 * Superclass not "constructed" by className;
	 */
	String superClassName;
	private List parameters;
	public ConflictiveImplicit(String className, String superClassName, int line, List parameters) {
		super(Conflict.IMPLICIT_SUPER,line);
		this.className = className;
		this.superClassName = superClassName;
		this.parameters = parameters;
		// TODO Auto-generated constructor stub
	}
	
	//Used in djava.TreeInterpreter. 
	//Thus not valid/meaningful in MCodeInterpreter
	static boolean inImplicitSuperCall = false;
	//Used in djava.TreeInterpreter. 
	//Thus not valid/meaningful in MCodeInterpreter


	
	public String getClassName() {
		return className;
	}
	public String getSuperClassName() {
		return superClassName;
	}
	
	public boolean isSameConstructor(String method, List parameters){
		return getSuperClassName().equals(method) && this.parameters.equals(parameters);
	}

}

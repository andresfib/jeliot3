package jeliot.conan;

public class ConflictiveOverriding extends Conflict{

	public ConflictiveOverriding() {
		super(Conflict.OVERRIDING);
		inConflictiveOverriding=true;
	}
	public ConflictiveOverriding(int line) {
		super(Conflict.OVERRIDING,line);
		inConflictiveOverriding=true;
	}
	static boolean inConflictiveOverriding=false;
	
	//The class that holds the correct method that 
	//is _not_ executed in the conflictive animation
	String overridenClass ="";
	//The class that holds the method that 
	//is executed in the conflictive animation
	String overridingClass ="";
	
	//The method that has been overriden
	String method;
	
	public void setOverridenMethod(String class1, String class2,
				String methodName) {
		overridenClass = class1;
		overridingClass = class2;
		method = methodName;
	}
	
	public boolean checkOverridenMethod(String className, String methodName){
		
		if(overridingClass.equals(className) && (method.equals(methodName))){
			return true;
		}else return false;
	}
	
	public String getOverridenClass(){
		return overridenClass;
	}
	
	public  void setInConflictiveOverriding(boolean v) {
		inConflictiveOverriding = v;
	//	if (v)
	//		ConAnUtilities.activateConflict("overriding");
	}
	public boolean isInConflictiveOverriding() {
		return inConflictiveOverriding;
	}
	
}

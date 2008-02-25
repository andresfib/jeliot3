package jeliot.conan;

/**
 * Utilities for the creation and retrieval of information related to the 
 * Conflictive Animations
 * @author amoreno
 *
 */
public class ConAnUtilities {
	/*
	 * inheritance tells us whether Jeliot has already animated the
	 * inheritance ConAn, user should press fault button after this. 
	 */
	static boolean inheritance;
	/*
	 * Counts the steps left before it is too late for the user to
	 * indicate there was a ConAn.
	 */
	private static int answerCounter;
	/*
	 * Whether there has been a ConAn.
	 */
	private static boolean conflictActivated;
	
	/**
	 * Set the values for the static fields. 
	 * @see Jeliot.rewind
	 */
	public static void initialize(){
		inheritance = false;
		answerCounter = 0;
		conflictActivated = false;
			
	}
		
	/**
	 * Sets the number of times the user can press the Step button before
	 * his/her detection of the ConAn is done too late, and thus not valid
	 * 
	 * @param i the number of steps
	 */
	public static void setAnswerCounter(int i) {
		answerCounter = i;
	}
	/**
	 * Decrease the answer counter/timer
	 *
	 */
	public static void decreaseAnswerCounter() {
		if (answerCounter > 0) 
		answerCounter--;
	}
	
	/**
	 * Determines whether the user detected the ConAn
	 * 
	 * @return
	 */
	public static boolean isAnswerRight (){
		//answer is right when answered in time
		if (conflictActivated && answerCounter > 0){
			return true;
		} else return false;
	}

	/**
	 * Flags the animation of a ConAn and its type
	 * @param type type or concept of the ConAn
	 */
	public static void activateConflict(String type) {
		conflictActivated = true;
		if(type.equals("inheritance")){
			inheritance = true;
		}
		
	}
}

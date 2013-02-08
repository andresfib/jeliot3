package jeliot.conan;

import java.util.ResourceBundle;

import jeliot.avinteraction.AVInteractionEngine;
import jeliot.util.ResourceBundles;

/**
 * Utilities for the creation and retrieval of information related to the 
 * Conflictive Animations
 * @author amoreno
 *
 */
public class ConAnUtilities {
	
	/**
     * The resource bundle for conan package
     */
    static private ResourceBundle messageBundle = ResourceBundles.getConAnResourceBundle();
            
    
	/*
	 * Counts the steps left before it is too late for the user to
	 * indicate there was a ConAn.
	 */
	private static int answerCounter;
	
	/*
	 * Indicates the times the user has checked the animation thinking there was
	 * a conflict
	 */

	private static int timesChecked;
	/*
	 * Constant holding the maximum number of times a user can check for the conflict,
	 * before getting a warning or rewinding the animation 
	 */
	
	final static public int maxTimesChecked = 3;
	
	//To be made singleton. Represents the conflict in the animation
	private static Conflict conflict = null;


	public static AVInteractionEngine engine;

	//used to de-activate conflictive animations in the review/normal mode
	private static boolean reviewCorrectAnimation;

	
	/**
	 * Set the values for the static fields. 
	 * @see Jeliot.rewind
	 */
	public static void initialize(){
		timesChecked = 0;
		answerCounter = 0;
		conflict = null;
			
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
		if (conflict!=null && answerCounter > 0){
			timesChecked = 0;
			return true;
		} else {
			timesChecked++;
			return false;
		}
	}
	
	public static int getTimesChecked(){
		return timesChecked;
	}
	public static boolean tooManyChecks(){
		return timesChecked >= maxTimesChecked;
	}
	/**
	 * Flags the animation of a ConAn and its type
	 * @param type type or concept of the ConAn
	 */
	//public static void activateConflict(String type) {
	//	conflict = new Conflict(type);
	//}
 
	public static String getMessageWrongAnswer() {
		String msg =""; //$NON-NLS-1$
		if (tooManyChecks()){
			msg = messageBundle.getString("missed")+ 
				messageBundle.getString("rewinding"); 
		} else {
			msg = messageBundle.getString("wrong")+ 
				messageBundle.getString("youhave")+( maxTimesChecked - timesChecked )+ messageBundle.getString("attemptsLeft"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return msg;
	}

	public static Conflict getConflict() {
		// TODO Auto-generated method stub
		return conflict;
	}
	
	public static void setConflict(Conflict c){
		conflict = c;
	}

	public static void setAvInteractionEngine(AVInteractionEngine avinteractionEngine) {
		// TODO Auto-generated method stub
		engine = avinteractionEngine;
		
	}

	public static boolean isReview() {
		
		return reviewCorrectAnimation;
	}

	public static void setReview(boolean b) {
		reviewCorrectAnimation = b;
		
	}
	
	
}

package jeliot.conan;

import javax.swing.*;

import java.util.ResourceBundle;

import jeliot.util.ResourceBundles;

public class ConflictCheck {
	
	static private ResourceBundle messageBundle = ResourceBundles.getConAnResourceBundle();
	static private String topicList = messageBundle.getString("topicList");
	
	public static void check(){		
		Conflict conflict = ConAnUtilities.getConflict();
		//If the type is in the topic list that means we have answers ready for it
		if (topicList.lastIndexOf(conflict.getType())!=-1){
			//The first one is the correct answer
			int[] correct = {1};
			//TODO:
			//We use the Timeschecked as id for the question, change it for something better (MCode instruction?)
			ConAnUtilities.engine.addMCQuestion(""+(ConAnUtilities.getTimesChecked()),""+ConAnUtilities.getTimesChecked(), messageBundle.getString("question"), 
					getAnswers(conflict),new int[0], 1, getComments(conflict), correct, new Integer[0]);
			ConAnUtilities.engine.interaction(""+ConAnUtilities.getTimesChecked());
		}
	}
	
	
	private static String[] getAnswers(Conflict conflict) {
		String[] result = new String[4];
		
		String key = conflict.getType();
		result[0] = messageBundle.getString(key + ".option0");
		result[1] = messageBundle.getString(key + ".option1");
		result[2] = messageBundle.getString(key + ".option2");
		result[3] = messageBundle.getString(key + ".option3");
		return result;
	}
	private static String[] getComments(Conflict conflict) {
		String[] result = new String[4];
		
		String key = conflict.getType();
		result[0] = messageBundle.getString(key + ".comment0");
		result[1] = messageBundle.getString(key + ".comment1");
		result[2] = messageBundle.getString(key + ".comment2");
		result[3] = messageBundle.getString(key + ".comment3");
		return result;
	}
		

}

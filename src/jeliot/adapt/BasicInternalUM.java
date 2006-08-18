package jeliot.adapt;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import jeliot.util.*;
public class BasicInternalUM implements UMInteraction{

	//Right now properties are just variables
	// TODO: To be changed to something more OO
	
	// Number represents number of right questions on the topic 
	public int assignment=0;
	UserProperties internalUM = null;
	//HashMap internalUM = new HashMap();
	public void userLogon(String userName, String password) {
		// TODO Auto-generated method stub
		
	}

	public void userLogin(String userName, String password) {
		internalUM = ResourceBundles.getUserModelConceptsProperties();
	}

	public void userLogout(String userName) {
		
		internalUM.save();

	}

	public void recordEvent(ModelEvent event) {
		// TODO Auto-generated method stub
		Integer[] entries = event.getProgrammingConcepts();
		int result = Integer.parseInt(event.getResult());
		String activity = event.getActivity();
		for (int i=0; i < entries.length; i++){
			if (internalUM.containsKey(entries[i].toString() + activity)){
				result = result + internalUM.getIntegerProperty(entries[i].toString());
			} 
			internalUM.setIntegerProperty(entries[i].toString() + 
					activity, result);
		}
				
	}

	public boolean isConceptKnown(int concept){
		String conceptID = Integer.toString(concept);
		int rightAnswers = (internalUM.containsKey(conceptID+".questions.right"))?
				internalUM.getIntegerProperty(conceptID+".questions.right"):0;
		int wrongAnswers = (internalUM.containsKey(conceptID+".questions.wrong"))?
				internalUM.getIntegerProperty(conceptID+".questions.wrong"):0;
		
		return (rightAnswers > 2) && (rightAnswers - wrongAnswers > 2);
				
	
	}
	public double getConceptKnowledge(String concept, String activity) {
		String property = concept + ".questions.right";
		double result = Double.valueOf(internalUM.getStringProperty(property)).doubleValue();
		return result;
	}
	
}

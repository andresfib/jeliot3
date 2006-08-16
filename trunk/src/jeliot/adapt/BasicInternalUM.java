package jeliot.adapt;

import jeliot.util.*;
public class BasicInternalUM implements UMInteraction{

	//Right now properties are just variables
	// TODO: To be changed to something more OO
	
	// Number represents number of right questions on the topic 
	public int assignment=0;
	UserProperties internalUM = ResourceBundles.getUserModelConceptsProperties();

	public void userLogon(String userName, String password) {
		// TODO Auto-generated method stub
		
	}

	public void userLogin(String userName, String password) {
		// TODO Auto-generated method stub
		
	}

	public void userLogout(String userName) {
		// TODO Auto-generated method stub
		
	}

	public void recordEvent(ModelEvent event) {
		// TODO Auto-generated method stub
		internalUM.setStringProperty(event.getProgrammingConcept()+"."+event.getActivity(),
									 event.getResult());
		
	}

	public double getConceptKnowledge(String concept) {
		String property = concept + ".questions";
		double result = Double.valueOf(internalUM.getStringProperty(property)).doubleValue();
		return result;
	}
	
}

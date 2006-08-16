package jeliot.adapt;

public class ModelEvent {

	String activity;
	String programmingConcept;
	String result;
	
	ModelEvent (String activity, String programmingConcept, String result){
		this.activity = activity;
		
		//TODO check that prog concept is in the list of concepts from the adapt resources
		this.programmingConcept = programmingConcept;
		
		this.result = result;
	}
	
	String getActivity(){
		return activity;
	}
	String getProgrammingConcept(){
		return programmingConcept;
	}
	
	String getResult(){
		return result;
	}
}

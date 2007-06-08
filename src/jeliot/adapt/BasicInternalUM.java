package jeliot.adapt;

import jeliot.mcode.Code;
import jeliot.mcode.MCodeUtilities;
import jeliot.util.ResourceBundles;
import jeliot.util.UserProperties;

import java.util.HashMap;
import java.util.Iterator;
public class BasicInternalUM extends UMInteraction{

	
	double threshold = 0.4;
	String userName;
	UserProperties internalUM = null;
	//HashMap internalUM = new HashMap();
	public void userLogon(String userName, String password) {
		// TODO Auto-generated method stub
		
	}

	public void userLogin(String userName, String password) {
		this.userName = userName;
		internalUM = ResourceBundles.getUserModelConceptsProperties(userName);
	}

	public void userLogout(String userName) {
		
		internalUM.save();

	}

	public void recordEvent(ModelEvent event) {
		// TODO Auto-generated method stub
		Integer[] entries = event.getProgrammingConcepts();
		String result = event.getResult();
		String activity = event.getActivity();
		for (int i=0; i < entries.length; i++){
			String concept = MCodeUtilities.getLongName(entries[i].intValue());
			if (entries[i].intValue() != (Code.QN)){
	            String key =  concept + "." + activity;
	            double temp = modifyKnowledge(activity, key, result);
				System.out.println(key + "="+temp);
				internalUM.setDoubleProperty(key, temp);
				if ((this.getClass()).equals(Adapt2Interaction.class)){
					((Adapt2Interaction)this).sendEvent(activity, concept, result);
				}
			}
		}
				
	}


	public boolean isConceptKnown(int concept){
	
		String conceptID = MCodeUtilities.getLongName(concept);
		double questionPoints = getActivityPoints(conceptID, "question");
		return questionPoints >= threshold;
	
	}
	private double getActivityPoints(String concept, String activity) {
		String property = concept + "." + activity;
		double result = internalUM.getDoubleProperty(property);
		return result;
	}
	
	/**
	 * Updates the internal User Model UserProperties. It overwrites previous values stored!!
	 * 
	 * @param properties Set of properties to replace internalUM with
	 */
	public void updateInternalUM(HashMap properties){
		
		Iterator it = properties.keySet().iterator();
		while (it.hasNext()){
			String key= (String) it.next();
			internalUM.setStringProperty(key,  
					(String) properties.get(key));
		}
		saveUM();
	}
    public void saveUM(){
        internalUM.save();
    }
    
	protected double modifyKnowledge(String activity, String key, String result) {
		double temp = 0;
		if (activity.equals("question")){
			if (internalUM.containsKey(key)){
				temp = internalUM.getDoubleProperty(key);
			}

//			if prob <.5 prob = [ (1-prob)^2 ] / 2
//			else prob = [ (1-prob)^2 ] * weight * result
//			 
//			result - result that you report as "res" parameter
//			weight - weight between Learning Object and Concept (usually 1)
			if (temp <0.5){
				temp = Math.abs(Math.pow(1-temp,2)) / 2;
			} else{
				double value = (result.equals("right"))? 1 : 0;
				temp = Math.abs(Math.pow(1-temp,2)) * value;
			}
			temp += (result.equals("right"))? 0.25 : -0.18;
			if (temp>1.) temp = 1.;
			else if (temp<-1.) temp = -1.;
		}
		return temp;
	}

}

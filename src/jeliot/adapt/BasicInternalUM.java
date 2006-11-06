package jeliot.adapt;

import jeliot.mcode.MCodeUtilities;
import jeliot.util.ResourceBundles;
import jeliot.util.UserProperties;

import java.util.HashMap;
import java.util.Iterator;
public class BasicInternalUM extends UMInteraction{

	//Right now properties are just variables
	// TODO: To be changed to something more OO
	
	
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
		double result = Double.parseDouble(event.getResult());
		String activity = event.getActivity();
		for (int i=0; i < entries.length; i++){
            String key = MCodeUtilities.getLongName(entries[i].intValue()) + "." + activity;
            double temp = result;
			if (internalUM.containsKey(key)){
				temp += internalUM.getDoubleProperty(key);
			}
			
			System.out.println(key + "="+temp);
			internalUM.setDoubleProperty(key, temp);
		}
				
	}

	public boolean isConceptKnown(int concept){
		int threshold = 2;
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
	}
    public void saveUM(){
        internalUM.save();
    }
}

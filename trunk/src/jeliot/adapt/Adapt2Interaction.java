package jeliot.adapt;

import jeliot.networking.*;
public class Adapt2Interaction implements UMInteraction {

	String adapt2UMServer = "http://kt1.exp.sis.pitt.edu:8080/cbum/um";
	String adapt2ReportServer = "http://kt1.exp.sis.pitt.edu:8080/cbum/ReportManager";
	String userID;
	String sessionID;
	String applicationID;
	
	/*
	 * Get the whole "knowledge" report from the ReportManager of adapt2
	 */
	String getReport(){
		String request_url = "http://kt1.exp.sis.pitt.edu:8080/cbum/ReportManager?typ=act&dir=out& frm=xml&app=2&usr=myudelson";
		String response=""; 
		try {
			response = NetworkUtils.getContent(request_url);
		} catch (Exception e) {
			// TODO Retry once if it didn't go well
			e.printStackTrace();
		}
		return response;
	}
	
	/* (non-Javadoc)
	 * @see jeliot.adapt.UMInteraction#getConceptKnowledge(java.lang.String)
	 */
	public double getConceptKnowledge(String concept) {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see jeliot.adapt.UMInteraction#recordEvent(jeliot.adapt.ModelEvent)
	 */
	public void recordEvent(ModelEvent event) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see jeliot.adapt.UMInteraction#userLogin(java.lang.String, java.lang.String)
	 */
	public void userLogin(String userName, String password) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see jeliot.adapt.UMInteraction#userLogon(java.lang.String, java.lang.String)
	 */
	public void userLogon(String userName, String password) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see jeliot.adapt.UMInteraction#userLogout(java.lang.String)
	 */
	public void userLogout(String userName) {
		// TODO Auto-generated method stub
		
	}

	
}

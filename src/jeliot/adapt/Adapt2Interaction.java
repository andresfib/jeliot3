package jeliot.adapt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import jeliot.networking.NetworkUtils;
import edu.pitt.sis.paws.cbum.report.ProgressEstimatorReport;

public class Adapt2Interaction extends BasicInternalUM {

	String adapt2UMServer = "http://kt1.exp.sis.pitt.edu:8080/cbum/um";
	String adapt2ReportServer = "http://kt1.exp.sis.pitt.edu:8080/cbum/ReportManager";
	String userID;
	String password;
	String group; //Group identifier for the students like "ViSCoSJava2006"
	String sessionID;
	String applicationID = "10"; //Fixed application ID for Jeliot in Adapt2
	String eventURL; //Personalized url to submit events
	
	public Adapt2Interaction(String userID, String password, String group, String sessionID){
		this.userID = userID;
		this.password = password;
		this.group = group;
		this.sessionID = "testAndres";//sessionID;
		this.eventURL = adapt2UMServer + "?usr=" + userID 
		+ "&sid=" + sessionID + "&grp=" + group
		+ "&svc=&app=" + applicationID;

	}
	
	/*
	 * Get the whole "knowledge" report from the ReportManager of adapt2
	 */
	private ArrayList getReport(){
		
		String reportURL = adapt2UMServer + "?typ=act&dir=in&frm=dat&app=" + 
      applicationID + "&usr=" + userID;
		ArrayList response=null; 
		try {
			response = NetworkUtils.getReport(reportURL);
		} catch (Exception e) {
			// TODO Retry once if it didn't go well
			System.out.println("Failed to get the Report from ADAPT2 server");
			e.printStackTrace();
		}
		return response;
	}
	
	/* (non-Javadoc)
	 * @see jeliot.adapt.UMInteraction#getConceptKnowledge(java.lang.String)
	 */
	public double getConceptKnowledge(String concept, String activity) {
		return super.getConceptKnowledge(concept, activity);
	}

	/* (non-Javadoc)
	 * @see jeliot.adapt.UMInteraction#recordEvent(jeliot.adapt.ModelEvent)
	 */
	public void recordEvent(ModelEvent event) {
		// TODO Auto-generated method stub
		Integer[] entries = event.getProgrammingConcepts();
		int result = Integer.parseInt(event.getResult());
		String activity = event.getActivity();
		
		for (int i=0; i < entries.length; i++){
			String key = entries[i].toString() + "." + activity;
			String activityURL = eventURL + "&act=" + key;
			int temp = result;
			if (internalUM.containsKey(key)){
				int previous = internalUM.getIntegerProperty(key);
				temp += previous; 		

			} 
			internalUM.setIntegerProperty(key, temp);
			
			try{
//				NetworkUtils.postContent(activityURL+ "&res=" + result);
				System.out.println(activityURL+"&res=" + result);
			} catch (Exception e){
				System.out.println("Failed to update the ADAPT2 server with activity");
				e.printStackTrace();
			}
		}
	
	}

	/* (non-Javadoc)
	 * @see jeliot.adapt.UMInteraction#userLogin(java.lang.String, java.lang.String)
	 */
	public void userLogin(String userID, String password) {
		this.userID = userID;
		this.password = password;
		super.userLogin(userID, password);
		
		ArrayList report = getReport();
		HashMap userModel = reportToMap(report);
		updateInternalUM(userModel);

	}



	private HashMap reportToMap (ArrayList report) {
		
		Iterator it = report.iterator();
		HashMap reportMap = new HashMap();
		while (it.hasNext()){
			ProgressEstimatorReport conceptReport = (ProgressEstimatorReport)it.next();
			reportMap.put(conceptReport.id, new Double(conceptReport.progress));
			
		}
		return reportMap;
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

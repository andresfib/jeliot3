package jeliot.adapt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import jeliot.mcode.Code;
import jeliot.mcode.ConceptVectors;
import jeliot.mcode.MCodeUtilities;
import jeliot.networking.NetworkUtils;
import edu.pitt.sis.paws.cbum.report.ProgressEstimatorReport;

public class Adapt2Interaction extends BasicInternalUM {

	String adapt2UMServer = "http://kt1.exp.sis.pitt.edu:8080/cbum/um";
	String adapt2ReportServer = "http://kt1.exp.sis.pitt.edu:8080/cbum/ReportManager";
	String userID;
	String password;
	String group; //Group identifier for the students like "ViSCoSJava2006"
	String sessionID;
	String applicationID = "17"; //Fixed application ID for Jeliot in Adapt2
	String eventURL; //Personalized url to submit events

	double threshold = 0.65;
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
			
			String reportURL = adapt2ReportServer + "?typ=act&dir=out&frm=dat&app=" + 
	      applicationID + "&usr=" + userID + "&grp=" + group;
	        System.out.println(reportURL);
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
//	public double getActivity(String concept, String activity) {
//		return super.getConceptKnowledge(concept, activity);
//	}

       public void sendEvent(String activity, String concept, String result){
   		String activityURL = eventURL + "&act=" + activity +"&sub="+ concept;
   		result = (activity.equals("question") && result.equals("right"))?"1":"0";
   		System.out.println(activityURL+"&res=" + result);		
		try{
			NetworkUtils.postContent(activityURL+ "&res=" + result);
		} catch (Exception e){
			System.out.println("Failed to update the ADAPT2 server with activity");
			e.printStackTrace();
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
//		while (it.hasNext()){
//			ProgressEstimatorReport activityReport = (ProgressEstimatorReport)it.next();
//            String activity = activityReport.id;
//            ArrayList subactivities = activityReport.subs;
//            Iterator itActivities = subactivities.iterator();
//            while(itActivities.hasNext()){
//                ProgressEstimatorReport conceptReport = (ProgressEstimatorReport)itActivities.next();
//                String concept = getCode(conceptReport.id, activity);
//                reportMap.put(concept, new Double(conceptReport.progress));
//            }
//			
//		}
//		return reportMap;
		String activity = "question";
		while (it.hasNext()){
			    ProgressEstimatorReport conceptReport = (ProgressEstimatorReport)it.next();
                String concept = conceptReport.id + "." + activity ; //getCode(conceptReport.id, activity);
                reportMap.put(concept, "" + conceptReport.progress);
		}
			
		
		return reportMap;

	}

	/* (non-Javadoc)
	 * @see jeliot.adapt.UMInteraction#userLogon(java.lang.String, java.lang.String)
	 */
	public void userLogon(String userName, String password) {
		// TODO Auto-generated method stub
		
	}
	
	protected double modifyKnowledge(String activity, String key, String result) {
		double temp = 0;
		if (activity.equals("question")){
			if (internalUM.containsKey(key)){
				temp = internalUM.getDoubleProperty(key);
			}
			temp += (result.equals("right"))? 0.25 : -0.18;
			if (temp>1.) temp = 1.;
			else if (temp<-1.) temp = -1.;
		}
		return temp;
	}

	/* (non-Javadoc)
	 * @see jeliot.adapt.UMInteraction#userLogout(java.lang.String)
	 */
	public void userLogout(String userName) {
		// TODO Auto-generated method stub
		
	}
//    assignments                 assign
//    % operator                  mod
//    PreIncrement operator           pre_inc
//    the PostIncrement Operator      post_inc
//    the predecrement operator       pre_dec
//    the post decrement operator     post_dec
//    and array creation          array
//    an element access of an array       array_elem
//    an object (non-static) method call  obj_method_call
//    a static method call            static_method_call
//    the return statement of method  return
//    the argument passed to a method arg_pass
//    an addition                 add
//    a substraction              sub
//	private String getCode(String id, String activity){
//        activity = activity + ".";
//         if (id.equals("assign")){
//             return activity + Code.A;
//         }
//         if (id.equals("mod")){
//             return activity + Code.ME;
//         }
//         if (id.equals("pre_inc")){
//             return activity + Code.PRIE;
//         }
//         if (id.equals("post_inc")){
//             return activity + Code.PIE;
//         }
//         if (id.equals("pre_dec")){
//             return activity + Code.PRDE;
//         }
//         if (id.equals("post_dec")){
//             return activity + Code.PDE;
//         }
//         if (id.equals("array")){
//             return activity + Code.AA;
//         }
//         if (id.equals("array_elem")){
//             return activity + Code.AAC;
//         }
//         if (id.equals("obj_method_call")){
//             return activity + Code.OMC;
//         }
//         if (id.equals("static_method_call")){
//             return activity + Code.SMC;
//         }
//         if (id.equals("return")){
//             return activity + Code.R;
//         }
//         if (id.equals("arg_pass")){
//             return activity + Code.PARAMETERS;
//         }
//         if (id.equals("add")){
//             return activity + Code.AE;
//         }
//         if (id.equals("sub")){
//             return activity + Code.SE;
//         }
//         else{
//             return activity + "unknownConcept";
//         }
//        if (id.equals("Assignment")){
//            return activity + Code.A;
//        }
//        if (id.equals("Remainder")){
//            return activity + Code.ME;
//        }
//        if (id.equals("Preincrement")){
//            return activity + Code.PRIE;
//        }
//        if (id.equals("Postincrement")){
//            return activity + Code.PIE;
//        }
//        if (id.equals("Predecrement")){
//            return activity + Code.PRDE;
//        }
//        if (id.equals("Postdecrement")){
//            return activity + Code.PDE;
//        }
//        if (id.equals("ArrayAllocation")){
//            return activity + Code.AA;
//        }
//        if (id.equals("ArrayAccess")){
//            return activity + Code.AAC;
//        }
//        if (id.equals("ObjectMethodCall")){
//            return activity + Code.OMC;
//        }
//        if (id.equals("StaticMethodCall")){
//            return activity + Code.SMC;
//        }
//        if (id.equals("MethodReturn")){
//            return activity + Code.R;
//        }
//        if (id.equals("Argument")){
//            return activity + Code.PARAMETERS;
//        }
//        if (id.equals("Addition")){
//            return activity + Code.AE;
//        }
//        if (id.equals("Substraction")){
//            return activity + Code.SE;
//        }
//        else{
//            return activity + "unknownConcept";
//        }
//
//    }
}

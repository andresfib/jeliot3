package jeliot.adapt;

public class UMInteraction {
	
    public UMInteraction(){}
    
    public UMInteraction createUserModel(String type,String userName,String password, String
            group, String sessionID){
        UMInteraction userModel;
        if (type.equals("basic")){
            userModel = new BasicInternalUM();
        } else if (type.equals("adapt2")){
            userModel = new Adapt2Interaction(userName, password, group, sessionID);
        } else {
            userModel = new DummyUM();
        }
        return userModel;
    }
	/*
	 * Register user for the first time in the UM system
	 * and starts a session
	 */
     public void  userLogon(String userName, String password){}
	
	/*
	 * Logins previously registered user in the system and 
	 * starts a session 
	 */
     public void userLogin(String userName, String password){}
	
	/*
	 * Logs out a user from the system
	 */
     public void userLogout(String userName){}
	
	/*
	 * Submits the event to the user model
	 */
     public void recordEvent(ModelEvent event){}
	
	/*
	 * Queries the UM for the knowledge of one user in the
	 * specified concept
	 */
     public boolean isConceptKnown(int conceptID){
        return false;
    }

     public void saveUM(){
      
     }
 
}

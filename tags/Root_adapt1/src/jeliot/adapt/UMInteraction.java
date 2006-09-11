package jeliot.adapt;

public interface UMInteraction {
	
	/*
	 * Register user for the first time in the UM system
	 * and starts a session
	 */
	void userLogon(String userName, String password);
	
	/*
	 * Logins previously registered user in the system and 
	 * starts a session 
	 */
	void userLogin(String userName, String password);
	
	/*
	 * Logs out a user from the system
	 */
	void userLogout(String userName);
	
	/*
	 * Submits the event to the user model
	 */
	void recordEvent(ModelEvent event);	
	
	/*
	 * Queries the UM for the knowledge of one user in the
	 * specified concept
	 */
	boolean isConceptKnown(int conceptID);
	
}

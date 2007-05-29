package jeliot.adapt;

public class DummyUM extends UMInteraction {

	private static UMInteraction userModel;
	
	protected DummyUM(){
		
	}
	
    public boolean isConceptKnown(int conceptID) {
        // TODO Auto-generated method stub
        return false;
    }

    public void recordEvent(ModelEvent event) {
        // TODO Auto-generated method stub

    }

    public void userLogin(String userName, String password) {
        // TODO Auto-generated method stub

    }

    public void userLogon(String userName, String password) {
        // TODO Auto-generated method stub

    }

    public void userLogout(String userName) {
        // TODO Auto-generated method stub

    }

}

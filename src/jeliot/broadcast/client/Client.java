package jeliot.broadcast.client;
/**
 *
 * @author Carlos Menéndez
 */

public class Client {
    
    //Class ClientServ for every client
    private static ClientServ c = null;
    //data sender thread
    public static CliSendDataThread clientSendData = null;
    
    //data receiver thread
    public static CliReceiveDataThread clientReceiveData = null;
    
    //CliMessages
    public CliMessages message = null;
    
    //private TestWindow window = null;
    
    public void initializeClient() {
        this.message = new CliMessages();
        this.c = new ClientServ(this);
        this.clientSendData = new CliSendDataThread(this, this.c, this.message);
        this.clientReceiveData = new CliReceiveDataThread(this, this.c, this.message);
        
        //this.window = new TestWindow();

        clientSendData.start();
        clientReceiveData.start();
    }
    
    public Client(){
        initializeClient();
    }
    
}

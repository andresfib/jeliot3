package jeliot.broadcast.server;

public class Server {
    
    // attributes
    
    // array of clients
    //private static ServClient t = null;
    private ConnectionTable connectionTable = null;
    
    // connection listener thread
    private static ServConnectionThread serverConnection = null;
    
    // data sender thread
    public static ServSendDataThread serverSendData = null;
    
    //ServMessages
    private ServMessages message = null;
    
    // method to initializate server
    public void initializeServer(){
        this.message = new ServMessages();
        this.connectionTable = new ConnectionTable(this, this.message);
        this.serverConnection = new ServConnectionThread(this, this.connectionTable, this.message);
        this.serverSendData = new ServSendDataThread(this, this.connectionTable, this.message);

        serverSendData.start();
        serverConnection.start();

        
    }
    
    public void closeConnection(){
        System.out.println("Close  in Server servConnection ");
        this.serverConnection.exitConnection();
        System.out.println("Closed all ");
    }
    
    public void closeClient(Integer idClient){
        this.connectionTable.removeConnection(idClient);
        System.out.println("Closed Client ");
    }

    public Server (){
        initializeServer();
    }
}

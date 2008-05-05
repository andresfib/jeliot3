package jeliot.broadcast.server;

import java.io.*;
import java.net.*;



public class ServConnectionThread extends Thread{
     
     // connection port
     public static final int PORT=5011;
     private ServerSocket serv = null;
     private Socket clientSocket;
     private Server server = null;
     public static int numClients;
     private ServMessages message = null;
     public boolean flagExit = true;
     private ConnectionTable connectionTable = null;
     
    /** Creates a new instance of servidor */
    public ServConnectionThread(Server server, ConnectionTable connectionTable, ServMessages message) 
    {
        try {
            this.serv = new ServerSocket(PORT);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        this.server = server;
        this.numClients = 0;
        this.message = message;
        this.connectionTable = connectionTable;
    }
    
    /*
     * This method is for reply the first request of each new client connected
     */
     public void run(){
         int id = 0;
         
        while(flagExit){

             try {
                
                System.out.println("Waiting connexion");
		clientSocket = serv.accept();
                System.out.println("Connexion accept");
                id = connectionTable.addConnection(clientSocket);

                numClients++;

                System.out.println("Number of clients: " + numClients);
                
	    }
	    catch (IOException e) {
		System.out.println(e);}           

	}//End While
         
     }//End run
     
     public void exitConnection(){
        try {
            System.out.println("Closing servConnectionThread");
            flagExit = false;
            clientSocket.close();
            serv.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}//End Thread


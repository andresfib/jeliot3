package jeliot.broadcast.server;

import java.io.*;
import java.net.*;
import java.util.*;

public class ServSendDataThread extends Thread{
    
    //Attributes
    private ConnectionTable connectionTable = null;
    private BufferedReader inputline = null;
    private Server server = null;
    private ServMessages message = null;
    public boolean flagExit = false;
    public boolean priorityConceded = false;

    /* Creates a new instance of ServSendDataThread */
    public ServSendDataThread(Server server, ConnectionTable connectionTable, ServMessages message) {
        this.inputline = new BufferedReader(new InputStreamReader(System.in));
        this.connectionTable = connectionTable;
        this.server = server;
        this.message = message;

    }
    
    public void run(){
        String text = null;

        while(true){
            //Read line from input
            try {
                text = inputline.readLine();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        
            if(text != null){
                if((text.indexOf("Exit") != -1)){
                    Boolean var = connectionTable.closeAllConnections();
                    if(var == true){
                        break; 
                    }
                }

                //Broadcasting sendToClient to every client
                if(text.length() != 0){
                    Broadcast(text);
                }
            }
        }//End While
        
        try {
        System.out.println("Closing servSendDataThread");
        inputline.close();
        server.closeConnection();

        } catch (IOException ex) {
            ex.printStackTrace();
        } 

    }//End run
    
    public void Broadcast(String text){
        Iterator it = connectionTable.connectionIds.iterator();
        while(it.hasNext()){
            ServClient client = (ServClient) connectionTable.connections.get((Integer) it.next());
            client.sendToClient(message.getInstruction(client.idClient, text));
            System.out.println("Data sent--> " + text + "\n");
        }
    }
    
}//End Thread


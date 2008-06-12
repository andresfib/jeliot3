package jeliot.broadcast.server;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServSendDataThread extends Thread{
    
    //Attributes
    private ConnectionTable connectionTable = null;
    public DataInputStream inputline = null;
    private Server server = null;
    private ServMessages message = null;
    public boolean flagExit = true;
    public boolean priorityConceded = false;
    public String text = null;
    public String programCode = null;
    public Object ob = null;

    /* Creates a new instance of ServSendDataThread */
    public ServSendDataThread(Server server, ConnectionTable connectionTable, ServMessages message) {
        this.inputline = new DataInputStream(System.in);
        this.connectionTable = connectionTable;
        this.server = server;
        this.message = message;

    }
    
    public void run(){

        while(flagExit){

            if(text == null){
                flagExit = sleepThread();
            }
            
            if((text.indexOf("Exit") != -1)){
                Boolean var = connectionTable.closeAllConnections();
                if(var == true){
                    break; 
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
    
    public void setMCode(String str){
        
            text = new String(str);
            System.out.println("Codigo " + str);
            if(text.length() != 0){
                Broadcast(text);
            }
    }
    
    public void getProgramCode(String str){
        
        programCode = new String(str);
        Iterator it = connectionTable.connectionIds.iterator();
        while(it.hasNext()){
            ServClient client = (ServClient) connectionTable.connections.get((Integer) it.next());
            client.sendToClient(message.getProgram(client.idClient, text));
            System.out.println("Data sent--> " + text + "\n");
        }
    }
    
       public synchronized void wakeupThread()
       {
          this.notify();
       }

       public synchronized Boolean sleepThread(){
            if (flagExit) {
                try {
                    this.wait();
                } catch (InterruptedException ex) {
                    Logger.getLogger(ServSendDataThread.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            return false;
       }
    
}//End Thread


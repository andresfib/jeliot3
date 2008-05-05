package jeliot.broadcast.server;

import java.io.*;
import java.net.*;

public class ServReceiveDataThread extends Thread{
    
    // Attributes
    private ServClient t = null;
    private Server server = null;
    private ServMessages message = null;
    private String [] text = null;
    public boolean flagExit = true;
       
    public ServReceiveDataThread(ServClient t, Server server) {
        this.t = t;
        this.server = server;
        this.message = new ServMessages();
    }
    
    public void run(){
    String str = null;

    while(flagExit){
        
            System.out.println("Waiting for an answer from client " + t.idClient);
            try {
                str = t.printFromClient();
            } catch(Exception e) {
                System.out.println("Client " + t.idClient + " broke");
                e.printStackTrace();
            }
            text = str.split("§");

            //Exit condition Server
            if((text[0].indexOf("EXIT") != -1)){
                if (this.t!=null){
                    System.out.println("Closing client " + t.idClient + " in servReceiveDataThread\n"); 
                    flagExit = false;
                }
            }
            if((text[0].indexOf("CLOSE") != -1)){
                if (this.t!=null){
                    System.out.println("Closing client " + t.idClient + "\n");
                    server.closeClient((Integer) t.idClient);
                    flagExit = false;
                    break;
                }
            }
            //Hello condition Server
            if((text[0].indexOf("HELLO") != -1)){
                System.out.println("Hello from Client " + text[1] + " --> " + text[3]);
            }
            //Ack condition Server
            if((text[0].indexOf("ACK") != -1)){
                System.out.println("ACK from Client " + text[1] + " --> " + text[3]);
            }
            
            //Instruction condition Server
            if((text[0].indexOf("INST") != -1)){
                if(t.priority == false)
                    System.out.println("Data from Client " + text[1] + " --> " + text[3]);
                else if (t.priority == true){
                    server.serverSendData.Broadcast(text[3]);
                }
            }
            
            //Instruction condition Server
            if((text[0].indexOf("PRI") != -1)){
                t.priority = Priority();
            }
        }//End While

        System.out.println("Closing servReceiveDataThread");
    }//End run
    
    public boolean Priority(){
        String answer = null;
        System.out.println("Set priority to Client " + text[1] + "?");

        boolean var = true;

        if(server.serverSendData.priorityConceded == true){
            System.out.println("Priority for Client " + text[1] + " reject!!\n");
            t.sendToClient(message.getPriorityNack(t.idClient, "NACK"));
            var = false;
        }
        else if(server.serverSendData.priorityConceded == false){
            System.out.println("Priority for Client " + text[1] + " accept!!\n");
            t.sendToClient(message.getPriorityAck(t.idClient, "ACK"));
            server.serverSendData.priorityConceded = true;
            var = true;
        }
        return var;
    }
    
}//End Thread

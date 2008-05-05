package jeliot.broadcast.client;

import java.io.*;
import java.net.*;

public class CliReceiveDataThread extends Thread{
    
    //Atributes
    private ClientServ c = null;
    private Client client = null;
    private CliMessages message = null;
    private String [] text = null;
    public boolean flagExit = true;
    
    public CliReceiveDataThread(Client client, ClientServ c, CliMessages message) {
        this.c = c;
        this.client = client;
        this.message = message;
    }
    
     public void run(){
        String str = null;
        
        while(flagExit){
            
            //Read from Server
                if (c!=null)
                    str = c.printFromServer(); 
                    text = str.split("�");
                //Exit condition Client
                if((text[0].indexOf("EXIT") != -1)){
                    System.out.println("Type -Exit- to exit the application");
                    flagExit = false;
                }
                if((text[0].indexOf("CLOSE") != -1)){
                    flagExit = false;
                }
                
                if((text[0].indexOf("PRIACK") != -1) || (text[0].indexOf("PRINACK") != -1)){
                    c.priority = Priority(text[0]);
                }

                //Intruction from Server
                if((text[0].indexOf("INST") != -1)){
                    System.out.println("Instruction from " + text[1] + " --> " + text[3]);
                    c.sendToServer(message.getAck(c.idClient, "SERVER", "Ack Client " + c.idClient));
                }    
        }//End While
        if((text[0].indexOf("CLOSE") != -1)){
            System.out.println("Closing cliReceiveDataThread\n");
            c.closeConnection();
        }
        else{
            System.out.println("Closing cliReceiveDataThread\n");
        }
    }//End Run
     
     public boolean Priority(String answer){
        boolean var = false;
        
        if((answer.indexOf("PRINACK") != -1)){
            System.out.println("Priority reject!! ");
            var = false;
        }
        else if((answer.indexOf("PRIACK") != -1)){
            System.out.println("Priority Conceded!! ");
            var = true;
        }
        return var;
    }
    
}//End Thread

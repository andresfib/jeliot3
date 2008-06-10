package jeliot.broadcast.client;
/**
 *
 * @author Carlos Menéndez
 */
import java.io.*;
import java.net.*;

public class CliReceiveDataThread extends Thread{
    
    //Atributes
    private ClientServ c = null;
    private Client client = null;
    private CliMessages message = null;
    private String [] text = null;
    public boolean flagExit = true;
    public String programCode = null;
    
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
                    text = str.split("§");
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
                
                //if header says start then we have to put Jeliot Client in play mode 
                
                //Intruction from Server
                if((text[0].indexOf("INST") != -1)){
                    //Send the rest of the line to buffer that connects with readline in TheaterMCodeInterpreter
                    System.out.println("Instruction from " + text[1] + " --> " + text[3]);
                    //c.sendToServer(message.getAck(c.idClient, "SERVER", "Ack Client " + c.idClient));
                } 
                //Intruction from Server
                if((text[0].indexOf("PROG") != -1)){
                    System.out.println("Instruction from " + text[1] + " --> " + text[3]);
                    //c.sendToServer(message.getAck(c.idClient, "SERVER", "Ack Client " + c.idClient));
                    programCode = new String(text[3]);
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
     
    public String sendMCode(){
        String str = null;
        if(text[3].indexOf(null) != -1){
            return "";
        }
        else{
            str = new String(text[3]);
            System.out.println(str);
            return str;
        }
    }
    
}//End Thread

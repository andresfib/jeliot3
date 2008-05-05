package jeliot.broadcast.client;

import java.io.*;
import java.net.*;

public class CliSendDataThread extends Thread{

    //Atributes
    private ClientServ c = null;
    public BufferedReader inputline = null;
    private Client client = null;
    private CliMessages message = null;
    public boolean flagExit = true;
    public String text = null;
    
    public CliSendDataThread(Client client, ClientServ c, CliMessages message) {
        this.inputline = new BufferedReader(new InputStreamReader(System.in));
        this.c = c;
        this.client = client;
        this.message = message;
    }
    
    public void run(){
        
        while(flagExit){
            
            System.out.println("Introduce Data-->");
            //Read line from input
            try {
                text = inputline.readLine();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if(text != null){
                //Exit condition Clients
                if((text.indexOf("Exit") != -1)){
                    flagExit = false;    
                    break;
                }

                //Close condition for one Client
                if((text.indexOf("Close") != -1)){
                    c.sendToServer(message.getClose(c.idClient, "SERVER", text));
                    flagExit = false;    
                    break;
                }

                //Priority condition for one Client
                if((text.indexOf("Priority") != -1)){
                    c.sendToServer(message.getPriority(c.idClient, "SERVER", text));
                }

                if(text.length() != 0){
                    c.sendToServer(message.getInstruction(c.idClient, "SERVER", text, c.priority));
                    System.out.println("Data sent to Server--> " + text);
                }
            }
            
        }//End While

        if((text.indexOf("Close") != -1)){
            try {
                System.out.println("Closing cliSendDataThread");
                inputline.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        else{
           try {
                System.out.println("Closing cliSendDataThread");
                inputline.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            c.closeConnection();
        }
         
    }//End Run
    
}//End Thread

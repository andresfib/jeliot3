package jeliot.broadcast.server;

import java.io.*;
import java.net.*;

public class ServClient {
    //Attributes
    private DataInputStream is = null;
    private DataOutputStream os = null;
    private Socket clientSocket = null;
    private String textFromClient = null;
    //data receiver thread for every client
    public ServReceiveDataThread serverReceiveData = null;
    private Server server = null;
    public int idClient = 0;
    public boolean priority = false;
       
    public ServClient(Server server, Socket clientSocket){
        
        try {
            this.os = new DataOutputStream(clientSocket.getOutputStream());
            this.is = new DataInputStream(clientSocket.getInputStream());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
	this.clientSocket = clientSocket;
        this.server = server;
        
        this.serverReceiveData = new ServReceiveDataThread(this, this.server);
        
        serverReceiveData.start();
        
    }
    
    public String printFromClient(){

            try {
                textFromClient = is.readUTF();
                //System.out.println("Data received --> " + textFromClient);
                    
            } catch (IOException ex) {
                System.out.println(this.idClient);
                ex.printStackTrace();
            }
        return textFromClient;
    }
    
    //Method sendToClient
    public void sendToClient (String message){
        
        try {
            this.os.writeUTF(message);
            
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        //System.out.println("Data sent--> " + message + "/n");
    }
    
       
    //Method for close Data Streams and close clientSocket
    public void closeConnection(){
        try {
            System.out.println("Close servClient Buffers ");
            os.close();
            is.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }   
    }  
}


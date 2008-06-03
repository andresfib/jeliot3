package jeliot.broadcast.client;
/**
 *
 * @author Carlos Menéndez
 */
import java.io.*;
import java.net.*;
import java.util.StringTokenizer;

public class ClientServ {
    
    public static final String [] arrayHosts = {"localhost","192.168.0.1","192.168.30.4"};
    public static String HOST = null;
    public static final int PORT = 5011;
    private Socket cli = null;
    private Client client = null;
    private DataOutputStream os = null;
    private DataInputStream is = null;
    private DataInputStream inputLine = null;
    private String textFromServer, textToServer = null;
    private String [] text = null;
    public static int idClient;
    public static boolean priority;
    
    public ClientServ(Client client) {
        //Bucle for choose the ip address to connect from a list
        /*for(int i=0; i<arrayHosts.length; i++){
            HOST = arrayHosts[i];
            try{
             this.cli = new Socket(HOST, PORT);
            }
            catch(Exception e){
                System.out.println(e);
            }
        }*/
        try{
             this.cli = new Socket("127.0.0.1", PORT);
        }
        catch(Exception e){
            System.out.println(e);
        }
        System.out.println("Socket Address connection: " + this.cli.getInetAddress());
        
        this.client = client;

        try {          
            this.is = new DataInputStream(cli.getInputStream());
            this.os = new DataOutputStream (cli.getOutputStream());
            this.inputLine = new DataInputStream(new BufferedInputStream(System.in));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        //Getting the id from the server
        this.idClient = getId();

    }
    
    public int getId(){
        int id = 4;
        boolean flag = true;

        while(flag){
            String str = null;
            try {
                str = is.readUTF();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            text = str.split("§");
            
            if((text[0].indexOf("HELLO") != -1)){
                id = Integer.parseInt(text[3]); //We convert into int the id received
                System.out.println("Client Id: " + id);
                this.idClient = id;
                sendToServer(client.message.getHello(idClient, "SERVER", "Hello Server!! I'm Client " + idClient));
                flag = false;
            }     
        }
        return id;
    }
    
    public String printFromServer(){

            try {
                    textFromServer = is.readUTF();
                    //System.out.println("MCode: " + textFromServer);
                }
             catch (IOException ex) {
                ex.printStackTrace();
            }
        return textFromServer;
    }
    
    public void sendToServer (String textToServer){

        try {
            os.writeUTF(textToServer);
            
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        //System.out.println("Data sent to Server--> " + textToServer);
    }
    
    
    //close Data Streams and close socket
    public void closeConnection(){
        try {
            System.out.println("Close client_serv");
            sendToServer(client.message.getExit(idClient, "SERVER", "Exit"));
            os.close();
            is.close();
            inputLine.close();
            cli.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }   
    }
    
}


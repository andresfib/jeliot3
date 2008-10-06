package jeliot.broadcast.client;
/**
 *
 * @author Carlos Menéndez
 */
import java.io.*;
import java.net.*;
import java.nio.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CliReceiveDataThread extends Thread{
    
    //Atributes
    private ClientServ c = null;
    private Client client = null;
    private CliMessages message = null;
    public String [] text = null;
    public String data = "";
    public boolean flagExit = true;
    public String programCode = null;
    public File fileMCode = new File ("f:\\fileMCode.txt");
    public StringBuffer bufferMCode = new StringBuffer();
    public PipedOutputStream pouts = new PipedOutputStream();
    public int len = 0;
    
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
                    client.clientSendData.wakeupThread();
                    flagExit = false;
                }
                if((text[0].indexOf("CLOSE") != -1)){
                    client.clientSendData.wakeupThread();
                    flagExit = false;
                }
                
                if((text[0].indexOf("PRIACK") != -1) || (text[0].indexOf("PRINACK") != -1)){
                    c.priority = Priority(text[0]);
                }
                
                //if header says start then we have to put Jeliot Client in play mode 
                
                //Intruction from Server
                if((text[0].indexOf("INST") != -1)){
                    //Send the rest of the line to buffer that connects with readline in TheaterMCodeInterpreter
                    //System.out.println("Instruction from " + text[1] + " --> " + text[3]);
                    bufferMCode.delete(0, bufferMCode.capacity());
                    this.data = new String(this.text[3]);
                   // mCodeToFile(data, fileMCode);
                    bufferMCode.append(data);
                    
                    len = data.length();
                    try {
                        pouts.write(data.getBytes(), 0, len);
                    } catch (IOException ex) {
                        Logger.getLogger(CliReceiveDataThread.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                    //System.out.println("Reader from buffer: " + bufferMCode.substring(0));
                    //c.sendToServer(message.getAck(c.idClient, "SERVER", "Ack Client " + c.idClient));
                }
                    //fileErase(fileMCode);
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
     
    //public MCodeToPiped (String MCode){
     
public void fileErase(File file){
    if(file.delete()){
        System.out.println("File erased\n");
        try {
            file.createNewFile();
        } catch (IOException ex) {
            Logger.getLogger(CliReceiveDataThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    else   
        System.out.println("File cannot be erased\n");
} 

public boolean fileExists(File file){
    if (file.exists()){
        System.out.println("The file " + file + " exists\n");
        return true;
    }
    else{
        System.out.println("The file " + file + " doesn't exists!!!\n");
    }
    return false;
}
     
public String fileToMCode(File file){
    StringBuffer buffer = new StringBuffer();
    String line;
    FileReader fReader;
    BufferedReader bReader;

    try {
          fReader = new FileReader(file);
          bReader = new BufferedReader(fReader);
          while ((line = bReader.readLine()) != null){
            buffer.append(line);
          }
          bReader.close();
          fReader.close();
        } catch (FileNotFoundException e) {
          e.printStackTrace();
        } catch (IOException e) {
          e.printStackTrace();
        }

    return buffer.toString();
  }

public void mCodeToFile(String string, File file){
    FileWriter fWriter;
    BufferedWriter bWriter;

    try {
      fWriter = new FileWriter(file);
      bWriter = new BufferedWriter(fWriter);
      bWriter.write(string);
      bWriter.close();
      fWriter.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}//End Thread

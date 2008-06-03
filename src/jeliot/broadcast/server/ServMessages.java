package jeliot.broadcast.server;
/**
 *
 * @author Carlos Menéndez
 */
import java.io.*;
import java.net.*;

public class ServMessages {
    
    //Attributes
    private final static String hello = "HELLO";
    private final static String ack = "ACK";
    private final static String nack = "NACK";
    private final static String instruction = "INST";
    private final static String priorityAck = "PRIACK";
    private final static String priorityNack = "PRINACK";
    private final static String close = "CLOSE";
    private final static String exit = "EXIT";
    private final static String server = "SERVER";
    
    public ServMessages() {
        
    }
    
    public String getHello(int to, String text){
        String str = new String(hello + "§" + server + "§"+ to + "§" + text + "§");
        return str;
    }
    public String getAck(int to, String text){
        String str = new String(ack + "§" + server + "§"+ to + "§" + text + "§");
        return str;
    }
    public String getNack(int to, String text){
        String str = new String(nack + "§" + server + "§"+ to + "§" + text + "§");
        return str;
    }
    public String getInstruction(int to, String text){
        String str = new String(instruction + "§" + server + "§"+ to + "§" + text + "§");
        return str;
    }
    public String getPriorityAck(int to, String text){
        String str = new String(priorityAck + "§" + server + "§"+ to + "§" + text + "§");
        return str;
    }
    public String getPriorityNack(int to, String text){
        String str = new String(priorityNack + "§" + server + "§"+ to + "§" + text + "§");
        return str;
    }
    public String getClose(int to, String text){
        String str = new String(close + "§" + server + "§"+ to + "§" + text + "§");
        return str;
    }
    public String getExit(int to, String text){
        String str = new String(exit + "§" + server + "§"+ to + "§" + text + "§");
        return str;
    }
    
}

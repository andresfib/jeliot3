package jeliot.broadcast.client;

import java.io.*;
import java.net.*;

public class CliMessages {
    
        //Attributes
    private final static String hello = "HELLO";
    private final static String ack = "ACK";
    private final static String nack = "NACK";
    private final static String instruction = "INST";
    private final static String priority = "PRI";
    private final static String exit = "EXIT";
    private final static String close = "CLOSE";
    
    public CliMessages() {
    }
    
    public String getHello(int from, String to, String text){
        String str = new String(hello + "§" + from + "§"+ to + "§" + text + "§");
        return str;
    }
    public String getAck(int from, String to, String text){
        String str = new String(ack + "§" + from + "§"+ to + "§" + text + "§");
        return str;
    }
    public String getNack(int from, String to, String text){
        String str = new String(nack + "§" + from + "§"+ to + "§" + text + "§");
        return str;
    }
    public String getInstruction(int from, String to, String text, boolean priority){
        String str = new String(instruction + "§" + from + "§"+ to + "§" + text + "§" + priority + "§");
        return str;
    }
    public String getPriority(int from, String to, String text){
        String str = new String(priority + "§" + from + "§"+ to + "§" + text + "§");
        return str;
    }
    public String getExit(int from, String to, String text){
        String str = new String(exit + "§" + from + "§"+ to + "§" + text + "§");
        return str;
    }
    public String getClose(int from, String to, String text){
        String str = new String(close + "§" + from + "§"+ to + "§" + text + "§");
        return str;
    }
}

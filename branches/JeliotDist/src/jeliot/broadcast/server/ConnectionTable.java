package jeliot.broadcast.server;
/**
 *
 * @author Carlos Menéndez
 */
import java.io.*;
import java.net.*;
import java.util.*;

public class ConnectionTable {
    
    public Hashtable connections;
    public Vector connectionIds;
    private ServMessages message = null;
    private Server server = null;
    private int counter = 0;

    public ConnectionTable(Server server, ServMessages message) {
        this.connections = new Hashtable();
        this.connectionIds = new Vector();
        this.message = message;
        this.server = server;
        
    }
    
    public int addConnection(Socket clientSocket){
        int connectionId =  counter++;
        connectionIds.add(new Integer(connectionId));
        connections.put(new Integer(connectionId), new ServClient(server, clientSocket));
        ServClient client = (ServClient) connections.get(connectionId);
        client.idClient = connectionId;
        client.sendToClient(message.getHello(connectionId, String.valueOf(connectionId)));
        return connectionId;
    }
    
    public void removeConnection(Integer idClient){
        ServClient client = (ServClient) connections.get(idClient);
        System.out.println("Closing client " + idClient);
        client.sendToClient(message.getClose(client.idClient, "Close"));
        System.out.println("Closing servCli \n");
        client.closeConnection();
        connections.remove(idClient);
        connectionIds.remove(idClient);
        ServConnectionThread.numClients--;
        System.out.println("Number of clients " + ServConnectionThread.numClients);

    }
    
    public boolean closeAllConnections(){
        Iterator it = connectionIds.iterator();
        while(!connections.isEmpty()){
            ServClient client = (ServClient) this.getNextClient();
            client.sendToClient(message.getExit(client.idClient, "Exit"));
            while(client.serverReceiveData.isAlive()){
            }
            this.removeNextClient();
            ServConnectionThread.numClients--;
            System.out.println("Number of Clients " + ServConnectionThread.numClients);
            client.closeConnection();
        }
        return true;
    }
    
    public ServClient getNextClient(){
        Iterator it = connectionIds.iterator();
        return (ServClient) connections.get( (Integer) it.next());
    }
    
    public void removeNextClient(){
        Iterator it = connectionIds.iterator();
        Integer id = (Integer) it.next();
        connections.remove(id);
        connectionIds.remove(id);
        counter--;
    }
    public boolean isEmpty(){
        return connectionIds.isEmpty();
    }
    
}

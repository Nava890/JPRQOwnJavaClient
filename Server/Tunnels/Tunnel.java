package Server.Tunnels;

import Server.Events.ConnectionReceived;
import Server.Events.Event;
import Server.Server.TCPServer;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class Tunnel implements TunnelInterface{
    public String hostName;
    public int maxConLimit;
    public InputStream eventWriter;
    public static final ReentrantLock eventWriterMx = new ReentrantLock();;
    public TCPServer privateServer;
    public Map<Short, Socket> publicCons;
    public Map<Short, byte[]> initialBuffer;
    Tunnel (String hostName,InputStream eventWriter, int maxConLimit) {
        this.hostName = hostName;
        this.maxConLimit = maxConLimit;
        this.eventWriter = eventWriter;
        this.publicCons = new HashMap<Short, Socket>();
        this.initialBuffer = new HashMap<Short,byte[]>();
        this.privateServer = new TCPServer();
    }

    @Override
    public void open() {

    }

    @Override
    public void close(){
        try {
            this.privateServer.close();
        } catch (IOException e) {
            System.out.println("Error closing Tunnel");
        }
        for (Map.Entry<Short, Socket> entry : publicCons.entrySet()) {
            Socket con = entry.getValue(); // Get the connection
            try {
                if (con != null) {
                    con.close(); // Close the connection
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            short port = entry.getKey();
            publicCons.remove(port);
            initialBuffer.remove(port);
        }
    }
    @Override
    public String hostName() {
        return hostName;
    }
    @Override
    public String protocol(){
        return "http";
    }
    @Override
    public int publicServerport(Socket publicCon){
        return this.privateServer.port();
    }

    public boolean publicConnectionHandler(Socket publicCon){
        InetAddress inetAddress = publicCon.getInetAddress();
        String ip = inetAddress.getHostAddress();
        int port = publicCon.getPort();
        short portAsShort = (short) port;
        eventWriterMx.lock();
        if (publicCons.size() >= maxConLimit) {
            ConnectionReceived connectionReceived = new ConnectionReceived(ip,true);
            Event<ConnectionReceived> event = new Event<>(connectionReceived);
            closeTheSocket(publicCon);
            event.write(this.eventWriter);
            System.out.println("Connection limit reached");
            return false;
        }
        ConnectionReceived connectionReceived = new ConnectionReceived(ip,portAsShort,false);
        Event<ConnectionReceived> event = new Event<>(connectionReceived);
        if(!event.write(this.eventWriter)){
            System.out.println("cannot write event");
            closeTheSocket(publicCon);
            return false;
        }
        this.publicCons.put(portAsShort, publicCon);
        return true;

    }

    @Override
    public int privateServerport(Socket publicCon) {
        return 0;
    }

    public void closeTheSocket(Socket publicCon) {
        try {
            publicCon.close();
        } catch (IOException e) {
            System.out.println("Error closing Tunnel");
        }
    }
}

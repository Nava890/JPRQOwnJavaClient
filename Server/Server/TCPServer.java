package Server.Server;

import Server.Events.Event;
import Server.Events.TunnelRequested;
import Server.Jprq;
import Server.Tunnels.Tunnel;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocketFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyStore;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class TCPServer {
    public String title;
    public ServerSocket socket;
    public Socket listener;

    public boolean init(int port, String title) {
        try {

            this.socket = new ServerSocket(port);
            this.title = title;
            System.out.println("Server initialized with title: " + title + " on port: " + port);
            return true;
        } catch (IOException e) {
            System.err.println("Error initializing server: " + e.getMessage());
            return false;
        }
    }
    public boolean initTLS(int port, String title, String certFile, String keyFile, String password) {
        try {
            // Load the keystore (containing the certificate and private key)
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            try (FileInputStream keyStoreStream = new FileInputStream(certFile)) {
                keyStore.load(keyStoreStream, password.toCharArray());
            }

            // Set up KeyManagerFactory to use the loaded keystore
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keyStore, password.toCharArray());

            // Create an SSLContext with the key managers
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyManagerFactory.getKeyManagers(), null, null);

            // Create SSLServerSocket using the SSLContext
            SSLServerSocketFactory sslServerSocketFactory = sslContext.getServerSocketFactory();
            socket = sslServerSocketFactory.createServerSocket(port);

            // Set the title
            this.title = title;

            System.out.println("TLS server initialized with title: " + title + " on port: " + port);
            return true;
        } catch (Exception e) {
            System.err.println("Error initializing TLS server: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    public void startEventCon(Jprq jprq) {
        try {
            listener = socket.accept();
            InputStream in = listener.getInputStream();
            Event<TunnelRequested> event = new Event<TunnelRequested>();
            if(!event.read(in)){
                System.err.println("Error reading event: " + event.toString());
                return;
            }
            TunnelRequested requested = event.getData();
            if(!requested.getProtocol().equals(Event.HTTP)){
                System.out.println("protocol not supported " + requested.toString());
                return;
            }
            if(requested.getSubDomain().isEmpty() || requested.getSubDomain().isBlank()){
                requested.setSubDomain("sample");
            }
            if(validateSubDomain(requested.getSubDomain())){
                System.out.println("Invalid  subdomain");
                return;

            }
            String hostName = jprq.config.domainName+requested.getSubDomain();


        }catch (Exception e) {
            System.err.println("Error initializing TLS server: " + e.getMessage());
            e.printStackTrace();
        }
    }
    public void startPublicCon(Jprq jprq){
        try{
            listener = socket.accept();
        } catch (IOException e) {
            System.out.println("Error ocuured when trying to open socket publicCon");
            return;
        }
    }
    public void close() throws IOException {
        this.listener.close();
    }
    private boolean validateSubDomain(String subDomain) {
         Pattern regex = Pattern.compile("^[a-z\\d](?:[a-z\\d]|-[a-z\\d]){0,38}$");

        // Blocklist map
        Map<String, Boolean> blockList = new HashMap<String, Boolean>() {{
            put("www", true);
            put("jprq", true);
        }};
        if(subDomain.length()>38 || subDomain.length()<3){
            System.out.println("Subdomain length cannot be less than 3 or equal to 38");
            return false;
        }
        if (blockList.containsKey(subDomain)) {
            return false;
        }
        if (!regex.matcher(subDomain).matches()) {
            return false;
        }
        return true;
    }
    public short port(){
        return (short) listener.getLocalPort();
    }


    public void start(Tunnel tunnel) {
        try {
            listener = socket.accept();
        } catch (IOException e) {
            System.out.println("unable to open connection for private Con");
        }
        tunnel.privateConnectionHandler(listener);
    }
}

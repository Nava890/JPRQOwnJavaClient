package Server.Tunnels;

import java.io.InputStream;

public class HttpTunnels {
    Tunnel tunnel;
    public HttpTunnels newHttp(String hostName, InputStream writer, int maxConsLimit){

        tunnel = new Tunnel(hostName,writer,maxConsLimit);

        boolean error = tunnel.privateServer.init(0,"http-tunnel-privateServer");
        if(error){
            return null;
        }
        return this;
    }
}

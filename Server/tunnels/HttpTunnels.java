package tunnels;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class HttpTunnels {
  Tunnel tunnel;
  public static final short defaultPort = 80;

  public HttpTunnels newHttp(String hostName, OutputStream writer, int maxConsLimit) {

    tunnel = new Tunnel(hostName, writer, maxConsLimit);

    boolean error = tunnel.privateServer.init(0, "http-tunnel-privateServer");
    if (error) {
      return null;
    }
    return this;
  }

  public String protocol() {
    return "http";
  }

  public short publicServerPort() {
    return defaultPort;
  }

  public void open(Socket listener) {
    tunnel.privateServer.start(tunnel);
  }

}

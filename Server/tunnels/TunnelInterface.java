package Server.tunnels;

import java.net.Socket;

public interface TunnelInterface {
  public void open();

  public void close();

  public String hostName();

  public String protocol();

  public int publicServerport(Socket publicCon);

  public int privateServerport(Socket publicCon);
}

package Server;

import Server.server.TCPServer;
import config.Config;
import Server.tunnels.HttpTunnels;
import Server.tunnels.Tunnel;

import java.util.HashMap;
import java.util.Map;

public class Jprq {
  public Config config;
  public TCPServer eventServer;
  public TCPServer publicServer;
  public TCPServer privateServer;
  public TCPServer publicServerTLS;
  public Map<String, HttpTunnels> httpTunnels;
  public Map<String, Tunnel> userTunnels;
  public Map<String, String> subDomains;
  public Map<String, Tunnel> activeTunnels;

  public void init(Config config) {
    this.config = config;
    this.httpTunnels = new HashMap<>();
    this.userTunnels = new HashMap<>();
    this.subDomains = new HashMap<>();
    this.activeTunnels = new HashMap<>();
    this.eventServer = new TCPServer();
    this.publicServer = new TCPServer();
    this.publicServerTLS = new TCPServer();
    this.privateServer = new TCPServer();

    boolean temp = this.eventServer.init(config.eventServerPort, "jprq_event_server");
    if (!temp) {
      System.out.println("Event server failed to initialize");
      return;
    }
    temp = this.publicServer.init(config.publicServerPort, "jprq_public_server");
    if (!temp) {
      System.out.println("Public server failed to initialize");
      return;
    }
    temp = this.publicServerTLS.init(config.publicServerTLSPort, "jprq_public_server_tls");
    if (!temp) {
      System.out.println("Public serverTLS failed to initialize");
      return;
    }
  }

  public void start() {
    this.eventServer.startEventCon(this);
    this.publicServer.startPublicCon(this);
    this.publicServerTLS.startPublicCon(this);
  }
}

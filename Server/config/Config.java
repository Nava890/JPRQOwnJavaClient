package config;

public class Config {
  public String domainName;
  public Integer maxTunnelPerUser;
  public Integer maxConsPerTunnel;
  public Integer eventServerPort;
  public Integer publicServerPort;
  public Integer publicServerTLSPort;
  public String tLSCertFile;
  public String tLSKeyFile;

  public boolean load() {
    this.maxTunnelPerUser = 4;
    this.maxConsPerTunnel = 24;
    this.eventServerPort = 4321;
    this.publicServerPort = 80;
    this.publicServerTLSPort = 443;
    this.domainName = "JPRQ_DOMAIN";
    this.tLSKeyFile = "JPRQ_TLS_KEY";
    this.tLSCertFile = "JPRQ_TLS_CERT";

    if (this.domainName == "") {
      System.out.println("Domain not found");
      return false;
    }
    if (this.tLSKeyFile == "" || this.tLSCertFile == "") {
      System.out.println("TLS key/cert file is missing");
      return false;
    }
    return true;
  }
}

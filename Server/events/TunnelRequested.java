package events;

public class TunnelRequested implements EventType {
  private String protocol;

  private String subDomain;

  public TunnelRequested(String protocol, String subdomain) {
    this.protocol = protocol;
    this.subDomain = subdomain;
  }

  public String getSubDomain() {
    return subDomain;
  }

  public void setSubDomain(String subDomain) {
    this.subDomain = subDomain;
  }

  public String getProtocol() {
    return protocol;
  }

  public void setProtocol(String protocol) {
    this.protocol = protocol;
  }
}

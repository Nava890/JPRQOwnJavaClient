package Server.Events;

public class TunnelOpened implements EventType{
    private String hostName;
    private String protocol;
    private int publicServer;
    private int privateServer;
    private String errorMessage;

    public TunnelOpened(String hostname, String protocol, int publicServer, int privateServer, String errorMessage) {
        this.hostName = hostname;
        this.protocol = protocol;
        this.publicServer = publicServer;
        this.privateServer = privateServer;
        this.errorMessage = errorMessage;
    }
    public String getHostname() {
        return hostName;
    }

    public void setHostname(String hostname) {
        this.hostName = hostname;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public int getPublicServer() {
        return publicServer;
    }

    public void setPublicServer(int publicServer) {
        this.publicServer = publicServer;
    }

    public int getPrivateServer() {
        return privateServer;
    }

    public void setPrivateServer(int privateServer) {
        this.privateServer = privateServer;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}


package Server.Events;

public class ConnectionReceived implements EventType{
    private String clientIp;  // Use String for IP address in Java
    private int clientPort;
    private boolean rateLimited;

    public ConnectionReceived(String clientIp, int clientPort, boolean rateLimited) {
        this.clientIp = clientIp;
        this.clientPort = clientPort;
        this.rateLimited = rateLimited;
    }
    public ConnectionReceived(String clientIp, boolean rateLimited) {
        this.clientIp = clientIp;
        this.rateLimited = rateLimited;
    }
    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    public int getClientPort() {
        return clientPort;
    }

    public void setClientPort(int clientPort) {
        this.clientPort = clientPort;
    }

    public boolean isRateLimited() {
        return rateLimited;
    }

    public void setRateLimited(boolean rateLimited) {
        this.rateLimited = rateLimited;
    }
}

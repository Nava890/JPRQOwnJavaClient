package Server.Config;

public class Config{
    public String domainName;
    public Integer maxTunnelPerUser;
    public Integer maxConsPerTunnel;
    public Integer eventServerPort;
    public Integer publicServerPort;
    public Integer publicServerTLSPort;
    public String tLSCertFile;
    public String tLSKeyFile;

    public void load(Config config) throws Exception {
        config.maxTunnelPerUser = 4;
        config.maxConsPerTunnel = 24;
        config.eventServerPort = 4321;
        config.publicServerPort = 80;
        config.publicServerTLSPort =443;
        config.domainName = "JPRQ_DOMAIN";
        config.tLSKeyFile = "JPRQ_TLS_KEY";
        config.tLSCertFile = "JPRQ_TLS_CERT";

        if (config.domainName =="")
            throw new Exception("Domain not found");
        if (config.tLSKeyFile == "" || config.tLSCertFile == "")
		    throw new Exception("TLS key/cert file is missing");
    }
}

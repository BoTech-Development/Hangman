package Server.Models.Protocol;

public class ServerInfo
{
    public String IPAddress;
    public int Port;
    public String Hostname;
    public ServerInfo(String IPAddress, int Port, String Hostname)
    {
        this.IPAddress = IPAddress;
        this.Port = Port;
        this.Hostname = Hostname;
    }
}

package Server.Models.Protocol;

public class ClientInfo
{
    public String IPAddress;
    public int Port;
    public String Hostname;
    public String SessionString;

    public ClientInfo(String IPAddress, int Port, String Hostname, String SessionString)
    {
        this.IPAddress = IPAddress;
        this.Port = Port;
        this.Hostname = Hostname;
        this.SessionString = SessionString;
    }
    public ClientInfo(String IPAddress, int Port, String Hostname)
    {
        this.IPAddress = IPAddress;
        this.Port = Port;
        this.Hostname = Hostname;
        this.SessionString = "";
    }
}

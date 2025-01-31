package Server.Models.Protocol;

import java.util.HashMap;

public class Request
{
    public String Version;
    public ClientInfo ClientInfo;
    public String EndPoint;
    public HashMap<String,String> Parameters;

    public Request(){}

    public Request(String version, ClientInfo clientInfo, String endPoint, HashMap<String,String> parameters)
    {
        Version = version;
        ClientInfo = clientInfo;
        EndPoint = endPoint;
        Parameters = parameters;

    }

}

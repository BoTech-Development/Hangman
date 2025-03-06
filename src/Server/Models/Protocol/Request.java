package Server.Models.Protocol;

import java.util.HashMap;

public class Request
{
    public String Version;
    public ClientInfo ClientInfo;
    public String EndPoint;
    public HashMap<String,Object> Parameters;
    public Request(){}

    public Request(String version, ClientInfo clientInfo, String endPoint, HashMap<String,Object> parameters)
    {
        Version = version;
        ClientInfo = clientInfo;
        EndPoint = endPoint;
        Parameters = parameters;

    }

}

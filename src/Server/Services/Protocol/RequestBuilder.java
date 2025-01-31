package Server.Services.Protocol;

import Server.Models.Protocol.ClientInfo;
import Server.Models.Protocol.Request;

import java.net.Inet4Address;
import java.util.HashMap;
import java.util.Map;

public class RequestBuilder
{
    private Request currentRequest;
    private ClientInfo currentClientInfo;
    public RequestBuilder()
    {
        try
        {
            currentClientInfo = new ClientInfo(Inet4Address.getLocalHost().getHostAddress(), 80,Inet4Address.getLocalHost().getHostName());
        }
        catch (Exception e)
        {
            System.out.println("Could not get local host name!");
        }
    }
    public void Create(String sessionString, String endPoint, HashMap<String,String> Parameters)
    {
        currentClientInfo.SessionString = sessionString;
        currentRequest = new Request("V1.1.0", currentClientInfo, endPoint, Parameters);

    }
    public String Serialize(){
        String requestString = "StartRequest;";
        requestString += "ProtocolInfo:Version=" + currentRequest.Version + ";\n";
        requestString += "ClientInfo:Hostname=" + currentRequest.ClientInfo.Hostname + ";\n";
        requestString += "ClientInfo:IPAddress=" + currentRequest.ClientInfo.IPAddress + ";\n";
        requestString += "ClientInfo:Port=" + currentRequest.ClientInfo.Port + ";\n";
        requestString += "ClientInfo:SessionString=" + currentRequest.ClientInfo.SessionString + ";\n";
        requestString += "EndPoint=" + currentRequest.EndPoint + ";\n";

        for (Map.Entry<String, String> param : currentRequest.Parameters.entrySet())
        {
            requestString += "Param:" + param.getKey() + "=" + param.getValue() + ";\n";
        }
        requestString += "}";
        return requestString;

    }
}

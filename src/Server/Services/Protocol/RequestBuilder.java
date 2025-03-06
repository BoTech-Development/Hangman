package Server.Services.Protocol;

import Server.Models.Protocol.ClientInfo;
import Server.Models.Protocol.Request;

import java.net.Inet4Address;
import java.util.HashMap;
import java.util.Map;

public class RequestBuilder extends BuilderBase
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
    public void Create(String sessionString, String endPoint, HashMap<String,Object> Parameters)
    {
        currentClientInfo.SessionString = sessionString;
        currentRequest = new Request("V1.1.0", currentClientInfo, endPoint, Parameters);

    }
    public String Serialize(){
        String requestString = "StartRequest;";//\n";
        requestString += "ProtocolInfo:Version=" + currentRequest.Version + ";";//\n";
        requestString += "ClientInfo:Hostname=" + currentRequest.ClientInfo.Hostname + ";";//\n";
        requestString += "ClientInfo:IPAddress=" + currentRequest.ClientInfo.IPAddress + ";";//\n";
        requestString += "ClientInfo:Port=" + currentRequest.ClientInfo.Port + ";";//\n";
        requestString += "ClientInfo:SessionString=" + currentRequest.ClientInfo.SessionString + ";";//\n";
        requestString += "EndPoint:Function=" + currentRequest.EndPoint + ";";//\n";

        for (Map.Entry<String, Object> param : currentRequest.Parameters.entrySet())
        {
            if(param.getValue().getClass().isPrimitive() || param.getValue().getClass().getName().contains("String") || param.getValue().getClass().getName().contains("Character"))
            {
                requestString += "Param:" + param.getKey() + ":" + param.getValue().getClass().getName() + "=" + param.getValue() + ";";//\n";
            }
            else
            {
                requestString += "Param:" + param.getKey() + ":" + param.getValue().getClass().getName() + "={" + this.CreateParamStringForObject(param.getValue()) + "};";//\n";
            }
        }
        requestString += "EndRequest;";
        return requestString;

    }
}

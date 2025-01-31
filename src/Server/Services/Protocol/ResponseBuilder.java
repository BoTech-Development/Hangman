package Server.Services.Protocol;

import Server.Models.Protocol.Response;
import Server.Models.Protocol.ResponseInfo;
import Server.Models.Protocol.ServerInfo;

import java.lang.reflect.Field;
import java.net.Inet4Address;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class ResponseBuilder
{
    private Response currentResponse;
    private ServerInfo currentServerInfo;
    public ResponseBuilder(){
        try
        {
            currentServerInfo = new ServerInfo(Inet4Address.getLocalHost().getHostAddress(), 80,Inet4Address.getLocalHost().getHostName());
        }
        catch (Exception e)
        {
            System.out.println("Could not get local host name!");
        }
    }
    public void Create(int ID, Object object)
    {
        HashMap<String, String> Parameters = new HashMap<>();
        for(Field field : object.getClass().getFields())
        {
            try {
                Parameters.put(field.getName(), field.get(object).toString());
            }catch (Exception e){
                System.out.println("Could not get parameter: " + field.getName() + " from " + object.getClass().getName());
            }

        }
        ResponseInfo responseInfo = new ResponseInfo(ID, LocalDateTime.now(), object.getClass().getName());
        currentResponse = new Response("V1.1.0", currentServerInfo, responseInfo, Parameters, new HashMap<String, String>());
    }
    public String Serialize(){
        String responseString = "StartResponse;";
        responseString += "ProtocolInfo:Version=" + currentResponse.Version + ";\n";
        responseString += "ServerInfo:IPAddress=" + currentResponse.ServerInfo.IPAddress + ";\n";
        responseString += "ServerInfo:Hostname=" + currentResponse.ServerInfo.Hostname + ";\n";
        responseString += "ServerInfo:Port=" + currentResponse.ServerInfo.Port + ";\n";

        responseString += "ResponseInfo:RequestID=" + currentResponse.ResponseInfo.RequestID + ";\n";
        responseString += "ResponseInfo:TimeElapsed=" + currentResponse.ResponseInfo.TimeElapsed + ";\n";
        responseString += "ResponseInfo:ObjectType=" + currentResponse.ResponseInfo.ObjectType + ";\n";

        responseString += currentResponse.ResponseInfo.ObjectType + ":Properties=\n{\n";
        // Add all Properties:

        for (Map.Entry<String, String> property : currentResponse.Properties.entrySet())
        {
            responseString += property.getKey() + "=" + property.getValue() + ";\n";
        }
        responseString += "}\n";
        responseString += "EndResponse;";
        return responseString;
    }
}

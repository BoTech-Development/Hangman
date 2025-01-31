package Server.Services.Protocol;

import Server.Models.Protocol.ClientInfo;
import Server.Models.Protocol.Response;
import Server.Models.Protocol.ResponseInfo;
import Server.Models.Protocol.ServerInfo;

import java.time.LocalDateTime;
import java.util.HashMap;

public class ResponseDecoder extends DecoderBase
{
    private Response currentResponse;
    public Response DeserializeResponse(String response){
        currentResponse = new Response();
        String[] lines = response.split(";");
        this.RemoveLineBreakFromLines(lines);
        if(lines[0].equals("StartResponse") && lines[lines.length-1].equals("EndResponse"))
        {
            for (String line : lines)
            {
                if(!line.equals("StartResponse") && !line.equals("EndResponse")) DeserializeLine(line);
            }
        }
        return currentResponse;
    }

    private void DeserializeLine(String line)
    {
        if(line.contains("{") && line.contains("}"))
        {
            String className = line.split(":")[0];
            String propertyName = line.split(":")[1].split("=")[0];
            String propertyValue = line.substring(line.indexOf("=") + 1);
            if(className.equals(currentResponse.ResponseInfo.ObjectType)){
                switch(propertyName){
                    case "Properties":
                        currentResponse.Properties = DeserializeHashMap(propertyValue);
                        break;
                        case "Ctor":
                            currentResponse.Ctor = DeserializeHashMap(propertyValue);
                            break;
                }
            }
            else
            {
                System.out.println("Error by deserializing line: " + line + ". The ObjectType is not set to the currect Value or the Set statement is not above this Statement.");
            }
        }
        else
        {
            String propertyBase = line.split(":")[0];
            String propertyName = line.split(":")[1].split("=")[0];
            String propertyValue = line.split("=")[1];
            if(!propertyBase.isEmpty() && !propertyName.isEmpty() && !propertyValue.isEmpty()){
                switch(propertyBase){
                    case "ProtocolInfo":
                        if(propertyName.equals("Version")) currentResponse.Version = propertyValue;
                        break;
                    case "ServerInfo":
                        DeserializeServerInfo(propertyName, propertyValue);
                        break;
                    case "ResponseInfo":
                        DeserializeResponseInfo(propertyName, propertyValue);
                        break;
                }
            }
        }
    }
    private HashMap<String,String> DeserializeHashMap(String values)
    {
        values = values.replace("{", "").replace("}", "");
        String[] keysAndValues = values.split(",");
        HashMap<String,String> map = new HashMap<>();
        for(String keyAndValue : keysAndValues){
            String key = keyAndValue.split("=")[0];
            String value = keyAndValue.split("=")[1];
            map.put(key, value);
        }
        return map;
    }
    private void DeserializeResponseInfo(String propertyName, String propertyValue){
        if(currentResponse.ResponseInfo == null) currentResponse.ResponseInfo = new ResponseInfo();
        switch(propertyName){
            case "RequestID":
                currentResponse.ResponseInfo.RequestID = Integer.parseInt(propertyValue);
                break;
            case "TimeElapsed":
                currentResponse.ResponseInfo.TimeElapsed = propertyValue;
                break;
            case "ObjectType":
                currentResponse.ResponseInfo.ObjectType = propertyValue;
                break;
        }
    }
    private void DeserializeServerInfo(String propertyName, String propertyValue)
    {
        if(currentResponse.ServerInfo == null) currentResponse.ServerInfo = new ServerInfo();
        switch(propertyName){
            case "Hostname":
                currentResponse.ServerInfo.Hostname = propertyValue;
                break;
            case "Port":
                currentResponse.ServerInfo.Port = Integer.parseInt(propertyValue);
                break;
            case "IPAddress":
                currentResponse.ServerInfo.IPAddress = propertyValue;
                break;
        }
    }
}

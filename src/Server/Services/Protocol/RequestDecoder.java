package Server.Services.Protocol;

import Server.Models.Protocol.ClientInfo;
import Server.Models.Protocol.Request;

import java.util.HashMap;

public class RequestDecoder
{
    private Request currentRequest;
    public Request DeserializeRequest(String request)
    {
        currentRequest = new Request();
        String[] lines = request.split(";");
        RemoveLineBreakFromLines(lines);
        if(lines[0].equals("StartRequest") && lines[lines.length-1].equals("EndRequest"))
        {
            for (String line : lines)
            {
                if(!line.equals("StartRequest") && !line.equals("EndRequest")) DeserializeLine(line);
            }
        }
        return currentRequest;
    }
    private void RemoveLineBreakFromLines(String[] lines){
        for (int i = 0; i < lines.length; i++) {
            lines[i] = lines[i].replaceAll("\\r?\\n", "");
        }
    }
    private void DeserializeLine(String line)
    {
        String propertyBase = line.split(":")[0];
        String propertyName = line.split(":")[1].split("=")[0];
        String propertyValue = line.split("=")[1];
        if(!propertyBase.isEmpty() && !propertyName.isEmpty() && !propertyValue.isEmpty()){
            switch(propertyBase){
                case "ProtocolInfo":
                    if(propertyName.equals("Version")) currentRequest.Version = propertyValue;
                    break;
                case "ClientInfo":
                    DeserializeClientInfo(propertyName, propertyValue);
                    break;
                case "EndPoint":
                    if(propertyName.equals("Function")) currentRequest.EndPoint = propertyValue;
                    break;
                case "Param":
                    if(currentRequest.Parameters == null) currentRequest.Parameters = new HashMap<String, String>();
                    currentRequest.Parameters.put(propertyName, propertyValue);
                    break;
            }
        }
    }
    private void DeserializeClientInfo(String propertyName, String propertyValue)
    {
        if(currentRequest.ClientInfo == null) currentRequest.ClientInfo = new ClientInfo("", 0, "");
        switch(propertyName){
            case "Hostname":
                currentRequest.ClientInfo.Hostname = propertyValue;
                break;
            case "Port":
                currentRequest.ClientInfo.Port = Integer.parseInt(propertyValue);
                break;
            case "IPAddress":
                currentRequest.ClientInfo.IPAddress = propertyValue;
                break;
            case "SessionString":
                currentRequest.ClientInfo.SessionString = propertyValue;
                break;
        }
    }
}
package Server.Services.Protocol;

import Server.Models.Protocol.ClientInfo;
import Server.Models.Protocol.Request;

import java.lang.reflect.Field;
import java.util.HashMap;

public class RequestDecoder extends DecoderBase
{
    private Request currentRequest;
    public Request DeserializeRequest(String request)
    {
        currentRequest = new Request();
        String[] lines = request.split(";");
        this.RemoveLineBreakFromLines(lines);
        if(lines[0].equals("StartRequest") && lines[lines.length-1].equals("EndRequest"))
        {
            for (String line : lines)
            {
                if(!line.equals("StartRequest") && !line.equals("EndRequest")) DeserializeLine(line);
            }
        }
        return currentRequest;
    }

    private void DeserializeLine(String line)
    {
        try {
            String propertyBase = line.split(":")[0];
            String propertyName = line.split(":")[1].split("=")[0];
            String propertyValue = line.split("=")[1];
            if (!propertyBase.isEmpty() && !propertyName.isEmpty() && !propertyValue.isEmpty()) {
                switch (propertyBase) {
                    case "ProtocolInfo":
                        if (propertyName.equals("Version")) currentRequest.Version = propertyValue;
                        break;
                    case "ClientInfo":
                        DeserializeClientInfo(propertyName, propertyValue);
                        break;
                    case "EndPoint":
                        if (propertyName.equals("Function")) currentRequest.EndPoint = propertyValue;
                        break;
                    case "Param":
                        if (currentRequest.Parameters == null)
                            currentRequest.Parameters = new HashMap<String, Object>();
                        if (line.contains("{") && line.contains("}")) {
                            String[] splitedValue = line.replace(propertyBase + ":" + propertyName + ":", "").split("=", 2);
                            String className = splitedValue[0];
                            String paramString = splitedValue[1];
                            try {
                                currentRequest.Parameters.put(propertyName, this.DecodePropertiesString(paramString, Class.forName(className)));
                            } catch (ClassNotFoundException e) {
                                System.out.println("Error by Deserializing the Request! There might be an error in the json string!: " + e.getMessage());
                            }
                        } else {
                            // it is a Primitive Type
                            String[] splitedValue = line.replace(propertyBase + ":" + propertyName + ":", "").split("=", 2);
                            String className = splitedValue[0];
                            String value = splitedValue[1];
                            Object deserializedPrimitive = this.TryToParsePrimitive(value, className);
                            if (deserializedPrimitive != null)
                                currentRequest.Parameters.put(propertyName, deserializedPrimitive);
                        }
                        break;
                }
            }
        }catch (Exception e){
            System.out.println("Error by Deserializing the Request! There might be an error in one or more lines of the request. This exception can be thrown although the request is ok, when some of the props is String.Empty!:  " + e.getMessage());
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
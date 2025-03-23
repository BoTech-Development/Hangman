package Server.Services.Protocol;

import Abitur.List;
import Server.Models.Protocol.Response;
import Server.Models.Protocol.ResponseInfo;
import Server.Models.Protocol.ServerInfo;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.Inet4Address;
import java.time.LocalDateTime;
import java.util.HashMap;

import java.util.Map;

public class ResponseBuilder extends BuilderBase
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
        ResponseInfo responseInfo = new ResponseInfo(ID, object.getClass().getName());
        currentResponse = new Response("V1.1.0", currentServerInfo, responseInfo, object, new HashMap<String, String>());
    }
    public String Serialize(){
        String responseString = "StartResponse;";//\n";
        responseString += "ProtocolInfo:Version=" + currentResponse.Version + ";";//\n";
        responseString += "ServerInfo:IPAddress=" + currentResponse.ServerInfo.IPAddress + ";";//\n";
        responseString += "ServerInfo:Hostname=" + currentResponse.ServerInfo.Hostname + ";";//\n";
        responseString += "ServerInfo:Port=" + currentResponse.ServerInfo.Port + ";";//\n";

        responseString += "ResponseInfo:RequestID=" + currentResponse.ResponseInfo.RequestID + ";";//\n";
        responseString += "ResponseInfo:TimeElapsed=" + currentResponse.ResponseInfo.TimeElapsed + ";";//\n";
        responseString += "ResponseInfo:ObjectType=" + currentResponse.ResponseInfo.ObjectType + ";";//\n";
        if(currentResponse.Object != null) {
            if (currentResponse.Object instanceof Boolean) {
                // The Type is Primitive => it is not necessary to use json
                if((Boolean) currentResponse.Object){
                    responseString += currentResponse.ResponseInfo.ObjectType + ":Properties={TRUE};";
                }else{
                    responseString += currentResponse.ResponseInfo.ObjectType + ":Properties={FALSE};";
                }


            }
            else
            {
                responseString += currentResponse.ResponseInfo.ObjectType + ":Properties={";
                responseString += this.CreateParamStringForObject(currentResponse.Object);
                responseString += "};";//\n";
            }
        }
        else
        {
            responseString += "Value:" + "NOTYPE" + "=NULL;";
        }
        responseString += "EndResponse;";
        return responseString;
    }

}

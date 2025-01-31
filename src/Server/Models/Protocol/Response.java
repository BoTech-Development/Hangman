package Server.Models.Protocol;

import java.util.HashMap;

public class Response
{
    public String Version;
    public ServerInfo ServerInfo;
    public ResponseInfo ResponseInfo;
    public HashMap<String, String> Properties;
    public HashMap<String, String> Ctor;

    public Response(String Version, ServerInfo ServerInfo, ResponseInfo ResponseInfo, HashMap<String, String> Properties, HashMap<String, String> Ctor){
        this.Version = Version;
        this.ServerInfo = ServerInfo;
        this.ResponseInfo = ResponseInfo;
        this.Properties = Properties;
        this.Ctor = Ctor;
    }
}

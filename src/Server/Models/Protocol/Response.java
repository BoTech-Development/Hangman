package Server.Models.Protocol;

import java.util.HashMap;

public class Response
{
    public String Version;
    public ServerInfo ServerInfo;
    public ResponseInfo ResponseInfo;

    public HashMap<String, String> Ctor;
    public Object Object;

    public Response(String Version, ServerInfo ServerInfo, ResponseInfo ResponseInfo,Object Object, HashMap<String, String> Ctor){
        this.Version = Version;
        this.ServerInfo = ServerInfo;
        this.ResponseInfo = ResponseInfo;
        this.Ctor = Ctor;
        this.Object = Object;
    }
    public Response()
    {
        this.Version = "";
        this.ServerInfo = new ServerInfo();
        this.ResponseInfo = new ResponseInfo();
        this.Ctor = new HashMap<String, String>();
    }
}

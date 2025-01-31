import Server.Services.Protocol.RequestDecoder;
import Server.Services.Protocol.ResponseDecoder;

public class Main {
    public static void main(String[] args) {
        new RequestDecoder().DeserializeRequest("StartRequest;\n" +
                "ProtocolInfo:Version=V1.1.0;\n" +
                "ClientInfo:Hostname=SurfaceVonFlo;\n" +
                "ClientInfo:SessionString=103_SurfaceVonFlo_128.128.128.0;\n" +
                "ClientInfo:IPAddress=128.128.128.0;\n" +
                "EndPoint:Function=StartGame;\n" +
                "Param:UserID=103;\n" +
                "Param:GameID=213;\n" +
                "EndRequest;");
        new ResponseDecoder().DeserializeResponse("StartResponse;\n" +
                "ProtocolInfo:Version=V1.1.0;\n" +
                "ServerInfo:IPAddress=127.127.0.1;\n" +
                "ServerInfo:Port=80;\n" +
                "ServerInfo:Hostname=ServerName;\n" +
                "ResponseInfo:RequestID=83247;\n" +
                "ResponseInfo:TimeElapsed=2523ms;\n" +
                "ResponseInfo:ObjectType=Game;\n" +
                "Game:Properties={ID=213,Word=Baum};\n" +
                "EndResponse;");
    }
}
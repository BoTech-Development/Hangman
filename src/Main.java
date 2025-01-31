import Server.Services.Protocol.RequestDecoder;

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
    }
}
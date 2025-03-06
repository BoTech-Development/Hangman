package Server.Models.Protocol;

import java.time.LocalDateTime;
import java.util.HashMap;

public class ResponseInfo
{
    public int RequestID;
    public String TimeElapsed;
    public String ObjectType;

    public ResponseInfo(int requestID, String objectType){
        this.RequestID = requestID;

        this.ObjectType = objectType;
    }
    public ResponseInfo(){
        this.RequestID = 0;
        this.ObjectType = "";
        this.TimeElapsed = "";
    }
}

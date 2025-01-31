package Server.Models.Protocol;

import java.time.LocalDateTime;
import java.util.HashMap;

public class ResponseInfo
{
    public int RequestID;
    public String TimeElapsed;
    public String ObjectType;
    private LocalDateTime Started;
    public ResponseInfo(int requestID, LocalDateTime started, String objectType){
        this.RequestID = requestID;
        this.Started = started;
        this.ObjectType = objectType;
    }
}

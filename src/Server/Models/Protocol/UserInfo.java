package Server.Models.Protocol;

public class UserInfo
{
    public String SessionString;
    public int UserID;
    public UserInfo(){}
    public UserInfo(String SessionString, int UserID){
        this.SessionString = SessionString;
        this.UserID = UserID;
    }
}

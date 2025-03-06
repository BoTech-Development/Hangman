package Server.Models;

import Server.Services.IDProvider;

import java.time.Duration;
import java.time.LocalDateTime;

public class Session
{
    public int ID = 0;
    public String IPAddress = "";
    public String Hostname = "";
    public LocalDateTime CreationDate;
    public Session(String IPAddress, String Hostname)
    {
        this.IPAddress = IPAddress;
        this.Hostname = Hostname;
        if(IDProvider.Instance.GetAndSetNewIDForObject(this)){
            System.out.println("Error: IDProvider failed to create new ID for Type Session.");
        }
        CreationDate = LocalDateTime.now();
    }
    public String GetDecodedUniqueSessionString()
    {
        return ID + "_" + IPAddress + "_" + Hostname + "_" + CreationDate.toString();
    }
    public Boolean IsUniqueSessionStringValid(String sessionString)
    {
        Duration diff = Duration.between(CreationDate, LocalDateTime.now());
        return GetDecodedUniqueSessionString().equals(sessionString) && diff.toMinutes() < 20;
    }
}

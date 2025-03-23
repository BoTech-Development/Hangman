package Server.Models;

import Abitur.List;
import Server.Annotations.SerializableCollection;

public class User
{
    public int ID = 0;
    public String UserName = "";
    public String Password = "";
    public String Email = "";
    public boolean EmailVerified = false;
    private String verifyCode = "";
    public GameStatistics CurrentGame = new GameStatistics();
    @SerializableCollection(GenericType = Game.class)
    public List<Game> PlayedGames = new List<Game>();
    @SerializableCollection(GenericType = Game.class)
    public List<Game> CreatedGames = new List<Game>();
    @SerializableCollection(GenericType = Session.class)
    public List<Session> Sessions = new List<Session>();
    public User(){}
    public User(String UserName, String Password, String Email){
        this.UserName = UserName;
        this.Password = Password;
        this.Email = Email;
    }
    private void CreateVerifyCodeAndSendMail(){

    }
}

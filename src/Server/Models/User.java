package Server.Models;

import Abitur.List;

public class User
{
    public int ID = 0;
    public String UserName = "";
    public String Password = "";
    public String Email = "";
    public boolean EmailVerified = false;
    private String verifyCode = "";
    public GameStatistics CurrentGame = new GameStatistics();
    public List<Game> PlayedGames = new List<Game>();
    public List<Game> CreatedGames = new List<Game>();
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

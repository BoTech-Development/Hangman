package Server.Controller;
import Abitur.List;
import Abitur.Server;
import Server.Models.Game;
import Server.Models.GameStatistics;
import Server.Models.Session;
import Server.Models.User;
import Server.Services.ListService;


import java.time.Duration;
import java.time.LocalDateTime;
import java.util.function.Function;

public class ServerController extends Server
{
    public List<Game> Games;
    public List<User> Users;
    public ServerController(int pPort) {
        super(pPort);
    }

    @Override
    public void processNewConnection(String pClientIP, int pClientPort) {

    }

    @Override
    public void processMessage(String pClientIP, int pClientPort, String pMessage) {
        send(pClientIP, pClientPort, pMessage);
    }

    @Override
    public void processClosingConnection(String pClientIP, int pClientPort) {

    }
    public Session Register(String ipAddress, String hostname, String userName, String password, String email)
    {
        Function<User, Boolean> searchUserFunc = (user) -> {
          return user.UserName.equals(userName);
        };
        User user = ListService.Find(Users,searchUserFunc);
        if(user == null) {
            Session session = new Session(ipAddress, hostname);
            User newuser = new User(userName, password, email);
            user.Sessions.append(session);

            return session;
        }
        return null; // A User with the given UserName already exists.
    }
    public boolean VerifyEmail(Session session, String code)
    {
        return false;
    }
    public Session Login(String userName, String password, String ipAddress, String hostname)
    {
        Function<User, Boolean> searchUserFunc = (User) -> {
            return User.UserName.equals(userName) && User.Password.equals(password);
        };
        User user = ListService.Find(Users,searchUserFunc);
        if(user == null) return null;
        Function<Session, Boolean> searchSessionFunc = (session) -> {
          return session.Hostname.equals(ipAddress) && session.Hostname.equals(hostname);
        };
        Session currentSession = ListService.Find(user.Sessions, searchSessionFunc);
        if(currentSession != null){
            Duration diff = Duration.between(currentSession.CreationDate, LocalDateTime.now());
            if(diff.toMinutes() < 20) return currentSession; // Login success
            return null;
        }
        return null;
    }
    /*
    public boolean CreateNewGame(int userID, String word)
    {

    }
    public List<Game> GetAllGames()
    {

    }
    public List<Game> GetAllPlayedGames(int userID)
    {

    }
    public List<Game> GetAllUnPlayedGames(int userID)
    {

    }
    public boolean StartGame(int userID, int gameID)
    {

    }
    public GameStatistics MakeGuess(int userID, Character letter)
    {

    }
    public GameStatistics StopGame(int userID)
    {

    }
    public User GetUserProfile(int userID)
    {

    }

     */
}

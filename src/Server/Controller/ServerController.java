package Server.Controller;
import Abitur.List;
import Abitur.Server;
import Server.Models.Game;
import Server.Models.GameStatistics;
import Server.Models.Protocol.ListModelGame;
import Server.Models.Protocol.Request;
import Server.Models.Protocol.UserInfo;
import Server.Models.Session;
import Server.Models.User;
import Server.Services.IDProvider;
import Server.Services.ListService;
import Server.Services.Protocol.RequestDecoder;
import Server.Services.Protocol.RequestInvoker;
import Server.Services.Protocol.ResponseBuilder;


import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.function.Function;

public class ServerController extends Server
{
    public List<Game> Games = new List<>();
    public List<User> Users = new List<>();
    private RequestInvoker requestInvoker;
    private final String[] publicAccessibleEndPoints = {"Register", "Login", "VerifyEmail"};

    public ServerController(int pPort) {
        super(pPort);
        requestInvoker = new RequestInvoker(this);
    }

    public static void main(String[]  args) {
        ServerController c = new ServerController(8000);
    }

    @Override
    public void processNewConnection(String pClientIP, int pClientPort) {

    }

    @Override
    public void processMessage(String pClientIP, int pClientPort, String pMessage) {
        RequestDecoder rd = new RequestDecoder();
        Request request = rd.DeserializeRequest(pMessage);
       // if(request.ClientInfo.IPAddress.equals(pClientIP) && request.ClientInfo.Port == pClientPort) {
            // check if the user is logged in
        if(!Arrays.asList(publicAccessibleEndPoints).contains(request.EndPoint)) {
            if (CheckIfAccessIsValid(request)) ExecuteRequest(request, pClientIP, pClientPort);
        }else {
            ExecuteRequest(request, pClientIP, pClientPort);
        }

    }
    private void ExecuteRequest(Request request, String pClientIP, int pClientPort) {
        ResponseBuilder rb = requestInvoker.Invoke(request);
        if (rb == null) {
            // Error by Invoking the requested Server Method
            ResponseBuilder rbError = new ResponseBuilder();
            rbError.Create(-1, "Error by Invoking the Request.");

        } else {
            send(pClientIP, pClientPort, rb.Serialize());
        }
    }

    @Override
    public void processClosingConnection(String pClientIP, int pClientPort) {
        Function<Session, Boolean> sessionSearchFunction = (s) ->{
            return s.IPAddress == pClientIP;
        };
        Function<User, Boolean> searchFunction = (u) -> {
            return ListService.Find(u.Sessions,  sessionSearchFunction) != null;
        };
        User possibleUser = ListService.Find(Users, searchFunction);
        if(possibleUser != null)
        {
            Session session = ListService.Find(possibleUser.Sessions, sessionSearchFunction);
            possibleUser.Sessions.toFirst();
            while (possibleUser.Sessions.hasAccess()){ if(possibleUser.Sessions.getContent() == session) break; possibleUser.Sessions.next();}
            possibleUser.Sessions.remove();
        }
    }
    private boolean CheckIfAccessIsValid(Request request)
    {
        String sessionString = request.ClientInfo.SessionString;
        Function<Session, Boolean> sessionSearchFunction = (s) ->{
           return s.GetDecodedUniqueSessionString().equals(sessionString);
        };
        Function<User, Boolean> searchFunction = (u) -> {
            return ListService.Find(u.Sessions,  sessionSearchFunction) != null;
        };
        User possibleUser = ListService.Find(Users, searchFunction);
        if(possibleUser != null){

            Session session = ListService.Find(possibleUser.Sessions,  sessionSearchFunction);
            if(session != null){
                if(session.IPAddress.equals(request.ClientInfo.IPAddress)
                        && session.Hostname.equals(request.ClientInfo.Hostname))
                {
                    if( session.IsUniqueSessionStringValid(request.ClientInfo.SessionString)){
                        return true;
                    }else {
                        // session is invalid and can be deleted
                        possibleUser.Sessions.toFirst();
                        while (possibleUser.Sessions.hasAccess()){ if (possibleUser.Sessions.getContent().equals(session)) break; possibleUser.Sessions.next();}
                        possibleUser.Sessions.remove();
                    }
                }
            }
        }
        return false;
    }
    public UserInfo Register(String ipAddress, String hostname, String userName, String password, String email)
    {
        Function<User, Boolean> searchUserFunc = (user) -> {
          return user.UserName.equals(userName);
        };
        User user = ListService.Find(Users,searchUserFunc);
        if(user == null) {
            Session session = new Session(ipAddress, hostname);
            User newuser = new User(userName, password, email);
            Users.append(newuser);
            newuser.Sessions.append(session);

            return new UserInfo(session.GetDecodedUniqueSessionString(), newuser.ID);
        }
        return null; // A User with the given UserName already exists.
    }
    public boolean VerifyEmail(Session session, String code)
    {
        return false;
    }
    /// <summary>
    /// Logs the user in
    /// </summary>
    /// <returns>
    /// Session String if success otherwise null.
    /// </returns>
    public UserInfo Login(String userName, String password, String ipAddress, String hostname)
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
            if(diff.toMinutes() < 20) {
                return new UserInfo(currentSession.GetDecodedUniqueSessionString(), user.ID);
            }
        }else{
            // Create new Session for user
            Session session = new Session(ipAddress, hostname);
            user.Sessions.append(session);
            return new UserInfo(session.GetDecodedUniqueSessionString(), user.ID);
        }
        return null;
    }
    public boolean CreateNewGame(int userID, String word, int Level)
    {
        Game game  = new Game();
        game.Word = word;
        game.Level = Level;
        IDProvider.Instance.GetAndSetNewIDForObject(game);
        User user = ListService.Find(Users, ListService.GetStandardUserSearchMethod(userID));
        if(user != null) {
            user.CreatedGames.append(game);
            Games.append(game);
            return true;
        }
        return false;
    }
    public ListModelGame GetAllGames()
    {
        return new ListModelGame(Games);
    }
    public ListModelGame GetAllPlayedGames(int userID)
    {
        User user = ListService.Find(Users, ListService.GetStandardUserSearchMethod(userID));
        if(user != null) {
            return new ListModelGame(user.PlayedGames);
        }
        return null;
    }
    public ListModelGame GetAllUnPlayedGames(int userID)
    {
        User user = ListService.Find(Users, ListService.GetStandardUserSearchMethod(userID));
        if(user != null) {
            return new ListModelGame(user.PlayedGames);
        }
        return null;
    }
    public boolean StartGame(int userID, int gameID)
    {
        User selectedUser = ListService.Find(Users, ListService.GetStandardUserSearchMethod(userID));
        if(selectedUser != null) {
            if(selectedUser.CurrentGame.Game.ID != -1) {
                return false; // The user plays a game
            }else {
                // The user plays no game
                Game selectedGame = ListService.Find(Games, GetStandardGameSearchMethod(gameID));
                if (selectedGame != null) {
                    GameStatistics currentGame = new GameStatistics();
                    currentGame.Game = selectedGame;
                    currentGame.MaxTries = selectedGame.GetMaxCountOfTries();
                    selectedUser.CurrentGame = currentGame;
                    return true;
                }
            }
        }
        return false;
    }
    public GameStatistics MakeGuess(int userID, Character letter)
    {
        User selectedUser = ListService.Find(Users, ListService.GetStandardUserSearchMethod(userID));
        if(selectedUser != null)
        {
            if(selectedUser.CurrentGame == null) {
                return null; // The user plays no game
            }
            else
            {

                if(selectedUser.CurrentGame.Game.Word.contains(letter.toString())) {
                   selectedUser.CurrentGame.CorrectTries++;

                }else{
                    selectedUser.CurrentGame.WrongTries++;
                }
                if(selectedUser.CurrentGame.CorrectTries == selectedUser.CurrentGame.MaxTries) {selectedUser.CurrentGame.Won = true;}
                if(selectedUser.CurrentGame.CorrectTries + selectedUser.CurrentGame.WrongTries >= selectedUser.CurrentGame.MaxTries) {selectedUser.CurrentGame.Won = false;}
                return selectedUser.CurrentGame;
            }
        }
        return null;
    }
    public GameStatistics StopGame(int userID)
    {
        User selectedUser = ListService.Find(Users, ListService.GetStandardUserSearchMethod(userID));
        if(selectedUser != null)
        {
            if(selectedUser.CurrentGame == null) {
                return null; // The user plays no game
            }
            else
            {
                GameStatistics currentGame = selectedUser.CurrentGame;
                selectedUser.PlayedGames.append(selectedUser.CurrentGame.Game);
                selectedUser.CurrentGame.Game.ID = -1;
                return  currentGame;
            }
        }
        return null;
    }
    public User GetUserProfile(int userID)
    {
        return ListService.Find(Users, ListService.GetStandardUserSearchMethod(userID));
    }
    private static Function<Game, Boolean> GetStandardGameSearchMethod(int ID)
    {
        Function<Game, Boolean> searchGameFunc = (Game) -> {
            return Game.ID == ID;
        };
        return searchGameFunc;
    }
}

package Client;

import Abitur.Client;
import Abitur.List;
import Server.Models.Game;
import Server.Models.GameStatistics;
import Server.Models.Protocol.ListModelGame;
import Server.Models.Protocol.Response;
import Server.Models.Protocol.UserInfo;
import Server.Models.User;
import Server.Services.Protocol.RequestBuilder;
import Server.Services.Protocol.ResponseBuilder;
import Server.Services.Protocol.ResponseDecoder;

import java.net.Inet4Address;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;


public class ClientController extends Client
{
    private WaitingState _waitingState = WaitingState.None;
    private String sessionString = null;
    private int userID = -1;
    private GameStatistics currentGame = null;

    public ClientController(String pServerIP, int pServerPort) {
        super(pServerIP, pServerPort);
    }

    @Override
    public void processMessage(String pMessage) {
        try {
            ResponseDecoder decoder = new ResponseDecoder();
            Response response = decoder.DeserializeResponse(pMessage);
            switch(_waitingState){
                case None:
                    System.out.println("Currently not Waiting for any Response of the Server but received:" + response);
                    break;
                case WaitingForRegisterResponse:
                    HandleRegisterResponse(response);
                    break;
                case WaitingForLoginResponse:
                    HandleLoginResponse(response);
                    break;
                case WaitingForCreateGameResponse:
                    HandleCreateNewGameResponse(response);
                    break;
                case WaitingForStartGameResponse:
                    HandleStartGameResponse(response);
                    break;
                case WaitingForStopGameResponse:
                    HandleStopGameResponse(response);
                    break;
                case WaitingForMakeGuessResponse:
                    HandleMakeGuessResponse(response);
                    break;
                case WaitingForGetAllGamesResponse:
                    HandleGetAllGamesResponse(response);
                    break;
                case WaitingForGetAllPlayedGamesResponse:
                    HandleGetAllPlayedGamesResponse(response);
                    break;
                case WaitingForGetAllUnPlayedGamesResponse:
                    HandleGetAllUnPlayedGamesResponse(response);
                    break;
                case WaitingForGetUserProfileResponse:
                    HandleGetUserProfileResponse(response);
                    break;
            }
        }catch (Exception e) {
            System.out.println("Error by translating the response: " + e.getMessage());
        }
    }

    private void Register(){
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Registering Client:");
            System.out.print("Email:");
            String email = scanner.nextLine();
            System.out.print("Password:");
            String password = scanner.nextLine();
            System.out.print("Username:");
            String userName = scanner.nextLine();

            RequestBuilder rb = new RequestBuilder();
            HashMap<String, Object> params = new HashMap<>();
            //TODO: Create an Input for The UserName, Password and Email
            params.put("ipAddress", Inet4Address.getLocalHost().getHostAddress());
            params.put("hostname", Inet4Address.getLocalHost().getHostName());
            params.put("userName", userName);
            params.put("password", password);
            params.put("email", email);
            rb.Create("", "Register", params);
            String requestText = rb.Serialize();
            this.send(requestText);
            _waitingState = WaitingState.WaitingForRegisterResponse;
        }catch (Exception e) {
            System.out.println("Error by Registering: " + e.toString());
        }
    }
    private void Login(){
        try{
            Scanner scanner = new Scanner(System.in);
            System.out.println("Login:");
            System.out.print("Username:");
            String userName = scanner.nextLine();
            System.out.print("Password:");
            String password = scanner.nextLine();

            RequestBuilder rb = new RequestBuilder();
            HashMap<String, Object> params = new HashMap<>();

            params.put("userName", userName);
            params.put("password", password);
            params.put("ipAddress", Inet4Address.getLocalHost().getHostAddress());
            params.put("hostname", Inet4Address.getLocalHost().getHostName());

            rb.Create(sessionString, "Login", params);
            String requestText = rb.Serialize();
            this.send(requestText);
            _waitingState = WaitingState.WaitingForLoginResponse;
        }catch (Exception e) {
            System.out.println("Error by Logging in: " + e.toString());
        }
    }
    private void CreateNewGame(){
        try{
            Scanner scanner = new Scanner(System.in);
            System.out.println("CreateNewGame:");
            System.out.print("Word:");
            String word = scanner.nextLine();
            System.out.print("Level:");
            String level = scanner.nextLine();

            RequestBuilder rb = new RequestBuilder();
            HashMap<String, Object> params = new HashMap<>();
            params.put("userID", userID);
            params.put("word", word);
            params.put("Level", Integer.parseInt(level));
            rb.Create(sessionString, "CreateNewGame", params);
            String requestText = rb.Serialize();
            this.send(requestText);
            _waitingState = WaitingState.WaitingForCreateGameResponse;
        }catch (Exception e) {
            System.out.println("Error by Creating new Game: " + e.toString());
        }
    }
    private void GetAllGames(){
        try{
            RequestBuilder rb = new RequestBuilder();
            HashMap<String, Object> params = new HashMap<>();
            rb.Create(sessionString, "GetAllGames", params);
            String requestText = rb.Serialize();
            this.send(requestText);
            _waitingState = WaitingState.WaitingForGetAllGamesResponse;
        }catch (Exception e) {
            System.out.println("Error by fetching all Games: " + e.toString());
        }
    }
    private void GetAllPlayedGames(){
        try{
            RequestBuilder rb = new RequestBuilder();
            HashMap<String, Object> params = new HashMap<>();
            params.put("userID", userID);
            rb.Create(sessionString, "GetAllPlayedGames", params);
            String requestText = rb.Serialize();
            this.send(requestText);
            _waitingState = WaitingState.WaitingForGetAllPlayedGamesResponse;
        }catch (Exception e) {
            System.out.println("Error by fetching all Played Games: " + e.toString());
        }
    }
    private void GetAllUnPlayedGames(){
        try{
            RequestBuilder rb = new RequestBuilder();
            HashMap<String, Object> params = new HashMap<>();
            params.put("userID", userID);
            rb.Create(sessionString, "GetAllUnPlayedGames", params);
            String requestText = rb.Serialize();
            this.send(requestText);
            _waitingState = WaitingState.WaitingForGetAllUnPlayedGamesResponse;
        }catch (Exception e) {
            System.out.println("Error by fetching all UnPlayed Games: " + e.toString());
        }
    }
    private void StartGame(){
        try{
            Scanner scanner = new Scanner(System.in);
            System.out.println("Start Game:");
            System.out.print("gameID:");
            Integer gameID = scanner.nextInt();

            RequestBuilder rb = new RequestBuilder();
            HashMap<String, Object> params = new HashMap<>();
            params.put("userID", userID);
            // TODO: Input for the GameID
            params.put("gameID", gameID);
            rb.Create(sessionString, "StartGame", params);
            String requestText = rb.Serialize();
            this.send(requestText);
            _waitingState = WaitingState.WaitingForStartGameResponse;
        }catch (Exception e) {
            System.out.println("Error by Creating new Game: " + e.toString());
        }
    }
    private void StopGame(){
        try{
            RequestBuilder rb = new RequestBuilder();
            HashMap<String, Object> params = new HashMap<>();
            params.put("userID", userID);

            rb.Create(sessionString, "StopGame", params);
            String requestText = rb.Serialize();
            this.send(requestText);
            _waitingState = WaitingState.WaitingForStopGameResponse;
        }catch (Exception e) {
            System.out.println("Error by trying to Stop the Current Game: " + e.toString());
        }
    }
    private void MakeGuess(){
        try{
            Scanner scanner = new Scanner(System.in);
            System.out.println("Start Game:");
            System.out.print("letter:");
            String letter = scanner.next();

            RequestBuilder rb = new RequestBuilder();
            HashMap<String, Object> params = new HashMap<>();
            params.put("userID", userID);
            //TODO: Input for the Next Letter
            params.put("letter", letter.charAt(0));

            rb.Create(sessionString, "MakeGuess", params);
            String requestText = rb.Serialize();
            this.send(requestText);
            _waitingState = WaitingState.WaitingForMakeGuessResponse;
        }catch (Exception e) {
            System.out.println("Error by trying to make a new Guess: " + e.toString());
        }
    }
    private void GetUserProfile(){
        try{
            RequestBuilder rb = new RequestBuilder();
            HashMap<String, Object> params = new HashMap<>();
            params.put("userID", userID);


            rb.Create(sessionString, "GetUserProfile", params);
            String requestText = rb.Serialize();
            this.send(requestText);
            _waitingState = WaitingState.WaitingForGetUserProfileResponse;
        }catch (Exception e) {
            System.out.println("Error by trying to fetching the User Profile: " + e.toString());
        }
    }
    private void HandleRegisterResponse(Response response) {
        if(response.Object != null){
            if(response.Object instanceof UserInfo){
                sessionString = ((UserInfo)response.Object).SessionString;
                userID = ((UserInfo)response.Object).UserID;
                System.out.println("+++Registration Success => Your User ID: " + userID + "+++");
                return;
            }
        }
        _waitingState = WaitingState.None;
        System.out.println("!!!!---ERROR---!!!!");
    }
    private void HandleLoginResponse(Response response) {
        if(response.Object != null){
            if(response.Object instanceof UserInfo){
                sessionString = ((UserInfo)response.Object).SessionString;
                userID = ((UserInfo)response.Object).UserID;
                System.out.println("+++ Login Success => Your User ID: " + userID + "+++");
                return;
            }
        }
        _waitingState = WaitingState.None;
        System.out.println("!!!!---ERROR---!!!!");
    }
    private void HandleCreateNewGameResponse(Response response) {
        if(response.Object != null){
            if((boolean)response.Object){
                System.out.println("+++Create New Game Success+++");
                _waitingState = WaitingState.None;
                return;
            }
        }
        System.out.println("!!!!---ERROR---!!!!");
        _waitingState = WaitingState.None;
    }
    private void HandleGetAllGamesResponse(Response response) {
        if(response.Object != null){
            if(response.Object instanceof ListModelGame){
                ListModelGame model = (ListModelGame)response.Object;
                System.out.println("+++Get all Games Success+++");
                model.Games.toFirst();
                while (model.Games.hasAccess()){
                    System.out.println("Game: ID:" + model.Games.getContent().ID + " Word-Length:" + model.Games.getContent().Word.length() + " Level:" + model.Games.getContent().Level);
                    model.Games.next();
                }
                return;
            }
        }
        System.out.println("!!!!---ERROR---!!!!");
        _waitingState = WaitingState.None;
    }
    private void HandleGetAllPlayedGamesResponse(Response response){
        if(response.Object != null){
            if(response.Object instanceof ListModelGame){
                ListModelGame model = (ListModelGame)response.Object;
                System.out.println("+++Get all PlayedGames Success+++");
                model.Games.toFirst();
                while (model.Games.hasAccess()){
                    System.out.println("Game: ID:" + model.Games.getContent().ID + " Word-Length:" + model.Games.getContent().Word.length() + " Level:" + model.Games.getContent().Level);
                    model.Games.next();
                }
                return;
            }
        }
        System.out.println("!!!!---ERROR---!!!!");
        _waitingState = WaitingState.None;
    }
    private void HandleGetAllUnPlayedGamesResponse(Response response){
        if(response.Object != null){
            if(response.Object instanceof ListModelGame){
                ListModelGame model = (ListModelGame)response.Object;
                System.out.println("+++Get all UnPlayedGames Success+++");
                model.Games.toFirst();
                while (model.Games.hasAccess()){
                    System.out.println("Game: ID:" + model.Games.getContent().ID + " Word-Length:" + model.Games.getContent().Word.length() + " Level:" + model.Games.getContent().Level);
                    model.Games.next();
                }
                return;
            }
        }
        System.out.println("!!!!---ERROR---!!!!");
        _waitingState = WaitingState.None;
    }
    private void HandleStartGameResponse(Response response){
        if(response.Object != null){
            if((boolean)response.Object){
                System.out.println("+++Game Starting Success+++");
                return;
            }
        }
        System.out.println("!!!!---ERROR---!!!!");
        _waitingState = WaitingState.None;
    }
    private void HandleStopGameResponse(Response response){
        if(response.Object != null){
            if(response.Object instanceof GameStatistics){
                GameStatistics gameStatistics = (GameStatistics)response.Object;
                System.out.println("+++Game Stopped Success+++");
                System.out.println("GameStatistics:");
                System.out.println("StartedAt:" + gameStatistics.StartedAt);
                System.out.println("MaxTries:" + gameStatistics.MaxTries);
                System.out.println("WrongTries:" + gameStatistics.WrongTries);
                System.out.println("Won:" + gameStatistics.Won);
                System.out.println("CorrectTries:" + gameStatistics.CorrectTries);
                return;
            }
        }
        System.out.println("!!!!---ERROR---!!!!");
        _waitingState = WaitingState.None;
    }
    private void HandleMakeGuessResponse(Response response){
        if(response.Object != null){
            if(response.Object instanceof GameStatistics){
                GameStatistics gameStatistics = (GameStatistics)response.Object;
                System.out.println("+++Game Make Guess Success+++");
                System.out.println("GameStatistics:");
                System.out.println("StartedAt:" + gameStatistics.StartedAt);
                System.out.println("MaxTries:" + gameStatistics.MaxTries);
                System.out.println("WrongTries:" + gameStatistics.WrongTries);
                System.out.println("Won:" + gameStatistics.Won);
                System.out.println("CorrectTries:" + gameStatistics.CorrectTries);

                if(gameStatistics.CorrectTries + gameStatistics.WrongTries >= gameStatistics.MaxTries){
                    System.out.println("YOU LOST");
                }else if(gameStatistics.Won){
                    System.out.println("YOU WON");
                }
                return;
            }
        }
        System.out.println("!!!!---ERROR---!!!!");
        _waitingState = WaitingState.None;
    }
    private void HandleGetUserProfileResponse(Response response){
        if(response.Object != null){
            if(response.Object instanceof User){
                User user = (User)response.Object;
                System.out.println("+++Get User Profile Success+++");
                System.out.println("Your Profile:");
                System.out.println("ID:" + user.ID);
                System.out.println("Username:" + user.UserName);
                System.out.println("Email:" + user.Email);
                System.out.println("Password:" + user.Password);
                System.out.println("Current Game:" + user.CurrentGame.Game.ID);

                return;
            }
        }
        System.out.println("!!!!---ERROR---!!!!");
        _waitingState = WaitingState.None;
    }



    public static void main(String[] args){
        ClientController c = new ClientController("127.0.0.1", 8000);
        boolean running = true;
        Scanner scanner = new Scanner(System.in);
        while(running){
            System.out.println("Hangman");
            System.out.println("Main-Menu");
            System.out.println("0. Exit");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Get User Profile");
            System.out.println("4. Create New Game");
            System.out.println("5. Print All Games");
            System.out.println("6. Print All Played Games");
            System.out.println("7. Print All Unplayed Games");
            System.out.println("8. Start Game");
            System.out.println("9. Make Guess");
            System.out.println("10. Stop Game");
            String cmd = scanner.nextLine();
            switch(cmd){
                case "0" :
                    running = false;
                    break;
                case "1" :
                    c.Register();
                    break;
                case "2" :
                    c.Login();
                    break;
                case "3" :
                    c.GetUserProfile();
                    break;
                case "4" :
                    c.CreateNewGame();
                    break;
                case "5" :
                    c.GetAllGames();
                    break;
                case "6" :
                    c.GetAllPlayedGames();
                    break;
                case "7" :
                    c.GetAllUnPlayedGames();
                    break;
                case "8" :
                    c.StartGame();
                    break;
                case "9" :
                    c.MakeGuess();
                    break;
                case "10" :
                    c.StopGame();
                    break;
            }
        }
    }



    private enum WaitingState{
        None,
        WaitingForRegisterResponse,
        WaitingForLoginResponse,
        WaitingForGetUserProfileResponse,
        WaitingForCreateGameResponse,
        WaitingForStartGameResponse,
        WaitingForStopGameResponse,
        WaitingForMakeGuessResponse,
        WaitingForGetAllGamesResponse,
        WaitingForGetAllUnPlayedGamesResponse,
        WaitingForGetAllPlayedGamesResponse

    }
}

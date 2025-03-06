package Client;

import Abitur.Client;
import Abitur.List;
import Server.Models.Game;
import Server.Models.GameStatistics;
import Server.Models.Protocol.Response;
import Server.Models.Protocol.UserInfo;
import Server.Services.Protocol.RequestBuilder;
import Server.Services.Protocol.ResponseBuilder;
import Server.Services.Protocol.ResponseDecoder;

import java.net.Inet4Address;
import java.util.ArrayList;
import java.util.HashMap;


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

            RequestBuilder rb = new RequestBuilder();
            HashMap<String, Object> params = new HashMap<>();
            //TODO: Create an Input for The UserName, Password and Email
            params.put("ipAddress", Inet4Address.getLocalHost().getHostAddress());
            params.put("hostname", Inet4Address.getLocalHost().getHostName());
            params.put("userName", "Florian");
            params.put("password", "Florian-12345");
            params.put("email", "fteetz@outlook.de");
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
            RequestBuilder rb = new RequestBuilder();
            HashMap<String, Object> params = new HashMap<>();
            //TODO: Create an Input for The UserName and Password
            params.put("userName", "Florian");
            params.put("password", "Florian-12345");
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
            RequestBuilder rb = new RequestBuilder();
            HashMap<String, Object> params = new HashMap<>();
            params.put("userID", userID);
            params.put("word", "Gleisbettreinigungsmaschiene");
            params.put("Level", 1);
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
            RequestBuilder rb = new RequestBuilder();
            HashMap<String, Object> params = new HashMap<>();
            params.put("userID", userID);
            // TODO: Input for the GameID
            params.put("gameID", 0);
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
            RequestBuilder rb = new RequestBuilder();
            HashMap<String, Object> params = new HashMap<>();
            params.put("userID", userID);
            //TODO: Input for the Next Letter
            params.put("letter", 'A');

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
                //TODO: Print that Registering was a Success
            }
        }
        _waitingState = WaitingState.None;
    }
    private void HandleLoginResponse(Response response) {
        if(response.Object != null){
            if(response.Object instanceof UserInfo){
                sessionString = ((UserInfo)response.Object).SessionString;
                userID = ((UserInfo)response.Object).UserID;
                //TODO: Print that Logging in was a Success
            }
        }
        _waitingState = WaitingState.None;
    }
    private void HandleCreateNewGameResponse(Response response) {
        if(response.Object != null){
            if((boolean)response.Object){
                //TODO: Print that creating a new game was a success:
                _waitingState = WaitingState.None;
                return;
            }
        }
        _waitingState = WaitingState.None;
    }
    private void HandleGetAllGamesResponse(Response response) {
        if(response.Object != null){
            if(response.Object instanceof List<?>){
                List<Game> allGames = (List<Game>)response.Object;
                //TODO: Print all Games out.
            }
        }
        _waitingState = WaitingState.None;
    }
    private void HandleGetAllPlayedGamesResponse(Response response){
        if(response.Object != null){
            if(response.Object instanceof List<?>){
                List<Game> allPlayedGames = (List<Game>)response.Object;
                //TODO: Print all Games out.
            }
        }
        _waitingState = WaitingState.None;
    }
    private void HandleGetAllUnPlayedGamesResponse(Response response){
        if(response.Object != null){
            if(response.Object instanceof List<?>){
                List<Game> allUnPlayedGames = (List<Game>)response.Object;
                //TODO: Print all Games out.
            }
        }
        _waitingState = WaitingState.None;
    }
    private void HandleStartGameResponse(Response response){
        if(response.Object != null){
            if((boolean)response.Object){
                //TODO: Print that the Game has started
            }
        }
        _waitingState = WaitingState.None;
    }
    private void HandleStopGameResponse(Response response){
        if(response.Object != null){
            if(response.Object instanceof GameStatistics){

                //TODO: Print the final Game Statistics and that stopping the game was successfully
            }
        }
        _waitingState = WaitingState.None;
    }
    private void HandleMakeGuessResponse(Response response){
        if(response.Object != null){
            if(response.Object instanceof GameStatistics){
                //TODO: Print the new Game Statistics and if the guess was correct.
            }
        }
        _waitingState = WaitingState.None;
    }
    private void HandleGetUserProfileResponse(Response response){}



    public static void main(String[] args){
        ClientController c = new ClientController("127.0.0.1", 8000);
        c.Register();
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

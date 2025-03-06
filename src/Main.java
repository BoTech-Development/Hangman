import Client.ClientController;
import Server.Controller.ServerController;
import Server.Models.Game;
import Server.Models.GameStatistics;
import Server.Models.Protocol.Request;
import Server.Models.Protocol.Response;
import Server.Models.User;
import Server.Services.Protocol.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Scanner;

public class Main {
    public static void main(String[] args)
    {
       /*

        User user = new User("Florian128", "Florian-12345", "fteetz@outlook.de");
        user.CurrentGame = new GameStatistics();
        user.CurrentGame.Game = new Game();
        user.CurrentGame.Game.ID = 1;
        user.CurrentGame.Game.Level = 3;
        user.CurrentGame.Game.Word = "DifficultWord";
        user.CurrentGame.MaxTries = 10;
        user.CurrentGame.Won = false;
        user.CurrentGame.WrongTries = 5;
        user.CurrentGame.StartedAt = LocalDateTime.now();



        ResponseBuilder rb = new ResponseBuilder();
        rb.Create(3723, user);
        String serialized = rb.Serialize();

        ResponseDecoder rd = new ResponseDecoder();
        Response rp = rd.DeserializeResponse(serialized);

        RequestBuilder rb1 = new RequestBuilder();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("User", user);


        rb1.Create("UniqueSession", "Login", hashMap);
        String requestString = rb1.Serialize();

        RequestDecoder rd1 = new RequestDecoder();
        Request request = rd1.DeserializeRequest(requestString);

        */
        Scanner sc = new Scanner(System.in);
        String type = sc.next();
        if(type.equals("c")){
            ClientController cC = new ClientController("127.0.0.1", 8000);

        }else if(type.equals("s")) {
            ServerController Server = new ServerController(8000);
        }
    }
    public void RegisterTest(){

    }
}


import Abitur.List;
import Client.ClientController;
import Server.Controller.ServerController;
import Server.Models.Game;
import Server.Models.GameStatistics;
import Server.Models.Protocol.Request;
import Server.Models.Protocol.Response;
import Server.Models.User;
import Server.Services.Protocol.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Main {
    public static void main(String[] args)
    {

       new Main();
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
        /*Scanner sc = new Scanner(System.in);
        String type = sc.next();
        if(type.equals("c")){
            ClientController cC = new ClientController("127.0.0.1", 8000);

        }else if(type.equals("s")) {
            ServerController Server = new ServerController(8000);
        }*/
    }
    public Main(){
       /* User user = new User();
        user.UserName = "FloFlo";
        user.Password = "123";
        user.PlayedGames = new List<>();
        Game game = new Game();
        game.ID = 0;
        game.Word = "Hello";
        game.Level = 1;
        Game game2 = new Game();
        game2.ID = 1;
        game2.Word = "World";
        game2.Level = 2;
        user.PlayedGames.append(game);
        user.PlayedGames.append(game2);


        ResponseBuilder responseBuilder = new ResponseBuilder();
        responseBuilder.Create(0, user);
        String ser = responseBuilder.Serialize();*/

        new DecoderBase().DecodePropertiesString("ID=0,UserName=FloFlo,Password=123,Email=a,EmailVerified=false,CurrentGame={Game={ID=5,Word=,Level=0},WrongTries=0,CorrectTries=0,MaxTries=0,StartedAt=2025-03-23T16:06:44.773700400,Won=false},PlayedGames={{ID=0,Word=Hello,Level=1},{ID=1,Word=World,Level=2}}", User.class);

    }
    public class ParentClass{
        public int Age = 10;
        public String T = "Hellord";
         public List<SubClass> SubClasses = new List<>();
    }
    public class SubClass{
        public String Name = "";
        public SubClass(){}
        public SubClass(String name){this.Name = name;}
    }

   /*
    public void RemoveDuplicatedContentStrings(BracketContent current) {
        String newContentString = current.Content;
        current.Children.toFirst();
        while (current.Children.hasAccess()) {
            newContentString = newContentString.replace(current.Children.getContent().Content, "");
            current.Children.next();
        }
        // do the same for each child
        current.Content = newContentString;
        current.Children.toFirst();
        while (current.Children.hasAccess()) {
            RemoveDuplicatedContentStrings(current.Children.getContent());
            current.Children.next();

        }
    }*/
/*
    static class BracketContent {
        private String content;

        public BracketContent(String content) {
            this.content = content;
        }

        public String getContent() {
            return content;
        }

        @Override
        public String toString() {
            return "BracketContent{" +
                    "content='" + content + '\'' +
                    '}';
        }
    }

    public static java.util.List<BracketContent> parseNestedBrackets(String input) {
        java.util.List<BracketContent> result = new java.util.ArrayList<>();
        parse(input, result, 0);
        return result;
    }

    private static int parse(String input, java.util.List<BracketContent> result, int startIndex) {
        StringBuilder currentContent = new StringBuilder();
        int i = startIndex;

        while (i < input.length()) {
            char c = input.charAt(i);
            if (c == '{') {
                // Recursively process nested brackets
                i = parse(input, result, i + 1);
            } else if (c == '}') {
                // End of current bracket
                result.add(new BracketContent(currentContent.toString().trim()));
                return i + 1; // Return the index after closing bracket
            } else {
                // Append other characters to the current content
                currentContent.append(c);
            }
            i++;
        }

        return i;
    }*/

}
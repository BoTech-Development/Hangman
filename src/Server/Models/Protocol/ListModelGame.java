package Server.Models.Protocol;

import Abitur.List;
import Server.Annotations.SerializableCollection;
import Server.Models.Game;

public class ListModelGame {
    public String Name = "List";
    @SerializableCollection(GenericType = Game.class)
    public List<Game> Games = new List<>();
    public ListModelGame(){}
    public ListModelGame(List<Game> Games){this.Games = Games;}
}

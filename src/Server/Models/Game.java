package Server.Models;

public class Game
{
    public int ID = -1;
    public String Word = "";
    public int Level = 0;
    public Game(){

    }
    public int GetMaxCountOfTries()
    {
        return Word.length() * Level;
    }
}

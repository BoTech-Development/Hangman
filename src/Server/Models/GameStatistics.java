package Server.Models;

import java.time.LocalDateTime;

public class GameStatistics
{
    public Game Game = new Game();
    public int WrongTries = 0;
    public int MaxTries = 0;
    public LocalDateTime StartedAd = LocalDateTime.now();
    public boolean Won = false;

}

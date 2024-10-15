import game.Game;
import players.Player;
import players.SmartPlayer;

import java.util.ArrayList;

public class main {

    public static void main(String[] args) throws CloneNotSupportedException {
        ArrayList<Player> players = new ArrayList<>();
        players.add(new SmartPlayer(0));
        players.add(new SmartPlayer(1));
        Game game = new Game(players);
        game.start();
    }
}
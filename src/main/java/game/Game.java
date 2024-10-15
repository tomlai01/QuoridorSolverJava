package game;

import players.Player;

import java.util.ArrayList;

public class Game {
    ArrayList<Player> players;

    public Game(ArrayList<Player> players) {
        this.players = players;
    }

    public void start() throws CloneNotSupportedException {
        State state = new State(players.size());
        System.out.println(state);
        while (state.existsWinners().isEmpty() || state.turn != 0) {
            ArrayList<State> possibleNextState = state.neighbors();
            State playedState = players.get(state.turn).play(state);
            if (!possibleNextState.contains(playedState)) {
                throw new RuntimeException("Player "+state.turn+" tried to play an illegal move");
            }
            state = playedState;
            System.out.println(state);
        }
        System.out.println("Player "+state.existsWinners().toString()+" won!");
    }
}


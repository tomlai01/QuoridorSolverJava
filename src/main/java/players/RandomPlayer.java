package players;

import game.State;

import java.util.ArrayList;
import java.util.Random;

public class RandomPlayer implements Player {
    Random rand = new Random();

    public RandomPlayer() {
    }

    @Override
    public State play(State state) throws CloneNotSupportedException {
        ArrayList<State> states = state.neighbors();
        return (State)states.get(this.rand.nextInt(states.size()));
    }
}
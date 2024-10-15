package players;

import game.State;

public interface Player {
    State play(State var1) throws CloneNotSupportedException;
}


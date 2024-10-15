package players;

import java.util.ArrayList;
import java.util.HashMap;

import game.Position;
import game.State;

public class SmartPlayer implements Player {
    int id;
    HashMap<State, Integer> evaluations = new HashMap<>();
    ArrayList<ArrayList<Position>> paths;

    public SmartPlayer(int id) {
        this.id = id;
    }

    @Override
    public State play(State state) throws CloneNotSupportedException {
        this.paths = new ArrayList<ArrayList<Position>>();
        paths.add(state.shortestPath(0));
        paths.add(state.shortestPath(1));
        this.evaluations = new HashMap<>();
        return this.alphaBeta(state, 3, Integer.MIN_VALUE, Integer.MAX_VALUE, false);
    }

    private boolean isTerminalState(State state) {
        return !state.existsWinners().isEmpty() && state.turn == 0;
    }

    private int evaluate(State state) {
        int playerDist;
        int otherDist;
        playerDist = state.shortestPath(this.id).size();
        otherDist = state.shortestPath((this.id + 1) % 2).size();
        return playerDist - otherDist;
    }

    private State alphaBeta(State state, int depth, int alpha, int beta, boolean isMaximizingPlayer) throws CloneNotSupportedException {
        // state value already computed
        if (evaluations.containsKey(state)) {
            return state;
        }
        // max depth reached or terminal state
        if (depth == 0 || isTerminalState(state)) {
            evaluations.put(state, evaluate(state));
            return state;
        }
        State bestMove = null;
        // maximization
        if (isMaximizingPlayer) {
            int maxEval = Integer.MIN_VALUE;
            for (State neighbor : state.neighbors()) {
                State newState = this.alphaBeta(neighbor, depth - 1, alpha, beta, false);
                if (evaluations.get(newState) > maxEval) {
                    maxEval = evaluations.get(newState);
                    bestMove = newState;
                }
                alpha = Math.max(alpha, evaluations.get(newState));
                if (beta <= alpha) {
                    break;
                }
            }
            evaluations.put(bestMove, maxEval);
            // minimization
        } else {
            int minEval = Integer.MAX_VALUE;
            for (State neighbor : state.neighbors()) {
                State newState = this.alphaBeta(neighbor, depth - 1, alpha, beta, true);
                if (evaluations.get(newState) < minEval) {
                    minEval = evaluations.get(newState);
                    bestMove = neighbor;
                }
                beta = Math.min(beta, evaluations.get(newState));
                if (beta <= alpha) {
                    break;
                }
            }
            evaluations.put(bestMove, minEval);
        }
        return bestMove;
    }

    boolean isCorrectPath(State state, ArrayList<Position> path) {
        for (int i = 0; i < path.size() - 1; i++) {
            if (state.throughWall(path.get(i), path.get(i + 1))) return false;
        }
        return true;
    }
}

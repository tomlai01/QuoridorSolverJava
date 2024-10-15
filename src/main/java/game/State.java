package game;

import java.util.*;

import static java.lang.Math.abs;

/**
 *
 */
public class State implements Cloneable {
    public int turn;
    public Position[] playerPositions;
    public int[][] playerGoals;
    public boolean[][] horizontalWalls; // horizontal walls placed
    public boolean[][] verticalWalls;
    public int[] wallsRemaining;
    public int round;

    public State(int nPlayers) {
        assert nPlayers == 2 || nPlayers == 4;
        this.turn = 0;
        this.round = 0;
        this.horizontalWalls = new boolean[8][8];
        this.verticalWalls = new boolean[8][8];
        if (nPlayers == 2) {
            this.playerPositions = new Position[]{new Position(0, 4), new Position(8, 4)};
            this.playerGoals = new int[][]{{0, 8}, {0, 0}};
            this.wallsRemaining = new int[]{10, 10};
        } else {
            this.playerPositions = new Position[]{
                    new Position(0, 4),
                    new Position(4, 8),
                    new Position(8, 4),
                    new Position(4, 0)};
            this.playerGoals = new int[][]{{0, 8}, {1, 0}, {0, 0}, {1, 8}};
            this.wallsRemaining = new int[]{5, 5, 5, 5};
        }
    }

    /**
     * check if it exists winners
     *
     * @return list of winners
     */
    public ArrayList<Integer> existsWinners() {
        ArrayList<Integer> winners = new ArrayList<>();
        for (int i = 0; i < this.playerPositions.length; ++i) {
            int goalIndex = this.playerGoals[i][0];
            int goalValue = this.playerGoals[i][1];
            if (this.playerPositions[i].getValue(goalIndex) == goalValue) {
                winners.add(i);
            }
        }
        return winners;
    }

    /**
     * Get every next possible states after the player whose turn it is has played
     *
     * @return list of neighbors
     * @throws CloneNotSupportedException
     */
    public ArrayList<State> neighbors() throws CloneNotSupportedException {
        ArrayList<State> neighbors = new ArrayList<>();
        Position currPos = this.playerPositions[this.turn];
        HashSet<Position> otherPos = new HashSet<>();
        for (Position playerPosition : playerPositions) {
            if (currPos != playerPosition) {
                otherPos.add(playerPosition);
            }
        }
        // moves
        int[][] movements = new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        for (int[] move : movements) {
            Position newPos = new Position(currPos.getX() + move[0], currPos.getY() + move[1]);
            if (this.canMove(currPos, newPos)) {
                if (otherPos.contains(newPos)) {
                    Position jumpPos = new Position(newPos.getX() + move[0], newPos.getY() + move[1]);
                    for (Position finalPos : this.handleFacing(currPos, newPos, jumpPos)) {
                        State clone = this.clone();
                        clone.playerPositions[this.turn] = finalPos;
                        clone.turn = (this.turn + 1) % this.playerPositions.length;
                        if (clone.turn == 0) ++clone.round;
                        neighbors.add(clone);
                    }
                } else {
                    State clone = this.clone();
                    clone.playerPositions[this.turn] = newPos;
                    clone.turn = (this.turn + 1) % this.playerPositions.length;
                    if (clone.turn == 0) ++clone.round;
                    neighbors.add(clone);
                }
            }
        }
        // placing walls
        if (this.wallsRemaining[this.turn] > 0) {
            ArrayList<HashSet<Position>> wallsOnPath = new ArrayList<>();
            wallsOnPath.add(new HashSet<>());
            wallsOnPath.add(new HashSet<>());
            for (int i = 0; i < playerPositions.length; i++) {
                ArrayList<HashSet<Position>> walls = wallsOnPath(shortestPath(i));
                wallsOnPath.get(0).addAll(walls.get(0));
                wallsOnPath.get(1).addAll(walls.get(1));
            }
            for (int x = 0; x < 8; ++x) {
                for (int y = 0; y < 8; ++y) {
                    if (this.canPlaceWall(new Position(x, y), true, wallsOnPath)) {
                        State clone = this.clone();
                        clone.horizontalWalls[x][y] = true;
                        clone.wallsRemaining[this.turn] = this.wallsRemaining[this.turn] - 1;
                        clone.turn = (this.turn + 1) % this.playerPositions.length;
                        if (clone.turn == 0) ++clone.round;
                        neighbors.add(clone);
                    }
                    if (this.canPlaceWall(new Position(x, y), false, wallsOnPath)) {
                        State clone = this.clone();
                        clone.verticalWalls[x][y] = true;
                        clone.wallsRemaining[this.turn] = this.wallsRemaining[this.turn] - 1;
                        clone.turn = (this.turn + 1) % this.playerPositions.length;
                        if (clone.turn == 0) ++clone.round;
                        neighbors.add(clone);
                    }
                }
            }
        }
        return neighbors;
    }

    /**
     * Find every position reachable in one turn from "pos" omitting the presence of other players
     *
     * @param pos the position from which to move
     * @return list of possible next positions
     */
    public ArrayList<Position> possibleNextPositions(Position pos) {
        ArrayList<Position> possibleNextPositions = new ArrayList<>();
        int[][] movements = new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        for (int[] move : movements) {
            Position newPos = new Position(pos.getX() + move[0], pos.getY() + move[1]);
            if (this.canMove(pos, newPos)) {
                possibleNextPositions.add(newPos);
            }
        }
        return possibleNextPositions;
    }


    /**
     * Check if there is a wall between the positions "from" and "to".
     * "from" and "to" are squares side by side non diagonal.
     *
     * @param from the position from which the player move
     * @param to   the position to which the player move
     * @return if there is a wall between "from" and "to"
     */
    public Boolean throughWall(Position from, Position to) {
        if (abs(from.getX() - to.getX()) != 0) { // vertical move
            int minX = Math.min(from.getX(), to.getX());
            return (from.getY() < 8 && this.horizontalWalls[minX][from.getY()])
                    || (from.getY() > 0 && this.horizontalWalls[minX][from.getY() - 1]);
        } // horizontal move
        int minY = Math.min(from.getY(), to.getY());
        return (from.getX() < 8 && this.verticalWalls[from.getX()][minY])
                || (from.getX() > 0 && this.verticalWalls[from.getX() - 1][minY]);
    }

    /**
     * Check if player can move from the position "from" to the position "to".
     * "from" and "to" are squares side by side non diagonal.
     *
     * @param from the start position
     * @param to   the final position
     * @return if the player can move
     */
    Boolean canMove(Position from, Position to) {
        assert from.getX() != to.getX() || from.getY() != to.getY(); // assert move
        assert abs(from.getX() - to.getX()) < 2;
        assert abs(from.getY() - to.getY()) < 2;
        assert from.getX() == to.getX() || from.getY() == to.getY();
        return 0 <= to.getX() && to.getX() <= 8 && 0 <= to.getY() && to.getY() <= 8 && !this.throughWall(from, to);
    }

    /**
     * Check if there is a wall adjacent to the wall at "position"
     *
     * @param position         the position to check
     * @param isHorizontalWall the type of wall to check
     * @return if there is an adjacent wall
     */
    Boolean existsAdjacentWall(Position position, boolean isHorizontalWall) {
        if (isHorizontalWall) {
            if (position.getY() > 1 && this.horizontalWalls[position.getX()][position.getY() - 2])
                return true; // check horizontal wall on left
            if (position.getY() < 6 && this.horizontalWalls[position.getX()][position.getY() + 2])
                return true; // check horizontal wall on right
            // check vertical walls above
            if (position.getX() > 0 && (
                    (position.getY() > 0 && this.verticalWalls[position.getX() - 1][position.getY() - 1])
                            || (position.getY() < 7 && this.verticalWalls[position.getX() - 1][position.getY() + 1])
                            || this.verticalWalls[position.getX() - 1][position.getY()])
            ) return true;
            // check vertical walls below
            if (position.getX() < 7 && (
                    (position.getY() > 0 && this.verticalWalls[position.getX() + 1][position.getY() - 1])
                            || (position.getY() < 7 && this.verticalWalls[position.getX() + 1][position.getY() + 1])
                            || this.verticalWalls[position.getX() + 1][position.getY()])
            ) return true;
            return (position.getY() > 0 && this.verticalWalls[position.getX()][position.getY() - 1]) // check vertical wall on left
                    || (position.getY() < 7 && this.verticalWalls[position.getX()][position.getY() + 1]); // check vertical wall on right
        } else {
            if (position.getX() > 1 && this.verticalWalls[position.getX() - 2][position.getY()])
                return true; // check vertical wall above
            if (position.getX() < 6 && this.verticalWalls[position.getX() + 2][position.getY()])
                return true; // check vertical wall below
            // check horizontal walls on left
            if (position.getY() > 0 && (
                    (position.getX() > 0 && this.horizontalWalls[position.getX() - 1][position.getY() - 1])
                            || (position.getX() < 7 && this.horizontalWalls[position.getX() + 1][position.getY() - 1])
                            || this.horizontalWalls[position.getX()][position.getY() - 1])
            ) return true;
            // check horizontal walls on right
            if (position.getY() < 7 && (
                    (position.getX() > 0 && this.horizontalWalls[position.getX() - 1][position.getY() + 1])
                            || (position.getX() < 7 && this.horizontalWalls[position.getX() + 1][position.getY() + 1])
                            || this.horizontalWalls[position.getX()][position.getY() + 1])
            ) return true;
            return (position.getX() > 0 && this.horizontalWalls[position.getX() - 1][position.getY()]) // horizontal wall above
                    || (position.getX() < 7 && this.horizontalWalls[position.getX() + 1][position.getY()]); // horizontal wall below
        }
    }

    /**
     * Use A* algorithm to find the shortest path for the player "i" to reach his goal
     *
     * @param i player id
     * @return the shortest path to the player goal or null if no path exists
     */
    public ArrayList<Position> shortestPath(int i) {
        HashMap<Position, Position> cameFrom = new HashMap<>();
        int[][] gScores = new int[9][9];
        int[][] fScores = new int[9][9];
        for (int j = 0; j < 9; j++) {
            Arrays.fill(gScores[j], Integer.MAX_VALUE);
            Arrays.fill(fScores[j], Integer.MAX_VALUE);
        }
        Position start = this.playerPositions[i];
        int[] goal = this.playerGoals[i];
        gScores[start.getX()][start.getX()] = 0;
        fScores[start.getX()][start.getY()] = abs(start.getValue(goal[0]) - goal[1]);
        PriorityQueue<Position> pq = new PriorityQueue<>(Comparator.comparingInt(pos -> fScores[pos.getX()][pos.getY()]));
        pq.add(start);
        int goalIndex = this.playerGoals[i][0];
        int goalValue = this.playerGoals[i][1];
        while (!pq.isEmpty()) {
            Position current = pq.poll();
            if (current.getValue(goalIndex) == goalValue) { // reach goal
                // reconstruct path
                ArrayList<Position> path = new ArrayList<>();
                while (!current.equals(start)) {
                    path.add(current);
                    current = cameFrom.get(current);
                }
                path.add(current);
                return path;
            }
            for (Position neighbor : this.possibleNextPositions(current)) {
                int nStep = gScores[current.getX()][current.getY()] + 1;
                if (nStep < gScores[neighbor.getX()][neighbor.getY()]) {
                    cameFrom.put(neighbor, current);
                    gScores[neighbor.getX()][neighbor.getY()] = nStep;
                    fScores[neighbor.getX()][neighbor.getY()] = nStep + abs(neighbor.getValue(goalIndex) - goalValue);
                    if (!pq.contains(neighbor)) {
                        pq.add(neighbor);
                    }
                }
            }
        }
        return new ArrayList<>();
    }

    /**
     * Check for each player if it exists a path to reach his goal
     *
     * @return exists solution for each player
     */
    Boolean existsSolutions() {
        for (int i = 0; i < this.playerPositions.length; ++i) {
            if (this.shortestPath(i).isEmpty()) return false;
        }
        return true;
    }

    /**
     * Find every wall that could bloc the path (Even path outside the board for optimization)
     *
     * @param path the path
     * @return list containing, in index 0, a set containing horizontal walls
     * and, in index 1, the set containing vertical walls
     */
    ArrayList<HashSet<Position>> wallsOnPath(ArrayList<Position> path) {
        ArrayList<HashSet<Position>> wallsOnPath = new ArrayList<>();
        wallsOnPath.add(new HashSet<>());
        wallsOnPath.add(new HashSet<>());
        for (int i = 0; i < path.size() - 1; i++) {
            Position currPos = path.get(i);
            Position nextPos = path.get(i + 1);
            if (currPos.getX() - nextPos.getX() != 0) { // vertical move
                int minX = Math.min(nextPos.getX(), currPos.getX());
                wallsOnPath.get(0).add(new Position(minX, currPos.getY()));
                wallsOnPath.get(0).add(new Position(minX, currPos.getY() - 1));
            } else { // horizontal move
                int minY = Math.min(nextPos.getY(), currPos.getY());
                wallsOnPath.get(1).add(new Position(currPos.getX(), minY));
                wallsOnPath.get(1).add(new Position(currPos.getX() - 1, minY));
            }
        }
        return wallsOnPath;
    }

    /**
     * Check if a wall can be placed at the "position"
     *
     * @param position         the position of the wall
     * @param isHorizontalWall type of the wall
     * @return if the wall can be placed
     */
    Boolean canPlaceWall(Position position, Boolean isHorizontalWall, ArrayList<HashSet<Position>> wallsOnPath) throws CloneNotSupportedException {
        // check if position conflicts with already placed walls
        if (horizontalWalls[position.getX()][position.getY()] || verticalWalls[position.getX()][position.getY()])
            return false;
        if (isHorizontalWall) {
            if (position.getY() > 0 && this.horizontalWalls[position.getX()][position.getY() - 1]) return false;
            if (position.getY() < 7 && this.horizontalWalls[position.getX()][position.getY() + 1]) return false;
        } else {
            if (position.getX() > 0 && this.verticalWalls[position.getX() - 1][position.getY()]) return false;
            if (position.getX() < 7 && this.verticalWalls[position.getX() + 1][position.getY()]) return false;
        }
        // check if new wall let a solution path for each player
        if (!existsAdjacentWall(position, isHorizontalWall)
                || (isHorizontalWall && !wallsOnPath.get(0).contains(position))
                || (!isHorizontalWall && !wallsOnPath.get(1).contains(position))
        ) return true; // no adjacent wall or don't bloc the solution paths of players
        else {
            State clone = this.clone();
            if (isHorizontalWall) {
                clone.horizontalWalls[position.getX()][position.getY()] = true;
            } else {
                clone.verticalWalls[position.getX()][position.getY()] = true;
            }
            return clone.existsSolutions();
        }
    }

    /**
     * Find the possible positions in the case when a player jump over another
     *
     * @param currPos   the current position of the player that play
     * @param concurPos the position of the other player over whom the player want to jump
     * @param jumPos    the position next to the concurrent position (where the player should arrived without obstacle)
     * @return list of the positions after jump
     */
    ArrayList<Position> handleFacing(Position currPos, Position concurPos, Position jumPos) {
        ArrayList<Position> finalPositions = new ArrayList<>();
        if (this.canMove(concurPos, jumPos)) {
            finalPositions.add(jumPos);
        } else {
            for (Position finalPos : possibleNextPositions(concurPos)) {
                if (!currPos.equals(finalPos)) {
                    finalPositions.add(finalPos);
                }
            }
        }
        return finalPositions;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            State state = (State) o;
            return this.turn == state.turn && Arrays.deepEquals(this.playerPositions, state.playerPositions) && Arrays.deepEquals(this.horizontalWalls, state.horizontalWalls) && Arrays.deepEquals(this.verticalWalls, state.verticalWalls) && Arrays.equals(this.wallsRemaining, state.wallsRemaining);
        } else {
            return false;
        }
    }

    public int hashCode() {
        return Objects.hash(this.turn, Arrays.deepHashCode(this.playerPositions), Arrays.deepHashCode(this.horizontalWalls), Arrays.deepHashCode(this.verticalWalls), Arrays.hashCode(this.wallsRemaining));
    }

    public String toString() {
        return "State{turn=" + turn + ", round=" + this.round + ", playerPositions=" + Arrays.deepToString(this.playerPositions) + ", playerGoals=" + Arrays.deepToString(this.playerGoals) + ", walls=" + Arrays.toString(this.wallsRemaining) + ", horizontalWalls=" + Arrays.deepToString(this.horizontalWalls) + ", verticalWalls=" + Arrays.deepToString(this.verticalWalls) + "}";
    }

    public State clone() throws CloneNotSupportedException {
        State clone = (State) super.clone();
        clone.playerPositions = this.playerPositions.clone();
        clone.playerGoals = new int[playerGoals.length][2];
        for (int i = 0; i < playerGoals.length; i++) {
            System.arraycopy(playerGoals[i], 0, clone.playerGoals[i], 0, playerGoals[i].length);
        }
        clone.horizontalWalls = new boolean[8][8];
        for (int i = 0; i < horizontalWalls.length; i++) {
            System.arraycopy(horizontalWalls[i], 0, clone.horizontalWalls[i], 0, horizontalWalls[i].length);
        }
        clone.verticalWalls = new boolean[8][8];
        for (int i = 0; i < verticalWalls.length; i++) {
            System.arraycopy(verticalWalls[i], 0, clone.verticalWalls[i], 0, verticalWalls[i].length);
        }
        clone.wallsRemaining = this.wallsRemaining.clone();
        return clone;
    }
}

package game;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;

import static org.junit.Assert.*;

public class TestState {
    State state2;
    State state4;

    @Before
    public void setUp() {
        state2 = new State(2);
        state4 = new State(4);
    }

    @Test
    public void testInit2() {
        assertEquals(0, state2.turn);
        assertArrayEquals(state2.playerPositions, new Position[]{new Position(0, 4), new Position(8, 4)});
        assertArrayEquals(state2.playerGoals, new int[][]{{0, 8}, {0, 0}});
        assertArrayEquals(state2.horizontalWalls, new boolean[8][8]);
        assertArrayEquals(state2.verticalWalls, new boolean[8][8]);
        assertArrayEquals(state2.wallsRemaining, new int[]{10, 10});
        assertEquals(0, state2.round);
    }

    @Test
    public void testInit4() {
        //TODO
    }

    @Test
    public void testExistsWinners2() {
        assertEquals(0, state2.existsWinners().size());
        state2.playerPositions[0] = new Position(8, 5);
        assertEquals(1, state2.existsWinners().size());
        state2.playerPositions[1] = new Position(0, 5);
        assertEquals(2, state2.existsWinners().size());
    }

    @Test
    public void testExistsWinners4() {
        //TODO
    }

    @Test
    public void testThroughWall() {
        // basic test
        assertFalse(state2.throughWall(new Position(4, 4), new Position(3, 4)));
        // side board checks
        assertFalse(state2.throughWall(new Position(0, 0), new Position(1, 0)));
        assertFalse(state2.throughWall(new Position(0, 0), new Position(0, 1)));
        assertFalse(state2.throughWall(new Position(0, 8), new Position(1, 8)));
        assertFalse(state2.throughWall(new Position(0, 8), new Position(0, 7)));
        assertFalse(state2.throughWall(new Position(8, 8), new Position(7, 8)));
        assertFalse(state2.throughWall(new Position(8, 8), new Position(8, 7)));
        assertFalse(state2.throughWall(new Position(8, 0), new Position(7, 0)));
        assertFalse(state2.throughWall(new Position(8, 0), new Position(8, 1)));
        // moves to north
        state2.horizontalWalls[3][3] = true;
        assertTrue(state2.throughWall(new Position(4, 4), new Position(3, 4)));
        state2.horizontalWalls[3][3] = false;
        state2.horizontalWalls[3][4] = true;
        assertTrue(state2.throughWall(new Position(4, 4), new Position(3, 4)));
        state2.horizontalWalls[3][4] = false;
        // moves to south
        state2.horizontalWalls[4][3] = true;
        assertTrue(state2.throughWall(new Position(4, 4), new Position(5, 4)));
        state2.horizontalWalls[4][3] = false;
        state2.horizontalWalls[4][4] = true;
        assertTrue(state2.throughWall(new Position(4, 4), new Position(5, 4)));
        state2.horizontalWalls[4][4] = false;
        // moves to east
        state2.verticalWalls[3][4] = true;
        assertTrue(state2.throughWall(new Position(4, 4), new Position(4, 5)));
        state2.verticalWalls[3][4] = false;
        state2.verticalWalls[4][4] = true;
        assertTrue(state2.throughWall(new Position(4, 4), new Position(4, 5)));
        state2.verticalWalls[4][4] = false;
        // moves to west
        state2.verticalWalls[3][3] = true;
        assertTrue(state2.throughWall(new Position(4, 4), new Position(4, 3)));
        state2.verticalWalls[3][3] = false;
        state2.verticalWalls[4][3] = true;
        assertTrue(state2.throughWall(new Position(4, 4), new Position(4, 3)));
        state2.verticalWalls[4][3] = false;
    }

    @Test
    public void testCanMove() {

    }

    @Test
    public void testExistsAdjacentWalls() {
        // basic tests
        assertFalse(state2.existsAdjacentWall(new Position(4, 4), true));
        assertFalse(state2.existsAdjacentWall(new Position(4, 4), false));
        // board sides
        assertFalse(state2.existsAdjacentWall(new Position(0, 0), true));
        assertFalse(state2.existsAdjacentWall(new Position(0, 0), false));
        assertFalse(state2.existsAdjacentWall(new Position(0, 7), true));
        assertFalse(state2.existsAdjacentWall(new Position(0, 7), false));
        assertFalse(state2.existsAdjacentWall(new Position(7, 7), true));
        assertFalse(state2.existsAdjacentWall(new Position(7, 7), false));
        assertFalse(state2.existsAdjacentWall(new Position(7, 0), true));
        assertFalse(state2.existsAdjacentWall(new Position(7, 0), false));
        // horizontal walls tests
        state2.horizontalWalls[4][2] = true;
        assertTrue(state2.existsAdjacentWall(new Position(4, 4), true));
        state2.horizontalWalls[4][2] = false;
        state2.horizontalWalls[4][6] = true;
        assertTrue(state2.existsAdjacentWall(new Position(4, 4), true));
        state2.horizontalWalls[4][6] = false;
        state2.verticalWalls[4][5] = true;
        assertTrue(state2.existsAdjacentWall(new Position(4, 4), true));
        state2.verticalWalls[4][5] = false;
        state2.verticalWalls[5][5] = true;
        assertTrue(state2.existsAdjacentWall(new Position(4, 4), true));
        state2.verticalWalls[5][5] = false;
        state2.verticalWalls[5][4] = true;
        assertTrue(state2.existsAdjacentWall(new Position(4, 4), true));
        state2.verticalWalls[5][4] = false;
        state2.verticalWalls[5][3] = true;
        assertTrue(state2.existsAdjacentWall(new Position(4, 4), true));
        state2.verticalWalls[5][3] = false;
        state2.verticalWalls[4][3] = true;
        assertTrue(state2.existsAdjacentWall(new Position(4, 4), true));
        state2.verticalWalls[4][3] = false;
        state2.verticalWalls[3][3] = true;
        assertTrue(state2.existsAdjacentWall(new Position(4, 4), true));
        state2.verticalWalls[3][3] = false;
        state2.verticalWalls[3][4] = true;
        assertTrue(state2.existsAdjacentWall(new Position(4, 4), true));
        state2.verticalWalls[3][4] = false;
        state2.verticalWalls[3][5] = true;
        assertTrue(state2.existsAdjacentWall(new Position(4, 4), true));
        state2.verticalWalls[3][5] = false;
        // vertical walls tests
        state2.verticalWalls[2][4] = true;
        assertTrue(state2.existsAdjacentWall(new Position(4, 4), false));
        state2.verticalWalls[2][4] = false;
        state2.verticalWalls[6][4] = true;
        assertTrue(state2.existsAdjacentWall(new Position(4, 4), false));
        state2.verticalWalls[6][4] = false;
        state2.horizontalWalls[4][5] = true;
        assertTrue(state2.existsAdjacentWall(new Position(4, 4), false));
        state2.horizontalWalls[4][5] = false;
        state2.horizontalWalls[5][5] = true;
        assertTrue(state2.existsAdjacentWall(new Position(4, 4), false));
        state2.horizontalWalls[5][5] = false;
        state2.horizontalWalls[5][4] = true;
        assertTrue(state2.existsAdjacentWall(new Position(4, 4), false));
        state2.horizontalWalls[5][4] = false;
        state2.horizontalWalls[5][3] = true;
        assertTrue(state2.existsAdjacentWall(new Position(4, 4), false));
        state2.horizontalWalls[5][3] = false;
        state2.horizontalWalls[4][3] = true;
        assertTrue(state2.existsAdjacentWall(new Position(4, 4), false));
        state2.horizontalWalls[4][3] = false;
        state2.horizontalWalls[3][3] = true;
        assertTrue(state2.existsAdjacentWall(new Position(4, 4), false));
        state2.horizontalWalls[3][3] = false;
        state2.horizontalWalls[3][4] = true;
        assertTrue(state2.existsAdjacentWall(new Position(4, 4), false));
        state2.horizontalWalls[3][4] = false;
        state2.horizontalWalls[3][5] = true;
        assertTrue(state2.existsAdjacentWall(new Position(4, 4), false));
        state2.horizontalWalls[3][5] = false;
    }

    @Test
    public void testPossibleNextPositions() {
        Position[] nextPositions = new Position[]{
                new Position(5, 4), 
                new Position(3, 4), 
                new Position(4, 5), 
                new Position(4, 3)};
        assertEquals(nextPositions, state2.possibleNextPositions(new Position(4,4)).toArray());
        state2.horizontalWalls[4][4] = true;
        assertEquals(3, state2.possibleNextPositions(new Position(4,4)).size());
        state2.horizontalWalls[4][4] = false;
        state2.horizontalWalls[3][4] = true;
        assertEquals(3, state2.possibleNextPositions(new Position(4,4)).size());
        state2.horizontalWalls[3][4] = false;
        state2.verticalWalls[4][4] = true;
        assertEquals(3, state2.possibleNextPositions(new Position(4,4)).size());
        state2.verticalWalls[4][4] = false;
        state2.verticalWalls[4][3] = true;
        assertEquals(3, state2.possibleNextPositions(new Position(4,4)).size());
        state2.verticalWalls[4][3] = false;
        assertEquals(2, state2.possibleNextPositions(new Position(0,0)).size());
        assertEquals(2, state2.possibleNextPositions(new Position(0,8)).size());
        assertEquals(2, state2.possibleNextPositions(new Position(8,8)).size());
        assertEquals(2, state2.possibleNextPositions(new Position(8,0)).size());
    }

    @Test
    public void testShortestPath2() {
        assertEquals(9, state2.shortestPath(0).size());
        assertEquals(9, state2.shortestPath(1).size());
        state2.horizontalWalls[4][4] = true;
        assertEquals(10, state2.shortestPath(0).size());
        assertEquals(10, state2.shortestPath(1).size());
        state2.horizontalWalls[4][2] = true;
        assertEquals(11, state2.shortestPath(0).size());
        assertEquals(11, state2.shortestPath(1).size());
        state2.horizontalWalls[4][0] = true;
        assertEquals(11, state2.shortestPath(0).size());
        assertEquals(11, state2.shortestPath(1).size());
        state2.horizontalWalls[4][6] = true;
        assertEquals(13, state2.shortestPath(0).size());
        assertEquals(13, state2.shortestPath(1).size());
        state2.horizontalWalls[3][7] = true;
        assertEquals(13, state2.shortestPath(0).size());
        assertEquals(15, state2.shortestPath(1).size());
        state2.verticalWalls[3][6] = true;
        assertEquals(new ArrayList<>(), state2.shortestPath(0));
        assertEquals(new ArrayList<>(), state2.shortestPath(1));
    }

    @Test
    public void testWallsOnPath() {
        ArrayList<Position> path = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            path.add(new Position(i, 4));
        }
        assertEquals(16, state2.wallsOnPath(path).get(0).size());
        assertEquals(0, state2.wallsOnPath(path).get(1).size());
        state2.horizontalWalls[4][4] = true;
        ArrayList<Position> path0 = state2.shortestPath(0);
        assertEquals(16, state2.wallsOnPath(path0).get(0).size());
        assertEquals(2, state2.wallsOnPath(path0).get(1).size());
        ArrayList<Position> path1 = state2.shortestPath(1);
        assertEquals(16, state2.wallsOnPath(path1).get(0).size());
        assertEquals(2, state2.wallsOnPath(path1).get(1).size());
    }

    @Test
    public void testCanPlaceWall() throws CloneNotSupportedException {
        ArrayList<HashSet<Position>> wallsOnPath = new ArrayList<>();
        wallsOnPath.add(new HashSet<>());
        wallsOnPath.add(new HashSet<>());
        for (int i = 0; i < state2.playerPositions.length; i++) {
            ArrayList<HashSet<Position>> walls = state2.wallsOnPath(state2.shortestPath(i));
            wallsOnPath.get(0).addAll(walls.get(0));
            wallsOnPath.get(1).addAll(walls.get(1));
        }
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                assertTrue(state2.canPlaceWall(new Position(i, j), true, wallsOnPath));
                assertTrue(state2.canPlaceWall(new Position(i, j), false, wallsOnPath));
            }
        }
        state2.horizontalWalls[4][4] = true;
        assertFalse(state2.canPlaceWall(new Position(4, 4), true, wallsOnPath));
        assertFalse(state2.canPlaceWall(new Position(4, 3), true, wallsOnPath));
        assertFalse(state2.canPlaceWall(new Position(4, 5), true, wallsOnPath));
        assertFalse(state2.canPlaceWall(new Position(4, 4), false, wallsOnPath));
        state2.horizontalWalls[4][4] = false;
        state2.verticalWalls[4][4] = true;
        assertFalse(state2.canPlaceWall(new Position(4, 4), false, wallsOnPath));
        assertFalse(state2.canPlaceWall(new Position(3, 4), false, wallsOnPath));
        assertFalse(state2.canPlaceWall(new Position(5, 4), false, wallsOnPath));
        assertFalse(state2.canPlaceWall(new Position(4, 4), true, wallsOnPath));
    }

    @Test
    public void testHandleFacing() {
        state2.playerPositions[1] = new Position(1,4);
        Position current = new Position(0,4);
        Position concurrent = new Position(1,4);
        Position jump = new Position(2,4);
        assertEquals(jump, state2.handleFacing(current, concurrent, jump).get(0));
        state2.horizontalWalls[1][4] = true;
        assertArrayEquals(new Position[]{new Position(1,5), new Position(1,3)} ,
                state2.handleFacing(current, concurrent, jump).toArray());
    }

    @Test
    public void testNeighbors() throws CloneNotSupportedException {
        assertEquals(131, state2.neighbors().size());
        state2.turn = 1;
        state2.round = 16;
        state2.playerPositions = new Position[]{new Position(4, 7), new Position(1, 4)};
        state2.wallsRemaining = new int[]{4, 1};
        state2.horizontalWalls = new boolean[][]{{true, false, true, false, true, false, false, false}, {false, false, false, true, false, false, false, false}, {false, false, false, false, false, false, false, false}, {false, false, false, false, false, false, false, false}, {false, false, false, false, false, false, false, false}, {false, false, false, true, false, true, false, false}, {false, false, false, true, false, false, false, false}, {false, false, false, true, false, true, false, false}};
        state2.verticalWalls = new boolean[][]{{false, true, false, true, false, true, false, false}, {false, false, false, false, true, false, false, false}, {false, false, false, false, false, false, false, false}, {false, false, false, false, false, false, false, false}, {false, false, false, false, false, false, false, false}, {false, false, false, false, true, false, false, false}, {false, false, true, false, false, false, false, false}, {false, false, false, false, false, false, false, false}};
        for (State neighbor : state2.neighbors()) {
            for (State neighbor2 : neighbor.neighbors()) {
                for (State neighbor3 : neighbor2.neighbors()) {
                    System.out.println(neighbor3);
                }
            }
        }
    }

    @Test
    public void testEquals() throws CloneNotSupportedException {
        State clone2 = state2.clone();
        assertEquals(state2, clone2);
        clone2.turn = 1;
        assertNotEquals(state2, clone2);
        clone2.turn = 0;
        clone2.playerPositions[0] = new Position(1,4);
        assertNotEquals(state2, clone2);
        clone2.playerPositions[0] = new Position(0,4);
        clone2.wallsRemaining[0] = 9;
        assertNotEquals(state2, clone2);
        clone2.wallsRemaining[0] = 10;
        clone2.horizontalWalls[0][0] = true;
        assertNotEquals(state2, clone2);
        clone2.horizontalWalls[0][0] = false;
        clone2.verticalWalls[0][0] = true;
        assertNotEquals(state2, clone2);
        clone2.verticalWalls[0][0] = false;
        clone2.round = 1;
        assertEquals(state2, clone2);
        clone2.playerGoals[0] = new int[]{1,4};
        assertEquals(state2, clone2);
    }

    @Test
    public void testHashCode() throws CloneNotSupportedException {
        State clone2 = state2.clone();
        assertEquals(state2.hashCode(), clone2.hashCode());
        clone2.turn = 1;
        assertNotEquals(state2.hashCode(), clone2.hashCode());
        clone2.turn = 0;
        clone2.playerPositions[0] = new Position(1,4);
        assertNotEquals(state2.hashCode(), clone2.hashCode());
        clone2.playerPositions[0] = new Position(0,4);
        clone2.wallsRemaining[0] = 9;
        assertNotEquals(state2.hashCode(), clone2.hashCode());
        clone2.wallsRemaining[0] = 10;
        clone2.horizontalWalls[0][0] = true;
        assertNotEquals(state2.hashCode(), clone2.hashCode());
        clone2.horizontalWalls[0][0] = false;
        clone2.verticalWalls[0][0] = true;
        assertNotEquals(state2.hashCode(), clone2.hashCode());
        clone2.verticalWalls[0][0] = false;
        clone2.round = 1;
        assertEquals(state2.hashCode(), clone2.hashCode());
        clone2.playerGoals[0] = new int[]{1,4};
        assertEquals(state2.hashCode(), clone2.hashCode());
    }
}

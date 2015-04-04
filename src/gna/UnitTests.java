package gna;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * A number of JUnit tests for Solver.
 * <p/>
 * Feel free to modify these to automatically test puzzles or other functionality
 */
public class UnitTests {

    Board board;

    @Before
    public void init() {
        int[][] tiles = new int[3][3];
        tiles[0][0] = 8;
        tiles[0][1] = 1;
        tiles[0][2] = 3;
        tiles[1][0] = 4;
        tiles[1][1] = 0;
        tiles[1][2] = 2;
        tiles[2][0] = 7;
        tiles[2][1] = 6;
        tiles[2][2] = 5;

        board = new Board(tiles);
    }

    @Test
    public void test() {

    }

    @Test
    public void hamming_test() {
        assertEquals(5, board.hamming());

    }

    @Test
    public void manhattan_test() {
        assertEquals(10, board.manhattan());
    }

    @Test
    public void equals_test() {
        assertTrue(board.equals(board));
    }

    @Test
    public void isSolavable_test() {
        int[][] tiles = new int[3][3];
        tiles[0][0] = 0;
        tiles[0][1] = 1;
        tiles[0][2] = 3;
        tiles[1][0] = 4;
        tiles[1][1] = 2;
        tiles[1][2] = 5;
        tiles[2][0] = 7;
        tiles[2][1] = 8;
        tiles[2][2] = 6;
        assertTrue(board.isSolvable());


        tiles[0][0] = 1;
        tiles[0][1] = 2;
        tiles[0][2] = 3;
        tiles[1][0] = 4;
        tiles[1][1] = 5;
        tiles[1][2] = 6;
        tiles[2][0] = 8;
        tiles[2][1] = 7;
        tiles[2][2] = 0;

        board = new Board(tiles);
        assertFalse(board.isSolvable());
    }


}

package gna;

import java.math.BigInteger;
import java.util.Collection;
import java.util.HashSet;

public class Board {

    protected static int[][] tiles;
    private Board previousBoard;
    private Pair zeroPositon;

    public Board(Board previousBoard) {
        this.previousBoard = previousBoard;
    }

    // construct a board from an N-by-N array of tiles
    public Board(int[][] tiles) {
        Board.tiles = tiles;
        zeroPositon = getCurrentPosition(0);
    }

    // return number of blocks out of place
    public int hamming() {
        System.out.println("1");
        int[][] tiles = getTiles();
        int wrongCounter = 0;
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles.length; j++) {
                if (tiles[i][j] != getCorrectNumber(i, j) && tiles[i][j] != 0) {
                    wrongCounter++;
                }
            }
        }

        return wrongCounter + calcSteps();
    }

    // return sum of Manhattan distances between blocks and goal
    public int manhattan() {
        System.out.println("2");
        int[][] tiles = getTiles();
        int wrongCounter = 0;
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles.length; j++) {
                if (tiles[i][j] != getCorrectNumber(i, j) && tiles[i][j] != 0) {
                    wrongCounter += Math.abs(i - getCorrectRow(tiles[i][j]));
                    wrongCounter += Math.abs(j - getCorrectColumn(tiles[i][j]));
                }
            }
        }
        return wrongCounter + calcSteps();
    }

    //calculate steps
    public int calcSteps() {
        int steps = 0;
        Board prev = this.getPreviousBoard();
        while (prev != null) {
            steps++;
            prev = prev.getPreviousBoard();
        }
        return steps;
    }

    // does this board position equal y
    @Override
    public boolean equals(Object y) {
        Board toCompare = (Board) y;
        int[][] toCompareTiles = toCompare.getTiles();
        int[][] tiles = getTiles();
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles.length; j++) {
                if (toCompareTiles[i][j] != tiles[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }

    // return a Collection of all neighboring board positions
    public Collection<Board> neighbors() {
        int[][] tiles = getTiles();
        Collection<Board> neighbors = new HashSet<Board>();
        Pair currPositionZero = getCurrentPosition(0);

        Board neighbor;
        //move tile down if possible
        if (currPositionZero.getRow() != 0) {
            neighbor = new ChangedBoard(this, Change.DOWN);
            if ((this.getPreviousBoard() != null && !neighbor.equals(this.getPreviousBoard())) || this.getPreviousBoard() == null) {
                neighbors.add(neighbor);
                neighbor.setZeroPositon(new Pair(this.getZeroPositon().getRow() - 1, this.getZeroPositon().getCol()));
            }
        }

        //move tile up if possible
        if (currPositionZero.getRow() != tiles.length - 1) {
            neighbor = new ChangedBoard(this, Change.UP);
            if ((this.getPreviousBoard() != null && !neighbor.equals(this.getPreviousBoard())) || this.getPreviousBoard() == null) {
                neighbors.add(neighbor);
                neighbor.setZeroPositon(new Pair(this.getZeroPositon().getRow() +1, this.getZeroPositon().getCol()));
            }
        }

        //move tile to the right if possible
        if (currPositionZero.getCol() != 0) {
            neighbor = new ChangedBoard(this, Change.RIGHT);
            if ((this.getPreviousBoard() != null && !neighbor.equals(this.getPreviousBoard())) || this.getPreviousBoard() == null) {
                neighbors.add(neighbor);
                neighbor.setZeroPositon(new Pair(this.getZeroPositon().getRow(), this.getZeroPositon().getCol() - 1));
            }
        }

        //move tile to the left if possible
        if (currPositionZero.getCol()!= tiles.length - 1) {
            neighbor = new ChangedBoard(this, Change.LEFT);
            if ((this.getPreviousBoard() != null && !neighbor.equals(this.getPreviousBoard())) || this.getPreviousBoard() == null) {
                neighbors.add(neighbor);
                neighbor.setZeroPositon(new Pair(this.getZeroPositon().getRow(), this.getZeroPositon().getCol() + 1));
            }
        }
        return neighbors;
    }

    // return a string representation of the board
    public String toString() {
        int[][] tiles = getTiles();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles.length; j++) {
                builder.append(tiles[i][j]).append(" ");
            }
            builder.append("\n");
        }
        return builder.toString();
    }

    // is the initial board solvable?
    public boolean isSolvable() {
        //save the original board before calculating
        int[][] originalBoard = copyTiles(Board.tiles);
        Pair currPositionZero = getCurrentPosition(0);

        //move to the right
        while (currPositionZero.getCol() != tiles.length - 1) {
            tiles = switcher(new Pair(currPositionZero.getRow(), currPositionZero.getCol()), new Pair(currPositionZero.getRow(), currPositionZero.getCol() + 1));
            currPositionZero = getCurrentPosition(0);
        }

        //move down
        while (currPositionZero.getRow() != tiles.length - 1) {
            tiles = switcher(new Pair(currPositionZero.getRow(), currPositionZero.getCol()), new Pair(currPositionZero.getRow() + 1, currPositionZero.getCol()));
            currPositionZero = getCurrentPosition(0);
        }

        //now the board is ready

        //place the board in an array
        int[] array = new int[tiles.length * tiles.length];
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles.length; j++) {
                int counter = (i * tiles.length) + j;
                array[counter] = tiles[i][j];
            }
        }

        //bereken teller
        BigInteger teller = BigInteger.ONE;
        for (int j = 1; j < tiles.length * tiles.length; j++) {
            int i = 1;
            while (i < j) {
                teller = teller.multiply(new BigInteger(position(array, j) - position(array, i) + ""));
                i++;
            }
        }

        BigInteger noemer = BigInteger.ONE;
        for (int j = 1; j < tiles.length * tiles.length; j++) {
            int i = 1;
            while (i < j) {
                noemer = noemer.multiply(new BigInteger(j - i + ""));
                i++;
            }
        }

        BigInteger sgn = teller.divide(noemer);

        //set the original board back
        tiles = originalBoard;

        if (sgn.compareTo(BigInteger.ZERO) >= 0) {
            return true;
        }

        return false;
    }

    // returns the position in the array where the number is
    private int position(int[] array, int number) {
        for (int i : array) {
            if (array[i] == number) {
                return i + 1;
            }
        }
        return 0;
    }

    //returns the correct number on a row and column
    int getCorrectNumber(int row, int col) {
        int[][] tiles = getTiles();
        if (row == tiles.length - 1 && col == tiles.length - 1) {
            return 0;
        }
        return row * tiles.length + col + 1;
    }

    // returns the row a number should be on
    private int getCorrectRow(int number) {
        return number / tiles.length;
    }

    // return the column a number should be on
    private int getCorrectColumn(int number) {
        return (number % tiles.length) - 1;
    }

    // returns a coordinate pair of the current position of a number
    protected Pair getCurrentPosition(int number) {
        int[][] tiles = getTiles();
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles.length; j++) {
                if (tiles[i][j] == number) {
                    return new Pair(i, j);
                }
            }
        }
        return null;
    }


    protected int[][] getTiles() {
        return tiles;
    }

    // switches two positions and returns a new tile[][]
    protected int[][] switcher(Pair pair1, Pair pair2) {
        int[][] newTiles = copyTiles(Board.tiles);
        newTiles[pair1.getRow()][pair1.getCol()] = tiles[pair2.getRow()][pair2.getCol()];
        newTiles[pair2.getRow()][pair2.getCol()] = tiles[pair1.getRow()][pair1.getCol()];
        return newTiles;
    }

    // copies to entire new double array
    protected int[][] copyTiles(int[][] tiles) {
        int[][] newTiles = new int[tiles.length][tiles.length];
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles.length; j++) {
                newTiles[i][j] = tiles[i][j];
            }
        }
        return newTiles;
    }

    protected boolean isSolved() {
        int[][] tiles = getTiles();
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles.length; j++) {
                if (getCorrectNumber(i, j) != tiles[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }

    public Board getPreviousBoard() {
        return previousBoard;
    }

    public void setPreviousBoard(Board previousBoard) {
        this.previousBoard = previousBoard;
    }

    public Pair getZeroPositon() {
        return zeroPositon;
    }

    public void setZeroPositon(Pair zeroPositon) {
        this.zeroPositon = zeroPositon;
    }
}


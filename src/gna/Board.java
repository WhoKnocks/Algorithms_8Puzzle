package gna;

import java.math.BigInteger;
import java.util.Collection;
import java.util.HashSet;

public class Board {

    private int[][] tiles;

    private Board previousBoard;

    // construct a board from an N-by-N array of tiles
    public Board(int[][] tiles) {
        this.tiles = tiles;
    }

    // return number of blocks out of place
    public int hamming() {
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

    //calculate the amount of previous boards (cost)
    public int calcSteps() {
        int steps = 0;
        Board prev = this.getPreviousBoard();
        while (prev != null) {
            steps++;
            prev = prev.getPreviousBoard();
        }
        return steps;
    }

    // does this board positionInArray equal y
    @Override
    public boolean equals(Object y) {
        Board toCompare = (Board) y;
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles.length; j++) {
                if (toCompare.getTiles()[i][j] != tiles[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }

    // return a Collection of all neighboring board positions
    public Collection<Board> neighbors() {
        Collection<Board> neighbors = new HashSet<Board>();
        Pair currPositionZero = getCurrentPosition(0);

        Board neighbor;

        //move tile up if possible
        if (currPositionZero.getRow() != tiles.length - 1) {
            neighbor = new Board(switcher(currPositionZero, new Pair(currPositionZero.getRow() + 1, currPositionZero.getCol())));
            if ((this.getPreviousBoard() != null && !isSameAsParent(neighbor)) || this.getPreviousBoard() == null) {
                neighbor.setPreviousBoard(this);
                neighbors.add(neighbor);
            }
        }

        //move tile down if possible
        if (currPositionZero.getRow() != 0) {
            neighbor = new Board(switcher(currPositionZero, new Pair(currPositionZero.getRow() - 1, currPositionZero.getCol())));
            if ((this.getPreviousBoard() != null && !isSameAsParent(neighbor)) || this.getPreviousBoard() == null) {
                neighbor.setPreviousBoard(this);
                neighbors.add(neighbor);
            }
        }

        //move tile to the left if possible
        if (currPositionZero.getCol() != tiles.length - 1) {
            neighbor = new Board(switcher(currPositionZero, new Pair(currPositionZero.getRow(), currPositionZero.getCol() + 1)));
            if ((this.getPreviousBoard() != null && !isSameAsParent(neighbor)) || this.getPreviousBoard() == null) {
                neighbor.setPreviousBoard(this);
                neighbors.add(neighbor);
            }
        }

        //move tile to the right if possible
        if (currPositionZero.getCol() != 0) {
            neighbor = new Board(switcher(currPositionZero, new Pair(currPositionZero.getRow(), currPositionZero.getCol() - 1)));
            if ((this.getPreviousBoard() != null && !isSameAsParent(neighbor)) || this.getPreviousBoard() == null) {
                neighbor.setPreviousBoard(this);
                neighbors.add(neighbor);
            }
        }


        return neighbors;
    }

    public boolean isSameAsParent(Board board) {
        Board prev = this.getPreviousBoard();
        while (prev != null) {
            if (prev.equals(board)) {
                return true;
            }
            prev = prev.getPreviousBoard();
        }
        return false;
    }

    // return a string representation of the board
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles.length; j++) {
                builder.append(String.format("%-2s", tiles[i][j])).append(" ");
            }
            builder.append("\n");
        }
        builder.append("Heuristic = " + this.manhattan());
        return builder.toString();
    }

    // is the initial board solvable?
    public boolean isSolvable() {
        //save the original board before calculating
        int[][] originalBoard = copyTiles();
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
        int[] flatArray = new int[tiles.length * tiles.length];
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles.length; j++) {
                int counter = (i * tiles.length) + j;
                flatArray[counter] = tiles[i][j];
            }
        }

        //Array with indexes
        int[] indexArray = new int[tiles.length * tiles.length];
        for (int i = 0; i < tiles.length * tiles.length; i++) {
            indexArray[i] = positionInArray(flatArray, i);
        }


        //compute numerator
        BigInteger numerator = BigInteger.ONE;
        for (int j = 1; j < tiles.length * tiles.length; j++) {
            for (int i = 1; i < j; i++) {
                numerator = numerator.multiply(new BigInteger((indexArray[j] - indexArray[i]) + ""));
            }
        }

        //compute denominator
        BigInteger denominator = BigInteger.ONE;
        for (int j = 1; j < tiles.length * tiles.length; j++) {
            for (int i = 1; i < j; i++) {
                denominator = denominator.multiply(new BigInteger(j - i + ""));
            }

        }


        BigInteger sgn = numerator.divide(denominator);

        //set the original board back
        tiles = originalBoard;

        if (sgn.compareTo(BigInteger.ZERO) >= 0) {
            return true;
        }

        return false;
    }

    // returns the positionInArray in the array where the number is
    private int positionInArray(int[] array, int number) {
        for (int i : array) {
            if (array[i] == number) {
                return i + 1;
            }
        }
        return 0;
    }

    //returns the correct number on a row and column
    private int getCorrectNumber(int row, int col) {
        if (row == tiles.length - 1 && col == tiles.length - 1) {
            return 0;
        }
        return (row * tiles.length) + col + 1;
    }

    // returns the row a number should be on
    private int getCorrectRow(int number) {
        return (number - 1) / tiles.length;
    }

    // return the column a number should be on
    private int getCorrectColumn(int number) {
        return ((number - 1) % tiles.length);
    }

    // returns a coordinate pair of the current positionInArray of a number
    private Pair getCurrentPosition(int number) {
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles.length; j++) {
                if (tiles[i][j] == number) {
                    return new Pair(i, j);
                }
            }
        }
        return null;
    }


    private int[][] getTiles() {
        return tiles;
    }

    // switches two positions and returns a new tile[][]
    private int[][] switcher(Pair zeroPos, Pair pair2) {
        int[][] newTiles = copyTiles();
        newTiles[zeroPos.getRow()][zeroPos.getCol()] = tiles[pair2.getRow()][pair2.getCol()];
        newTiles[pair2.getRow()][pair2.getCol()] = 0;
        return newTiles;
    }

    // copies to entire new double array
    private int[][] copyTiles() {
        int[][] newTiles = new int[tiles.length][tiles.length];
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles.length; j++) {
                newTiles[i][j] = tiles[i][j];
            }
        }
        return newTiles;
    }

    public boolean isSolved() {
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
}


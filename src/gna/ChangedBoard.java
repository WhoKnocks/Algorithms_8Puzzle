package gna;

import java.util.Stack;

/**
 * Created by GJ on 4/04/2015.
 */
public class ChangedBoard extends Board {

    private Change change;

    public ChangedBoard(Board previousBoard, Change change) {
        super(previousBoard);
        this.change = change;
    }

    public int[][] makeBoard() {
        System.out.println("make board");
        int[][] newBoard = super.copyTiles(Board.tiles);
        Stack<Change> changes = getChanges();
        while (!changes.isEmpty()) {
            Change change = changes.pop();
            Pair zeroPosition = getPreviousBoard().getZeroPositon();
            if (change == Change.DOWN) {
                newBoard[zeroPosition.getRow()][zeroPosition.getCol()] = newBoard[zeroPosition.getRow() - 1][zeroPosition.getCol()];
                newBoard[zeroPosition.getRow() - 1][zeroPosition.getCol()] = 0;
            }
            if (change == Change.UP) {
                newBoard[zeroPosition.getRow()][zeroPosition.getCol()] = newBoard[zeroPosition.getRow() + 1][zeroPosition.getCol()];
                newBoard[zeroPosition.getRow() + 1][zeroPosition.getCol()] = 0;
            }
            if (change == Change.LEFT) {
                newBoard[zeroPosition.getRow()][zeroPosition.getCol()] = newBoard[zeroPosition.getRow()][zeroPosition.getCol() + 1];
                newBoard[zeroPosition.getRow()][zeroPosition.getCol() + 1] = 0;
            }
            if (change == Change.RIGHT) {
                newBoard[zeroPosition.getRow()][zeroPosition.getCol()] = newBoard[zeroPosition.getRow()][zeroPosition.getCol() - 1];
                newBoard[zeroPosition.getRow()][zeroPosition.getCol() - 1] = 0;
            }
        }
        return newBoard;
    }

    @Override
    protected boolean isSolved() {
        return super.isSolved();
    }

    private Stack<Change> getChanges() {
        System.out.println("getchanges");
        Board prevBoard = getPreviousBoard();
        Stack<Change> changes = new Stack<Change>();
        changes.push(change);
        while (prevBoard != null) {
            prevBoard = prevBoard.getPreviousBoard();
            if (prevBoard instanceof ChangedBoard) {
                changes.push(((ChangedBoard) prevBoard).change);
            }
        }
        return changes;
    }

    @Override
    protected int[][] getTiles() {
        return makeBoard();
    }


}

package gna;


import libpract.PriorityFunc;

import java.util.*;


public class Solver {

    PriorityQueue<Board> boardPQ;
    Board endBoard;

    /**
     * Finds a solution to the initial board.
     *
     * @param priority is either PriorityFunc.HAMMING or PriorityFunc.MANHATTAN
     */
    public Solver(Board initial, PriorityFunc priority) {
        // Use the given priority function (either PriorityFunc.HAMMING
        // or PriorityFunc.MANHATTAN) to solve the puzzle.
        if (priority == PriorityFunc.HAMMING) {
            boardPQ = new PriorityQueue<Board>(10, new Comparator<Board>() {
                @Override
                public int compare(Board b1, Board b2) {
                    return b1.hamming() - b2.hamming();
                }
            });
        } else if (priority == PriorityFunc.MANHATTAN) {
            boardPQ = new PriorityQueue<Board>(10, new Comparator<Board>() {
                @Override
                public int compare(Board b1, Board b2) {
                    return b1.manhattan() - b2.manhattan();
                }
            });
        } else {
            throw new IllegalArgumentException("Priority function not supported");
        }
        boardPQ.add(initial);
        solve();
    }

    private void solve() {
        long begin = System.currentTimeMillis();

        //get minimum heuristic value board
        Board lowHeurBoard = boardPQ.poll();

        while (!lowHeurBoard.isSolved()) {
            for (Board neighbor : lowHeurBoard.neighbors()) {
                boardPQ.offer(neighbor);
            }
            lowHeurBoard = boardPQ.poll();
        }
        endBoard = lowHeurBoard;

        System.out.println("tijd in miliseconden: " + (System.currentTimeMillis() - begin));
    }

    /**
     * Returns a Collection of board positions as the solution. It should contain the initial
     * Board as well as the solution (if these are equal only one Board is returned).
     */
    public Collection<Board> solution() {
        List<Board> solution = new ArrayList<Board>();
        solution.add(endBoard);
        Board tempBoard = endBoard;
        while (tempBoard.getPreviousBoard() != null) {
            solution.add(tempBoard.getPreviousBoard());
            tempBoard = tempBoard.getPreviousBoard();
        }
        Collections.reverse(solution);
        System.out.println(boardPQ.size());
        return solution;
    }
}



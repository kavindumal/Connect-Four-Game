package lk.ijse.dep.service;

import static lk.ijse.dep.service.Board.NUM_OF_COLS;
import static lk.ijse.dep.service.Board.NUM_OF_ROWS;

public class BoardImpl implements Board {
    private Piece[][] pieces;

    private BoardUI boardUI;

    public BoardImpl(BoardUI boardUI) {
        pieces = new Piece[NUM_OF_COLS][NUM_OF_ROWS];

        for (int i = 0; i < pieces.length; i++) {
            for (int j = 0; j < pieces[i].length; j++) {
                pieces[i][j] = Piece.EMPTY;
            }
        }
    }

    @Override
    public BoardUI getBoardUI() {
        return boardUI;
    }

    @Override
    public int findNextAvailableSpot(int col) {
        int returnValue = -1;
        for (int i = 0; i < pieces[i].length; i++) {
            if (pieces[col][i].equals(Piece.EMPTY)){
                returnValue = i;
            }
        }
        return returnValue;
    }

    @Override
    public boolean isLegelMove(int col) {
        boolean b = true;
        int returnValue = findNextAvailableSpot(col);
        if (returnValue == -1) {
            b = false;
        }
        return b;
    }

    @Override
    public boolean existLegelMoves() {
        return false;
    }

    @Override
    public void updateMove(int col, Piece move) {

    }

    @Override
    public Winner findWinner() {
        return null;
    }
}

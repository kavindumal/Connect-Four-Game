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
        return 0;
    }

    @Override
    public boolean isLegelMove(int col) {
        return false;
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

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
        return this.boardUI;
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
        boolean b = false;
        for (int i = 0; i < pieces.length ; i++) {
            for (int j = 0; j < pieces[i].length; j++) {
                if (pieces[i][j].equals(Piece.EMPTY)){
                    b = true;
                }
            }
        }
        return b;
    }

    @Override
    public void updateMove(int col, Piece move) {
        for (int i = 0; i < pieces[i].length; i++) {
            if (pieces[col][i].equals(Piece.EMPTY)){
                pieces[col][i] = move;
            }
        }
    }

    @Override
    public Winner findWinner() {
        Piece winningPiece = Piece.EMPTY;
        int col1 = -1;
        int row1 = -1;
        int col2 = -1;
        int row2 = -1;

        for (int i = 0; i < pieces.length; i++) {
            for (int j = 0; j < pieces[i].length - 3; j++) {
                Piece currentPiece = pieces[i][j];
                if (currentPiece.equals(pieces[i][j + 1]) && currentPiece.equals(pieces[i][j + 2]) && currentPiece.equals(pieces[i][j + 3])) {
                    winningPiece = currentPiece;
                    col1 = i;
                    row1 = j;
                    col2 = i;
                    row2 = j + 3;
                }
            }
        }


        for (int i = 0; i < pieces[i].length; i++) {
            for (int j = 0; j < pieces.length; j++) {
                Piece currentPiece = pieces[j][i];
                if (currentPiece.equals(pieces[j + 1][i]) && currentPiece.equals(pieces[j + 2][i]) && currentPiece.equals(pieces[j + 3][i])) {
                    winningPiece = currentPiece;
                    col1 = j;
                    row1 = i;
                    col2 = j + 3;
                    row2 = i;
                }
            }
        }

        return new Winner(winningPiece, col1, row1, col2, row2);
    }
}
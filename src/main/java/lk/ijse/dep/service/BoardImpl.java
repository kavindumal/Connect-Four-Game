package lk.ijse.dep.service;

public class BoardImpl implements Board {
    private final Piece[][] pieces;

    private final BoardUI boardUI;

    Piece winningPiece = Piece.EMPTY;
    int col1,col2,row1,row2;
    public BoardImpl(BoardUI boardUI) {
        pieces = new Piece[NUM_OF_COLS][NUM_OF_ROWS];
        this.boardUI = boardUI;

        for (int i = 0; i < NUM_OF_COLS; i++) {
            for (int j = 0 ; j < NUM_OF_ROWS; j++) {
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
        for (int i = 0; i < NUM_OF_ROWS ; i++) {
            if (pieces[col][i] == Piece.EMPTY){
                returnValue = i;
                break;
            }
        }
        return returnValue;
    }

    @Override
    public boolean isLegelMove(int col) {
        int returnValue = findNextAvailableSpot(col);
        return returnValue != (-1);
    }

    @Override
    public boolean existLegelMoves() {
        boolean b = false;
        for (int i = 0; i < NUM_OF_COLS ; i++) {
            for (int j = 0; j < NUM_OF_ROWS; j++) {
                if (pieces[i][j] == Piece.EMPTY){
                    b = true;
                    break;
                }
            }
        }
        return b;
    }

    @Override
    public void updateMove(int col, Piece move) {
        for (int i = 0; i < NUM_OF_ROWS; i++) {
            if (pieces[col][i] == Piece.EMPTY){
                pieces[col][i] = move;
                break;
            }
        }
    }

    @Override
    public void updateMove(int col, int row, Piece move){
        pieces[col][row] = move;
    }

    @Override
    public Winner findWinner() {
        Piece winningPiece = Piece.EMPTY;

        for (int i = 0; i < NUM_OF_COLS; i++) {
            for (int j = 0; j < NUM_OF_ROWS - 3; j++) {
                if (pieces[i][j] == pieces[i][j + 1] && pieces[i][j + 1] == pieces[i][j + 2] && pieces[i][j + 2] == pieces[i][j + 3]) {
                    if (pieces[i][j] != Piece.EMPTY) {
                        winningPiece = pieces[i][j];
                        col1 = i;
                        row1 = j;
                        col2 = i;
                        row2 = j + 3;
                        return new Winner(winningPiece, col1, row1, col2, row2);
                    }
                }
            }
        }


        for (int i = 0; i < NUM_OF_ROWS; i++) {
            for (int j = 0; j < NUM_OF_COLS - 3; j++) {
                if (pieces[j][i] == pieces[j + 1][i] && pieces[j + 1][i] == pieces[j + 2][i] && pieces[j + 2][i] == pieces[j + 3][i]) {
                    if (pieces[j][i] != Piece.EMPTY) {
                        winningPiece = pieces[j][i];
                        col1 = j;
                        row1 = i;
                        col2 = j + 3;
                        row2 = i;
                        return new Winner(winningPiece, col1, row1, col2, row2);
                    }
                }
            }
        }
        return new Winner(Piece.EMPTY);
    }
}
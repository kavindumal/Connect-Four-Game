package lk.ijse.dep.service;

public class HumanPlayer extends Player{
    boolean legelMove;
    public HumanPlayer(Board board) {
        super(board);
    }

    @Override
    public void movePiece(int col){
        legelMove = board.isLegelMove(col);
        if (legelMove){
            board.updateMove(col,Piece.BLUE);
            board.getBoardUI().update(col,legelMove);
            Winner winner = board.findWinner();
            if (winner.getWinningPiece() != Piece.EMPTY){
                board.getBoardUI().notifyWinner(winner);
            }else {
                if (!board.existLegelMoves()){
                    board.getBoardUI().notifyWinner(new Winner(Piece.EMPTY));
                }
            }
        }
    }
}

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
            BoardUI boardUI = board.getBoardUI();
            boardUI.update(col,true);
            board = new BoardImpl(boardUI);
            Winner winner = board.findWinner();
            if (winner.getCol1() == -1){
                boolean b = board.existLegelMoves();
                if (!b){
                    boardUI.notifyWinner(new Winner(Piece.EMPTY));
                } else return;
            } else {
                boardUI.notifyWinner(winner);
            }
        }
    }
}

package lk.ijse.dep.service;

import java.util.Map;
import java.util.Random;

public class AiPlayer extends Player{
    Random random = new Random();
    public AiPlayer(Board board) {
        super(board);
    }

    @Override
    public void movePiece(int col){
        do {
            col = (int) Math.floor(Math.random() * 6);
            if (board.isLegelMove(col)){
                break;
            } else continue;
        }while (true);
        board.updateMove(col, Piece.GREEN);
        BoardUI boardUI = board.getBoardUI();
        boardUI.update(col,false);
        board = new BoardImpl(boardUI);
        Winner winner = board.findWinner();
        if (winner.getWinningPiece()!=Piece.EMPTY){
            board.getBoardUI().notifyWinner(winner);
        }else {
            if (!board.existLegelMoves()){
                board.getBoardUI().notifyWinner(new Winner(Piece.EMPTY));
            }
        }
    }
}

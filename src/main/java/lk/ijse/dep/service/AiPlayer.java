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

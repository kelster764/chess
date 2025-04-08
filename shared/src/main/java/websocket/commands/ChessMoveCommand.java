package websocket.commands;

import chess.ChessMove;

public class ChessMoveCommand extends UserGameCommand{
    public chess.ChessMove move;
    public ChessMoveCommand(String authToken, Integer gameID, ChessMove move){
        super(CommandType.MAKE_MOVE, authToken, gameID);
        this.move = move;
    }

    public chess.ChessMove getMove() {
        return move;
    }
}

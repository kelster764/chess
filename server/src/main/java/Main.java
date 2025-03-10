import chess.*;
import server.Server;

//import dataaccess.MemoryDataAccess;


public class Main {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Server: " + piece);
        var port = 8080;
        Server server = new Server();
        server.run(port);
    }
}
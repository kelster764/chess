import client.Repl;
import exception.DataAccessException;

import javax.websocket.DeploymentException;
import java.io.IOException;
import java.net.URISyntaxException;


public class ClientMain {
    public static void main(String[] args) throws DeploymentException, URISyntaxException, IOException, DataAccessException {
//        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
//        System.out.println("♕ 240 Chess Client: " + piece);
        var serverUrl = "http://localhost:8080";
        new Repl(serverUrl).run();
    }
}
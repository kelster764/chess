package client;

import chess.ChessGame;
import com.google.gson.Gson;
import exception.DataAccessException;
import model.GameData;
import ui.ChessPrint;
import ui.ServerMessageHandler;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGame;
import websocket.messages.Notification;
import websocket.messages.ServerMessage;

import javax.websocket.DeploymentException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Scanner;

public class Repl implements ServerMessageHandler {
    private  final UserClient client;


    public Repl(String serverUrl) throws DeploymentException, URISyntaxException, IOException, DataAccessException {
        client = new UserClient(serverUrl, this);
    }
    public void run(){
        System.out.println("Welcome to Chess, ya nerd. Sign in already");
        System.out.print(client.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while(!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try{
                result = client.eval(line);
                System.out.print(result);
            }catch(Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

    public void notify(ServerMessage serverMessage) {
        System.out.println(serverMessage);
        printPrompt();
    }

    private void printPrompt() {
        System.out.print("\n" + '[' + client.state+ ']' + " >>> ");
    }

    @Override
    public void notify(Notification notification) {
        String realMessage = notification.getMessage();
        System.out.println(realMessage);
        printPrompt();
    }

    @Override
    public void load(LoadGame loadGame) {
        String color = client.color;
        Gson gson = new Gson();
        GameData game = loadGame.getGame();
        ChessGame chessGame = game.game();
        String json = gson.toJson(chessGame);
        ChessPrint chessPrint = new ChessPrint();
        System.out.println(String.format("game %d", game.gameID()));
        chessPrint.main(new String[]{color, json});
        printPrompt();

    }

    @Override
    public void error(Error error) {

    }

    @Override
    public void errorMessage(ErrorMessage error) {
        String realMessage = error.getMessage();
        System.out.println(realMessage);
        printPrompt();
    }
}

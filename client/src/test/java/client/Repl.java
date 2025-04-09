package client;

import exception.DataAccessException;
import ui.ServerMessageHandler;
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
    public void notify(Notification serverMessage) {
        String realMessage = serverMessage.getMessage();
        System.out.println(realMessage);
        printPrompt();
    }

    @Override
    public void load(LoadGame loadGame) {

    }

    @Override
    public void error(Error error) {

    }
}

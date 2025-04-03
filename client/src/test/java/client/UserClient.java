package client;
import com.google.gson.Gson;
import exception.DataAccessException;
import model.AuthData;
import model.GameData;
import server.Server;
import server.ServerFacade;
import server.websocket.WebSocketHandler;
import ui.ChessPrint;
import server.websocket.WebSocketHandler;

import java.util.Arrays;
import java.util.Objects;

public class UserClient {
    private String visitorName = null;
    private String authToken = null;
    private static String serverUrl;
    private static Server server;
    private static ServerFacade sv;
    private static WebSocketHandler ws;
    public static State state = State.LOGGEDOUT;


    public UserClient(String serverUrl){
        sv = new ServerFacade(serverUrl);
        ws = new WebSocketHandler();
        this.serverUrl = serverUrl;
    }

    public String eval(String input){
        try{
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            if(state == State.LOGGEDOUT) {
                return switch (cmd) {
                    case "register" -> register(params);
                    case "login" -> login(params);
                    case "quit" -> quit();
                    default -> help();
                };
            }
            else if(state == State.LOGGEDIN){
                return switch (cmd) {
                    case "logout" -> logout();
                    case "create" -> create(params);
                    case "list" -> list();
                    case "observe" -> observe(params);
                    case "join" -> join(params);
                    case "quit" -> quit();
                    default -> help();
                };
            }
            else if(state == State.GAMEMODE){
                return switch (cmd) {
                    case "connect" -> connect();
                    case "makeMove" -> makeMove(params);
                    case "leave" -> leave();
                    case "resign" -> resign();
                    case "quit" -> quit();
                    default -> help();
                };
            }

        }catch(Exception ex) {
            return ex.getMessage();
        }
    }

    private String quit() {
        System.exit(0);
        return "";
    }

    private String observe(String... params) throws DataAccessException {

        ChessPrint chessBoard = new ChessPrint();
        chessBoard.main(new String[]{"white"});
        return "Board displayed";
    }

    public String register(String... params) throws DataAccessException{
        if (params.length == 3){
            try {
                visitorName = params[0];
                AuthData authData = sv.register(visitorName, params[1], params[2]);
                authToken = authData.authToken();
                state = State.LOGGEDIN;
                return String.format("Welcome %s", visitorName);
            }catch(Exception ex){
                if (ex.getMessage() == "not null"){
                    throw new DataAccessException("user already exists");
                }
            }
        }
        throw new DataAccessException("Expected username, password, and email");
    }

    public String login(String... params) throws DataAccessException {
        if (params.length == 2) {
            try {
                String visitorName = params[0];
                AuthData authData = sv.login(visitorName, params[1]);
                authToken = authData.authToken();
                state = State.LOGGEDIN;
                return String.format("Welcome %s", visitorName);
            } catch (Exception ex) {
                if (ex.getMessage() == "not null") {
                    throw new DataAccessException("user not found");
                }
            }
        }
        throw new DataAccessException("Expected username and password");
    }

    public String logout() throws DataAccessException{
        assertSignedIn();
        sv.logout(authToken);
        state = State.LOGGEDOUT;
        //String visitor = visitorName;
        //visitorName = null;
        return "Goodbye!";
    }

    public String create(String... params) throws DataAccessException{
        assertSignedIn();
        if (params.length == 1) {
            try {
                String gameName = params[0];
                GameData gameData = sv.createGame(gameName, authToken);
                int gameID = gameData.gameID();
                return String.format("Your gameID is %d", gameID);
            }catch (Exception ex) {
                return ex.getMessage();
                }
        }
        throw new DataAccessException("Expected gameName");
    }

    public String join(String... params) throws DataAccessException{
        assertSignedIn();
        if (params.length == 2) {
            try {
                String gameColor = params[1].toUpperCase();

                int gameID = Integer.parseInt(params[0]);
                sv.joinGame(gameID, gameColor, authToken);
                ChessPrint chessBoard = new ChessPrint();
                chessBoard.main(new String[]{gameColor});
                return "play!";
            }catch(Exception ex){
                if(Objects.equals(ex.getMessage(), "not null")){
                    throw new DataAccessException("spot taken");
                }
            }
        }
        throw new DataAccessException("Expected number and color");
    }



    public String list() throws DataAccessException{
        assertSignedIn();
        var games = sv.listGames(authToken);
        var result = new StringBuilder();
        var gson = new Gson();
        for (var game : games) {
            result.append(gson.toJson(game)).append('\n');
        }
        return result.toString();
    }

    //put list here g

    public String help() {
        if (state == State.LOGGEDOUT) {
            return """
                    register <USERNAME> <PASSWORD> <EMAIL> - to create an account
                    login <USERNAME> <PASSWORD> - to play chess
                    quit - playing chess
                    help - with possible commands
                    """;
        }
        return """
                create <NAME> - a game
                list - games
                join <ID> [WHITE | BLACK] - a game
                observe <ID> - a game
                logout - when you are done
                quit - playing chess
                help - with possible commands
                """;
    }


    private void assertSignedIn() throws DataAccessException {
        if (state == State.LOGGEDOUT) {
            throw new DataAccessException("You must sign in");
        }
    }
}

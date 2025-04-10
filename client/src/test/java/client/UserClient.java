package client;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import exception.DataAccessException;
import model.AuthData;
import model.GameData;
import server.Server;
import server.ServerFacade;
import ui.ChessPrint;
import ui.WebSocketFacade;
import websocket.commands.ChessMoveCommand;
import websocket.commands.UserGameCommand;
//import org.eclipse.jetty.websocket.api.Session;

import javax.websocket.DeploymentException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Objects;

public class UserClient {
    private String visitorName = null;
    private String authToken = null;
    private static String serverUrl;
    private static Server server;
    private static ServerFacade sv;

    //private static WebSocketHandler ws;
    private WebSocketFacade ws;
    public static State state = State.LOGGEDOUT;
    public String color;
    public int gameID;
    public static GameDAO gameDAO;
    public static AuthDAO authDAO;


    public UserClient(String serverUrl, Repl repl) throws DeploymentException, URISyntaxException, IOException, DataAccessException {
        sv = new ServerFacade(serverUrl);
        //ws = new WebSocketHandler(authDAO, gameDAO);

        ws = new WebSocketFacade(serverUrl, repl);
        this.serverUrl = serverUrl;
        this.color = "white";
        this.gameID = gameID;
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
//(state == State.LOGGEDIN)
            else if (state == State.LOGGEDIN){
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
            else{
                return switch (cmd) {
                    case "redraw" -> redrawBoard();
                    case "leave" -> leave();
                    case "move" -> makeMove(params);
                    case "resign" -> resign();
                    //case "highlight" -> highlight();
                    default -> help();
                };
            }

        }catch(Exception ex) {
            return ex.getMessage();
        }
    }

    private String redrawBoard() throws DataAccessException {
        Gson gson = new Gson();
        GameData gameData = sv.getGame(gameID, authToken);
        ChessGame chessGame = gameData.game();
        String json = gson.toJson(chessGame);
        ChessPrint chessPrint = new ChessPrint();
        //System.out.println(String.format("game %d", game.gameID()));
        chessPrint.main(new String[]{color, json});
        return "board redrawn";
    }


    private String quit() {
        System.exit(0);
        return "";
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

    private String observe(String... params) throws DataAccessException, IOException {
        color = "white";
        Gson gson = new Gson();
        gameID = Integer.parseInt(params[0]);
        //GameData gameData = sv.getGame(gameID, authToken);
        state = State.GAMEMODE;
        UserGameCommand userGameCommand = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID);
        String userJson = gson.toJson(userGameCommand);
        ws.send(userJson);
        return "Board displayed";
    }


    public String join(String... params) throws DataAccessException{
        assertSignedIn();
        if (params.length == 2) {
            try {
                Gson gson = new Gson();
                color =  params[1].toUpperCase();
                gameID = Integer.parseInt(params[0]);

                sv.joinGame(gameID, color, authToken);
                UserGameCommand userGameCommand = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID);
                String userJson = gson.toJson(userGameCommand);
                ws.send(userJson);
                state = State.GAMEMODE;

                return "play!";
            }catch(Exception ex){
                if(Objects.equals(ex.getMessage(), "not null")){
                    throw new DataAccessException("spot taken");
                }
            }
        }
        throw new DataAccessException("Expected number and color");
    }

    public String leave() throws DataAccessException, IOException {
        Gson gson = new Gson();
        UserGameCommand userGameCommand = new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID);
        String userJson = gson.toJson(userGameCommand);
        ws.send(userJson);
        state = State.LOGGEDIN;
        return "you have left game";
        //leave gets unhappy if I try to leave a game where I am both users
    }

    public String resign() throws DataAccessException, IOException {
        Gson gson = new Gson();
        UserGameCommand userGameCommand = new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, gameID);
        String userJson = gson.toJson(userGameCommand);
        ws.send(userJson);
        state = State.LOGGEDIN;
        return "you have resigned";
    }

    private String makeMove(String[] params) throws IOException {
        String start = params[0];
        String end = params[1];
        String promotion = null;
        if(params.length > 2) {
            promotion = params[2];
        }
        ChessPiece chessPiece = null;

        if(promotion != null) {
            if (promotion.equalsIgnoreCase("queen")) {
                chessPiece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN);
            } else if (promotion.equalsIgnoreCase("bishop")) {
                chessPiece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);
            } else if (promotion.equalsIgnoreCase("rook")) {
                chessPiece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK);
            } else if (promotion.equalsIgnoreCase("knight")) {
                chessPiece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);
            }
        }


        int startCol = start.charAt(0) - 'a' + 1;
        int startRow = Character.getNumericValue(start.charAt(1));

        int endCol = end.charAt(0) - 'a' + 1;
        int endRow = Character.getNumericValue(end.charAt(1));

        ChessPosition startPosition = new ChessPosition(startRow, startCol);
        ChessPosition endPosition = new ChessPosition(endRow, endCol);

        ChessMove chessMove = new ChessMove(startPosition, endPosition, null);
        if(chessPiece != null){
            chessMove = new ChessMove(startPosition, endPosition, chessPiece.getPieceType());
        }
        Gson gson = new Gson();
        ChessMoveCommand chessMoveCommand = new ChessMoveCommand(authToken, gameID, chessMove);
        String userJson = gson.toJson(chessMoveCommand);
        ws.send(userJson);
        return "";

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
        if (state == State.GAMEMODE) {
            return """
                    redraw - to redraw board
                    leave - to leave game
                    move <startSpot> <endSpot> - makeMove
                    resign - resign
                    highlight - highlight
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

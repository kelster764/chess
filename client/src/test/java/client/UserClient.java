package client;
import com.google.gson.Gson;
import exception.DataAccessException;
import model.AuthData;
import model.GameData;
import server.Server;
import server.ServerFacade;

import java.util.Arrays;

public class UserClient {
    private String visitorName = null;
    private String authToken = null;
    private static String serverUrl;
    private static Server server;
    private static ServerFacade sv;
    public static State state = State.LOGGEDOUT;


    public UserClient(String serverUrl){
        sv = new ServerFacade(serverUrl);
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
                    default -> help();
                };
            }
            else return switch (cmd) {
                case "logout" -> logout();
                case "create" -> create(params);
                case "list" -> list();
                default -> help();
            };

        }catch(Exception ex) {
            return ex.getMessage();
        }
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

    public String login(String... params) throws DataAccessException{
        if (params.length == 2){
            String visitorName = params[0];
            AuthData authData = sv.login(visitorName, params[1]);
            authToken = authData.authToken();
            state = State.LOGGEDIN;
            return String.format("Welcome %s", visitorName);
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
        String gameName = params[0];
        GameData gameData = sv.createGame(gameName, authToken);
        int gameID = gameData.gameID();
        return String.format("Your gameID is %d", gameID);
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
                join <ID> - a game
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

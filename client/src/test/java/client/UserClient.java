package client;
import exception.DataAccessException;
import server.Server;
import server.ServerFacade;

import java.util.Arrays;

public class UserClient {
    private static String serverUrl;
    private static Server server;
    private static ServerFacade sv;
    private State state = State.LOGGEDOUT;


    public UserClient(String serverUrl){
        sv = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
    }

    public String eval(String input){
        try{
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch(cmd){
                case "register" -> register(params);
                default -> help();
            };
        }catch(Exception ex) {
            return ex.getMessage();
        }
    }

    public String register(String... params) throws DataAccessException{
        if (params.length == 3){
            String visitorName = params[0];
            sv.register(visitorName, params[1], params[2]);
            state = State.LOGGEDIN;
            return String.format("Welcome", visitorName);
        }
        throw new DataAccessException("Expected username, password, and email");
    }

    public String help() {
        return """
                register <USERNAME> <PASSWORD> <EMAIL> - to create an account
                login <USERNAME> <PASSWORD> - to play chess
                quit - playing chess
                help - with possible commands
                """;    }


    private void assertSignedIn() throws DataAccessException {
        if (state == State.LOGGEDOUT) {
            throw new DataAccessException("You must sign in");
        }
    }
}

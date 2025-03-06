package server;

import com.google.gson.Gson;
import dataaccess.*;
//import dataaccess.MemoryDataAccess;
import dataaccess.MemoryGameDao;
import dataaccess.MemoryAuthAccess;
import dataaccess.MemoryUserAccess;
//import model.AuthData;
import model.*;
import service.*;
import spark.*;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Map;

//import static dataaccess.DatabaseManager.configureDatabase();

public class Server {

    private final ClearService clearService;
    private final RegisterService registerService;
    private final LoginService loginService;
    private final LogoutService logoutService;
    private final AddGameService addGameService;
    private final ListGameService listGameService;
    private final JoinGameService joinGameService;
    private final GameDAO gameDao;
    private final AuthDAO authDao;
    private final UserDAO userDao;


    public Server() {
        //    public MySqlUserAccess() throws DataAccessException {
//        configureDatabase();
//    }
        //this.dataAccess =
        //this.userData == new UserData()
        this.gameDao = new MemoryGameDao();
        this.authDao = new MemoryAuthAccess();
        try{
            userDao = new MySqlUserAccess();

        }catch(DataAccessException e) {
            userDao = new MemoryUserAccess();
        }
        this.clearService = new ClearService(gameDao, authDao, userDao);
        this.registerService = new RegisterService(authDao, userDao);
        this.loginService = new LoginService(authDao, userDao);
        this.logoutService = new LogoutService(authDao);
        this.addGameService = new AddGameService(authDao, gameDao);
        this.listGameService = new ListGameService(authDao, gameDao);
        this.joinGameService = new JoinGameService(authDao, gameDao);
    }


    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        Spark.delete("/db", this::clear);

        Spark.post("/user", this::register);

        Spark.post("/session", this::login);

        Spark.delete("/session", this::logout);

        Spark.get("/game", this::listgames);

        Spark.post("/game", this::createGame);

        Spark.put("/game", this::joinGame);

        // Register your endpoints and handle exceptions here.

        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();


        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private void errorUpdate(String message, Response res) {
        if(message.equals("Error: bad request")){
            res.status(400);
        }
        else if (message.equals("Error: unauthorized")) {
            res.status(401);
        }
        else if (message.equals("Error: already taken")) {
            res.status(403);
        }
        else {
            res.status(500);
        }
    }

    private Object clear(Request req, Response res) throws DataAccessException {
        //service.deleteAllPets();
        clearService.clear();
        res.status(200);
        return "{}";
    }

    private Object register(Request req, Response res) throws DataAccessException{
        String body = req.body();
        UserData jbody = new Gson().fromJson(body, UserData.class);
        try {
            AuthData authdata = registerService.registerUser(jbody);
            String jauth = new Gson().toJson(authdata);
            res.status(200);
            return jauth;
        } catch(Exception ex){
            errorUpdate(ex.getMessage(), res);
            return new Gson().toJson(Map.of("message", ex.getMessage()));
        }
    }

    private Object login(Request req, Response res) throws DataAccessException {
        String body = req.body();
        UserData jbody = new Gson().fromJson(body, UserData.class);
        try {
            AuthData authdata = loginService.loginUser(jbody);
            String jauth = new Gson().toJson(authdata);
            res.status(200);
            return jauth;
        } catch (Exception ex) {
            errorUpdate(ex.getMessage(), res);
            return new Gson().toJson(Map.of("message", ex.getMessage()));
        }
    }



    private Object logout(Request req, Response res) throws DataAccessException {
        String auth = req.headers("Authorization");
        //UserData jauth = new Gson().fromJson(auth);
        try {
            logoutService.logoutUser(auth);
            //String jauth = new Gson().toJson(authdata);
            res.status(200);
            return "{}";
        } catch (Exception ex) {
            errorUpdate(ex.getMessage(), res);
            return new Gson().toJson(Map.of("message", ex.getMessage()));
        }
    }

    private Object listgames(Request req, Response res) throws DataAccessException {
        String auth = req.headers("Authorization");
        try {
            Collection<GameData> games = listGameService.listGame(auth);
            String jgames = new Gson().toJson(Map.of("games", games));
            //String jgames = new Gson().toJson(games);
            res.status(200);
            return jgames;
        } catch (Exception ex) {
            errorUpdate(ex.getMessage(), res);
            return new Gson().toJson(Map.of("message", ex.getMessage()));
        }
    }

    private Object createGame(Request req, Response res) throws DataAccessException {
        String auth = req.headers("Authorization");
        String body = req.body();
        GameData jbody = new Gson().fromJson(body, GameData.class);
        try {
            GameData game = addGameService.addGame(auth, jbody);
            String jgameID = new Gson().toJson(Map.of("gameID", game.gameID()));
            res.status(200);
            return jgameID;
        } catch (Exception ex){
            errorUpdate(ex.getMessage(), res);
            return new Gson().toJson(Map.of("message", ex.getMessage()));
        }
    }

    private Object joinGame(Request req, Response res) throws DataAccessException {
        String auth = req.headers("Authorization");
        String body = req.body();
        ColorData jbody = new Gson().fromJson(body, ColorData.class);
        try{
            joinGameService.joinGame(auth, jbody);
            res.status(200);
            return "{}";
        } catch (Exception ex){
            errorUpdate(ex.getMessage(), res);
            return new Gson().toJson(Map.of("message", ex.getMessage()));
        }
    }
}

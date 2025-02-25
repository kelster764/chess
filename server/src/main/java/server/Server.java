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

public class Server {
//    private final loginService;
//    private final registerService;
//    private final joinGameService;
    //private final UserData userData;
    private final ClearService clearService;
    private final RegisterService registerService;
    private final LoginService loginService;
    private final LogoutService logoutService;
    private final GameDAO gameDao;
    private final AuthDAO authDao;
    private final UserDAO userDao;
    //private final DataAccess dataAccess;
    //private final WebSocketHandler webSocketHandler;

    public Server() {
        //this.dataAccess =
        //this.userData == new UserData()
        this.gameDao = new MemoryGameDao();
        this.authDao = new MemoryAuthAccess();
        this.userDao = new MemoryUserAccess();
        this.clearService = new ClearService(gameDao, authDao, userDao);
        this.registerService = new RegisterService(authDao, userDao);
        this.loginService = new LoginService(authDao, userDao);
        this.logoutService = new LogoutService(authDao);
//        this.LoginService = loginService;
//        this.RegisterService = registerService;
//        this.JoinGameService = joinGameService;
        //webSocketHandler = new WebSocketHandler();
    }


    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        Spark.delete("/db", this::clear);

        Spark.post("/user", this::register);

        Spark.post("/session", this::login);

        Spark.delete("/session", this::logout);

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
            AuthData authdata = registerService.RegisterUser(jbody);
            String jauth = new Gson().toJson(authdata);
            res.status(200);
            return jauth;
        } catch(Exception ex){
            if (ex.getMessage().equals("Error: already taken")){
                res.status(403);
                res.body(ex.getMessage());
                return new Gson().toJson(ex.getMessage());
            }
            else if(ex.getMessage().equals("Error: bad request")){
                res.status(400);
                res.body(ex.getMessage());
                return new Gson().toJson(ex.getMessage());
            }
            else{
                res.status(500);
                res.body(ex.getMessage());
                return new Gson().toJson(ex.getMessage());
            }
        }

            //{ "username":"", "password":"", "email":"" }
//        res.status(200);
//
//        return jauth;
    }

    private Object login(Request req, Response res) throws DataAccessException {
        String body = req.body();
        UserData jbody = new Gson().fromJson(body, UserData.class);
        try {
            AuthData authdata = loginService.LoginUser(jbody);
            String jauth = new Gson().toJson(authdata);
            res.status(200);
            return jauth;
        } catch (Exception ex) {
            if (ex.getMessage().equals("Error: unauthorized")) {
                res.status(401);
            }
            else{
                res.status(500);
            }
            res.body(ex.getMessage());
            return new Gson().toJson(ex.getMessage());
        }
    }

    private Object logout(Request req, Response res) throws DataAccessException {
        String auth = req.headers("Authorization");
        //UserData jauth = new Gson().fromJson(auth);
        try {
            logoutService.LogoutUser(auth);
            //String jauth = new Gson().toJson(authdata);
            res.status(200);
            return "{}";
        } catch (Exception ex) {
            if (ex.getMessage().equals("Error: unauthorized")) {
                res.status(401);
            }
            else{
                res.status(500);
            }
            res.body(ex.getMessage());
            return new Gson().toJson(ex.getMessage());

        }
    }

}

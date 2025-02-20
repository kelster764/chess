package server;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import service.ClearService;
import spark.*;

public class Server {
//    private final loginService;
//    private final registerService;
//    private final joinGameService;
    private final ClearService clearService;
    private final DataAccess dataAccess;
    //private final WebSocketHandler webSocketHandler;

    public Server() {
        this.dataAccess = new MemoryDataAccess();
        this.clearService = new ClearService(dataAccess);
//        this.LoginService = loginService;
//        this.RegisterService = registerService;
//        this.JoinGameService = joinGameService;
        //webSocketHandler = new WebSocketHandler();
    }


    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        Spark.delete("/db", this::clear);

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

}

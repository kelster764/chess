package client;

import dataaccess.DataAccessException;
import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.*;
import server.Server;
import server.ServerFacade;

import java.util.Collection;


public class ServerFacadeTests {
    private static String serverUrl;
    private static Server server;
    private ServerFacade sv;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        serverUrl = "http://localhost:" + port;
        System.out.println("Started test HTTP server on " + port);
    }
    @BeforeEach
    public void setUp(){
        sv = new ServerFacade(serverUrl);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void sampleTest() {
        Assertions.assertTrue(true);
    }

    @Test
    public void registerTest(){
        try {
            sv.clear();
            AuthData authData = sv.register("urmom", "ishot", "doIlooklike@gmail.com");
            System.out.println(authData.authToken());
            assert authData.authToken() != null;
            sv.logout(authData.authToken());

        }catch(Exception ex){
            ex.getMessage();
            Assertions.fail();
        }

    }
    @Test
    public void registerTestFail(){
        try {
            sv.clear();
            sv.register(null, "ishot", "doIlooklike@gmail.com");
            Assertions.fail();

        }catch(Exception ex){
            ex.getMessage();
            //Assertions.assertInstanceOf(DataAccessException.class, ex);
        }

    }

    @Test
    public void loginTest(){
        try {
            sv.clear();
            AuthData authData = sv.register("urmom", "ishot", "doIlooklike@gmail.com");
            sv.logout(authData.authToken());
            AuthData authData2 = sv.login("urmom", "ishot");
            System.out.println(authData.authToken());
            assert authData2.authToken() != null;

        }catch(Exception ex){
            ex.getMessage();
            Assertions.fail();
        }

    }

    @Test
    public void loginTestFail() {
        try {
            sv.clear();
            AuthData authData = sv.register("urmom", "ishot", "doIlooklike@gmail.com");
            sv.logout(authData.authToken());
            AuthData authData2 = sv.login("urmom", "iswrong");
            Assertions.fail();
            //assert authData2.authToken() != null;

        } catch (Exception ex) {
            ex.getMessage();
        }
    }

    @Test
    public void logoutTest(){
        try {
            sv.clear();
            AuthData authData = sv.register("urmom", "ishot", "doIlooklike@gmail.com");
            sv.logout(authData.authToken());
            sv.clear();

        }catch(Exception ex){
            ex.getMessage();
            Assertions.fail();
        }

    }

    @Test
    public void logoutTestFail(){
        try {
            sv.clear();
            //AuthData authData = sv.register("urmom", "ishot", "doIlooklike@gmail.com");
            sv.logout(null);
            sv.clear();
            Assertions.fail();
        }catch(Exception ex){
            ex.getMessage();

        }

    }

    @Test
    public void createGameTest(){
        try{
            sv.clear();
            AuthData authData = sv.register("urmom", "ishot", "blah");
            GameData gameData = sv.createGame("bestGame", authData.authToken());
            //AuthData authData2 = sv.register("cheese", "ishot", "blah");
            GameData gameData2 = sv.createGame("wahwah", authData.authToken());
            assert gameData.gameID() == 1;
            assert gameData2.gameID() == 2;
            sv.clear();

        } catch(Exception ex){
            ex.getMessage();
            Assertions.fail();

        }
    }

    @Test
    public void createGameTestFail(){
        try{
            sv.clear();
            AuthData authData = sv.register("urmom", "ishot", "blah");
            GameData gameData = sv.createGame(null, authData.authToken());
            Assertions.fail();

        } catch(Exception ex){
            ex.getMessage();
        }
    }

    @Test
    public void listGameTest(){
        try {
            sv.clear();
            AuthData authData = sv.register("urmom", "ishot", "blah");
            sv.createGame("bestGame", authData.authToken());
            Collection<String> gameData= sv.listGames(authData.authToken());
            assert gameData != null;
            sv.clear();

        }catch(Exception ex){
            ex.getMessage();
            Assertions.fail();
        }
    }

    @Test
    public void listGameTestFail(){
        try {
            sv.clear();
            AuthData authData = sv.register("urmom", "ishot", "blah");
            sv.createGame("bestGame", authData.authToken());
            Collection<String> gameData = sv.listGames(null);
            Assertions.fail();
            sv.clear();

        }catch(Exception ex){
            ex.getMessage();
        }
    }

//    @Test
//    public void getGameTest(){
//        try{
//            sv.clear();
//            AuthData authData = sv.register("urmom", "ishot", "blah");
//            GameData gameData = sv.createGame("bestGame", authData.authToken());
//            GameData game = sv.getGame(gameData.gameID(), authData.authToken());
//            assert gameData == game;
//        }catch(Exception ex){
//            ex.getMessage();
//            Assertions.fail();
//        }
//    }

    @Test
    public void joinGameTest(){
        try{
            sv.clear();
            AuthData authData = sv.register("urmom", "ishot", "blah");
            GameData gameData = sv.createGame("bestGame", authData.authToken());
            sv.joinGame(gameData.gameID(), "WHITE", authData.authToken());


        }catch(Exception ex){
            ex.getMessage();
            Assertions.fail();
        }
    }

    @Test
    public void joinGameTestFail(){
        try{
            sv.clear();
            AuthData authData = sv.register("urmom", "ishot", "blah");
            GameData gameData = sv.createGame("bestGame", authData.authToken());
            sv.joinGame(gameData.gameID(), null, authData.authToken());
            Assertions.fail();
        }catch(Exception ex){
            ex.getMessage();

        }
    }

}

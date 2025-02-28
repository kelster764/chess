package service;
import chess.ChessGame;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.DisplayName;
import service.*;
import dataaccess.*;
import model.*;
import org.junit.jupiter.api.*;

import org.junit.jupiter.api.Test;

import java.util.Collection;

public class ClearTest {
    MemoryAuthAccess authAccess = new MemoryAuthAccess();
    MemoryGameDao  gameAccess = new MemoryGameDao();
    MemoryUserAccess userAccess = new MemoryUserAccess();
    ClearService clearService = new ClearService(gameAccess, authAccess, userAccess);
    RegisterService registerService = new RegisterService(authAccess, userAccess);
    //loginService loginService = new LoginService(authDao, userDao);
//        this.logoutService = new LogoutService(authDao);
    AddGameService addGameService = new AddGameService(authAccess, gameAccess);
    ListGameService listGameService = new ListGameService(authAccess, gameAccess);
    JoinGameService joinGameService = new JoinGameService(authAccess, gameAccess);

    //AuthData authData = new RegisterService.
    String userName = "yomama";
    String password = "ishot";
    String email = "doIlooklike@gmail.com";
    ChessGame game = new ChessGame();

    UserData userData = new UserData(userName, password, email);
    GameData gameData = new GameData(1, userName, "Yourmom", "thegame", game);
    //AuthData authData = new MemoryAuthAccess().createAuth(userName);


    @Test
    @DisplayName("test clearing")
    public void clearPass(){
        gameAccess.createGame(gameData);
        userAccess.createUser(userData);
        authAccess.createAuth(userName);

        Assertions.assertFalse(userAccess.users.isEmpty());
        Assertions.assertFalse(gameAccess.chessGames.isEmpty());
        Assertions.assertFalse(authAccess.authentications.isEmpty());

        try {
            clearService.clear();
        }catch(Exception ex){
            Assertions.fail();
        }

        Assertions.assertTrue(userAccess.users.isEmpty());
        Assertions.assertTrue(gameAccess.chessGames.isEmpty());
        Assertions.assertTrue(authAccess.authentications.isEmpty());

    }
    @Test
    public void listGames(){
        Assertions.assertTrue(gameAccess.chessGames.isEmpty());
        gameAccess.createGame(gameData);
        AuthData auth = authAccess.createAuth(userName);
        try {
            Collection<GameData> games1= listGameService.listGame(auth.authToken());
            Collection<GameData> games2 = gameAccess.listGames();
            Assertions.assertSame(games1, games2);
        }catch(Exception ex){
            Assertions.fail();
        }

    }
    @Test
    public void listGamesFail(){
        try {
            listGameService.listGame("totally wrong authtoken");
        }catch(Exception ex){
            Assertions.assertInstanceOf(DataAccessException.class, ex);
        }
    }
    @Test
    public void addGameService(){
        Assertions.assertTrue(gameAccess.chessGames.isEmpty());
        AuthData auth = authAccess.createAuth(userName);
        try {
            addGameService.addGame(auth.authToken(), gameData);
        }catch(Exception ex){
            Assertions.fail();
        }

    }
    @Test
    public void addGameServiceFail(){
        Assertions.assertTrue(gameAccess.chessGames.isEmpty());
        AuthData auth = authAccess.createAuth(userName);
        try {
            addGameService.addGame("wrong token", gameData);
            Assertions.fail();
        }catch(Exception ex){
            Assertions.assertInstanceOf(DataAccessException.class, ex);
        }

    }
    @Test
    public void joinGameService(){
        Assertions.assertTrue(gameAccess.chessGames.isEmpty());
        GameData gameData2 = new GameData(1, null, "Yourmom", "thegame", game);
        gameAccess.createGame(gameData2);
        AuthData auth = authAccess.createAuth(userName);
        ColorData color = new ColorData("WHITE", 1);
        try {
            joinGameService.joinGame(auth.authToken(), color);
            GameData answer = gameAccess.getGame(1);
            GameData expected = new GameData(1,"yomama" , "Yourmom", "thegame", game);
            Assertions.assertEquals(answer, expected);
        }catch(Exception ex){
            Assertions.fail();
            //Assertions.assertInstanceOf(DataAccessException.class, ex);
        }

    }
    @Test
    public void joinGameServiceFail(){
        Assertions.assertTrue(gameAccess.chessGames.isEmpty());
        GameData gameData2 = new GameData(1, null, "Yourmom", "thegame", game);
        gameAccess.createGame(gameData2);
        AuthData auth = authAccess.createAuth(userName);
        ColorData color = new ColorData("BLUE", 1);
        try {
            joinGameService.joinGame(auth.authToken(), color);
            Assertions.fail();
            GameData answer = gameAccess.getGame(1);
            GameData expected = new GameData(1,"yomama" , "Yourmom", "thegame", game);

        }catch(Exception ex){
            Assertions.assertInstanceOf(DataAccessException.class, ex);
        }

    }




}

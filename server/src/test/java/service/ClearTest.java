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
//        this.addGameService = new AddGameService(authDao, gameDao);
    ListGameService listGameService = new ListGameService(authAccess, gameAccess);
//        this.joinGameService = new JoinGameService(authDao, gameDao);

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

}

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

public class ClearTest {
    MemoryAuthAccess authAccess = new MemoryAuthAccess();
    MemoryGameDao  gameAccess = new MemoryGameDao();
    MemoryUserAccess userAccess = new MemoryUserAccess();
    ClearService clearService = new ClearService(gameAccess, authAccess, userAccess);
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

    }
}

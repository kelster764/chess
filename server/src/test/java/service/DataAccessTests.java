package service;
import chess.ChessGame;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.DisplayName;
import dataaccess.*;
import model.*;
import org.junit.jupiter.api.*;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Objects;


public class DataAccessTests {
    private static final Logger LOG = LoggerFactory.getLogger(DataAccessTests.class);
    private UserDAO userDAO;
    private AuthDAO authDAO;
    private GameDAO gameDAO;


    @BeforeEach
    public void sqlSetUp() {
        try {
            userDAO = new MySqlUserAccess();
            authDAO = new MySqlAuthAccess();
            gameDAO = new MySqlGameAccess();
        } catch (DataAccessException e) {
            System.out.println(e.getMessage());
            userDAO = new MemoryUserAccess();
            authDAO = new MemoryAuthAccess();
            gameDAO = new MemoryGameDao();
        }
    }

    @Test
    @DisplayName("clear database")
    public void userClear() throws DataAccessException {
        userDAO.clearUser();
        userDAO.createUser(new UserData("cheese", "urmom", "cheese@gmail.com"));
        userDAO.clearUser();
        assert ((userDAO.getUser("cheese")) == null);
    }


    @Test
    @DisplayName("test clearing")
    public void userMake() throws DataAccessException {
        userDAO.clearUser();
        userDAO.createUser(new UserData("cheese", "cheeseisgreat", "cheese@gmail.com"));
        assert ((userDAO.getUser("cheese")) != null);
    }

    @Test
    public void userMakeFail() throws DataAccessException {
        userDAO.clearUser();
        try {
            userDAO.createUser(new UserData(null, "cheeseisgreat", "cheese@gmail.com"));
        } catch (Exception ex) {
            Assertions.assertInstanceOf(DataAccessException.class, ex);
        }
    }


    @Test
    @DisplayName("get name")
    public void userGet() throws DataAccessException {
        userDAO.clearUser();
        userDAO.createUser(new UserData("cheese", "cheeseisgreat", "cheese@gmail.com"));
        UserData user = userDAO.getUser("cheese");
        assert (user != null);
    }

    @Test
    public void userGetFail() throws DataAccessException {
        userDAO.clearUser();
        try {
            userDAO.getUser("cheese");
        } catch (Exception ex) {
            Assertions.assertInstanceOf(DataAccessException.class, ex);
        }
    }

    @Test
    public void clearAuth() throws DataAccessException {
        authDAO.clearAuth();
        authDAO.createAuth("cheese");
        authDAO.clearAuth();
        assert (authDAO.getAuth("cheese") == null);
    }

    @Test
    @DisplayName("createAuth")
    public void authMake() throws DataAccessException {
        authDAO.clearAuth();
        AuthData auth = authDAO.createAuth("cheese");
        assert (authDAO.getAuth(auth.authToken()) != null);
    }


    @Test
    public void authMakeFail() throws DataAccessException {
        authDAO.clearAuth();
        try {
            authDAO.createAuth(null);
        } catch (Exception ex) {
            Assertions.assertInstanceOf(DataAccessException.class, ex);
        }
    }

    @Test
    public void authDelete() throws DataAccessException {
        authDAO.clearAuth();
        try {
            AuthData auth = authDAO.createAuth("cheese");
            authDAO.deleteAuth(auth.authToken());
            assert (authDAO.getAuth(auth.authToken()) == null);
        } catch (Exception ex) {
            Assertions.fail();
        }
    }

    @Test
    public void authDeleteFail() throws DataAccessException {
        authDAO.clearAuth();
        try {
            AuthData auth = authDAO.createAuth(null);
            authDAO.deleteAuth(auth.authToken());
            Assertions.fail();
        } catch (Exception ex) {
            Assertions.assertInstanceOf(DataAccessException.class, ex);
        }
    }

    @Test
    @DisplayName("get auth")
    public void authGet() throws DataAccessException {
        authDAO.clearAuth();
        AuthData auth = authDAO.createAuth("cheese");
        assert (authDAO.getAuth(auth.authToken()) != null);
    }

    @Test
    public void authGetFail() throws DataAccessException {
        authDAO.clearAuth();
        try {
            //AuthData auth = authDAO.createAuth(null);
            AuthData auth = authDAO.getAuth("1234");
            assert (auth == null);
        } catch (Exception ex) {
            Assertions.assertInstanceOf(DataAccessException.class, ex);
        }
    }

    @Test
    void clearGame() throws DataAccessException {
        gameDAO.clearGames();
        gameDAO.createGame(new GameData(4, null, null, "yomama", new ChessGame()));
        gameDAO.clearGames();
        assert (gameDAO.getGame(4) == null);
    }

    @Test
    public void createGame() throws DataAccessException {
        gameDAO.clearGames();
        gameDAO.createGame(new GameData(4, null, null, "yomama", new ChessGame()));
        gameDAO.createGame(new GameData(4, null, null, "cheese", new ChessGame()));
        Collection<GameData> games = gameDAO.listGames();
        assert (!games.isEmpty());
    }

    @Test
    public void createGameFail() throws DataAccessException {
        gameDAO.clearGames();
        try {
            //AuthData auth = authDAO.createAuth(null);
            gameDAO.createGame(new GameData(4, null, null, null, new ChessGame()));
            Assertions.fail();
        } catch (Exception ex) {
            Assertions.assertInstanceOf(DataAccessException.class, ex);
        }
    }

    @Test
    public void listGames() throws DataAccessException {
        gameDAO.clearGames();
        gameDAO.createGame(new GameData(4, null, null, "cheese", new ChessGame()));
        Collection<GameData> games = gameDAO.listGames();
        assert (!games.isEmpty());
    }

    @Test
    public void listGamesFail() throws DataAccessException {
        gameDAO.clearGames();
        Collection<GameData> games = gameDAO.listGames();
        assert (games.isEmpty());

    }

    @Test
    public void deleteGame() throws DataAccessException {
        gameDAO.clearGames();
        GameData game = gameDAO.createGame(new GameData(1, null, null, "yomama", new ChessGame()));
        gameDAO.deleteGame(1);
        assert (gameDAO.getGame(1) == null);

    }

    @Test
    public void deleteGameFail() throws DataAccessException {
        gameDAO.clearGames();
        try {
            //AuthData auth = authDAO.createAuth(null);
            GameData game = gameDAO.createGame(new GameData(1, null, null, "yomama", new ChessGame()));
            gameDAO.deleteGame(2);
            assert (gameDAO.getGame(1) != null);
        } catch (Exception ex) {
            Assertions.assertInstanceOf(DataAccessException.class, ex);
        }
    }

    @Test
    public void getGame() throws DataAccessException {
        gameDAO.clearGames();
        gameDAO.createGame(new GameData(1, null, null, "yomama", new ChessGame()));
        GameData game = gameDAO.getGame(1);
        assert (game != null);
    }


}



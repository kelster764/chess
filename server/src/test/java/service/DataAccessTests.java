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
    @DisplayName("test clearing")
    public void usersql() throws DataAccessException{
        userDAO.createUser(new UserData("cheese", "cheeseisgreat", "cheese@gmail.com"));
    }

    @Test
    @DisplayName("clear database")
    public void user_clear() throws DataAccessException{
        userDAO.clearUser();
    }

    @Test
    @DisplayName("get name")
    public void user_get() throws DataAccessException{
        UserData user = userDAO.getUser("cheese");
        //Assertions.assertEquals(user, );
    }
    @Test
    @DisplayName("createAuth")
    public void auth_make() throws DataAccessException{
        AuthData auth = authDAO.createAuth("cheese");
    }
    @Test
    public void auth_delete() throws DataAccessException{
        authDAO.deleteAuth("bc02a5ca-bd66-4e4c-b320-d2219ac78427");
    }
    @Test
    @DisplayName("get auth")
    public void auth_get() throws DataAccessException{
        AuthData auth = authDAO.getAuth("a726f5ff-2eae-40db-874a-4c3f5ea392dc");
        //Assertions.assertEquals(user, );
    }

    @Test
    public void clearAuth() throws DataAccessException{
        authDAO.clearAuth();
    }
    @Test
    public void createGame() throws DataAccessException{
        gameDAO.createGame(new GameData(4, null, null, "yomama", new ChessGame()));
        gameDAO.createGame(new GameData(4, null, null, "cheese", new ChessGame()));
    }
    @Test void clearGame() throws DataAccessException{
        gameDAO.clearGames();
    }
    @Test
    public void list_games() throws DataAccessException{
        gameDAO.createGame(new GameData(4, null, null, "cheese", new ChessGame()));
        Collection<GameData> games = gameDAO.listGames();
        assert(!games.isEmpty());
    }
    @Test
    public void deleteGame() throws DataAccessException{
        gameDAO.deleteGame(2);
    }
    @Test
    public void updateGame() throws DataAccessException{
        gameDAO.updateGame(1, "shoelace", "wawa", "yomama", new ChessGame());
    }

}



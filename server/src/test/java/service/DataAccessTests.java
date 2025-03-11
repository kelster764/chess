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
    @DisplayName("clear database")
    public void userClear() throws DataAccessException{
        userDAO.clearUser();
        userDAO.createUser(new UserData("cheese", "urmom", "cheese@gmail.com"));
        userDAO.clearUser();
        assert ((userDAO.getUser("cheese")) == null);
    }



    @Test
    @DisplayName("test clearing")
    public void userMake() throws DataAccessException{
        userDAO.clearUser();
        userDAO.createUser(new UserData("cheese", "cheeseisgreat", "cheese@gmail.com"));
        assert ((userDAO.getUser("cheese")) != null);
    }

    @Test
    public void userMakeFail() throws DataAccessException{
        userDAO.clearUser();
        try{
            userDAO.createUser(new UserData(null, "cheeseisgreat", "cheese@gmail.com"));
        }catch (Exception ex){
            Assertions.assertInstanceOf(DataAccessException.class, ex);
        }
    }


    @Test
    @DisplayName("get name")
    public void userGet() throws DataAccessException{
        userDAO.clearUser();
        userDAO.createUser(new UserData("cheese", "cheeseisgreat", "cheese@gmail.com"));
        UserData user = userDAO.getUser("cheese");
        assert(user != null);
    }

    @Test
    public void userGetFail() throws DataAccessException{
        userDAO.clearUser();
        try{
            userDAO.getUser("cheese");
        }catch (Exception ex){
            Assertions.assertInstanceOf(DataAccessException.class, ex);
        }
    }

    @Test
    public void clearAuth() throws DataAccessException{
        authDAO.clearAuth();
        authDAO.createAuth("cheese");
        authDAO.clearAuth();
        assert(authDAO.getAuth("cheese") == null);
    }

    @Test
    @DisplayName("createAuth")
    public void authMake() throws DataAccessException{
        authDAO.clearAuth();
        AuthData auth  = authDAO.createAuth("cheese");
        assert(authDAO.getAuth(auth.authToken()) != null);
    }


    @Test
    public void authMakeFail() throws DataAccessException{
        authDAO.clearAuth();
        try{
            authDAO.createAuth(null);
        }catch (Exception ex){
            Assertions.assertInstanceOf(DataAccessException.class, ex);
        }
    }

    @Test
    public void authDelete() throws DataAccessException{
        authDAO.clearAuth();
        try{
            AuthData auth = authDAO.createAuth("cheese");
            authDAO.deleteAuth(auth.authToken());
            assert (authDAO.getAuth(auth.authToken()) == null);
        }catch (Exception ex){
            Assertions.fail();
        }
    }

    @Test
    public void authDeleteFail() throws DataAccessException{
        authDAO.clearAuth();
        try{
            AuthData auth = authDAO.createAuth(null);
            authDAO.deleteAuth(auth.authToken());
            Assertions.fail();
        }catch (Exception ex){
            Assertions.assertInstanceOf(DataAccessException.class, ex);
        }
    }

    @Test
    @DisplayName("get auth")
    public void auth_get() throws DataAccessException{
        AuthData auth = authDAO.getAuth("a726f5ff-2eae-40db-874a-4c3f5ea392dc");
        //Assertions.assertEquals(user, );
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



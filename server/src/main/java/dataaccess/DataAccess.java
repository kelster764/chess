package dataaccess;
import model.GameData;
import model.AuthData;
import model.UserData;

import java.util.Collection;

public interface DataAccess {
    void clear() throws DataAccessException;

    UserData createUser() throws DataAccessException;

    UserData getUser(String username) throws DataAccessException;

    GameData createGame() throws DataAccessException;

    GameData getGame(int gameID) throws DataAccessException;

    Collection<GameData> listGames() throws DataAccessException;

    void updateGame(int GameID) throws DataAccessException;

    AuthData createAuth() throws DataAccessException;

    AuthData getAuth(String authToken) throws DataAccessException;

    void deleteAuth(String authToken) throws DataAccessException;









    // initialize all the other functions
}

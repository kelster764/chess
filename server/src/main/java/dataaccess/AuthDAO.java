package dataaccess;

import model.AuthData;
import java.util.Collection;

public interface AuthDAO {
    void clearAuth() throws DataAccessException;

    AuthData createAuth(String userName) throws DataAccessException;

    AuthData getAuth(String authToken) throws DataAccessException;

    void deleteAuth(String authToken) throws DataAccessException;
}

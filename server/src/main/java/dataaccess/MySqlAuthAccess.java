package dataaccess;

import com.google.gson.Gson;
import model.AuthData;
import model.UserData;
import model.GameData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class MySqlAuthAccess implements AuthDAO {
    public MySqlAuthAccess() throws DataAccessException {
        configureDatabase();
    }
    public static String generateToken() {
        return UUID.randomUUID().toString();
    }

    public AuthData createAuth(String userName) throws DataAccessException {
        var statement = "INSERT INTO authData (authToken, username) VALUES (?, ?)";
        String token = generateToken();
        DatabaseManager.executeUpdate(statement, token, userName);
        return new AuthData(token, userName);
    }


    public void clearAuth() throws DataAccessException{
        var statement = "TRUNCATE authData";
        DatabaseManager.executeUpdate(statement);
    }
    public AuthData getAuth(String authToken) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT authToken, username FROM authData WHERE authToken=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readAuth(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
        return null;
    }

    private AuthData readAuth(ResultSet rs) throws DataAccessException{
        //var username = rs.getString("username");
        try {
        var authToken = rs.getString("authToken");
        var username = rs.getString("username");
        var auth = new AuthData(authToken, username);
        return auth;
        }catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public void deleteAuth(String authToken) throws DataAccessException{
        var statement = "DELETE FROM authData WHERE authToken=?";
        DatabaseManager.executeUpdate(statement, authToken);
    }


    public final String[] createStatements = {

            """
            CREATE TABLE IF NOT EXISTS  authData (
               `authToken` varchar(256) NOT NULL,
               `username` varchar(256) NOT NULL,
                PRIMARY KEY (`authToken`),
                INDEX(`username`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };



    private void configureDatabase()throws DataAccessException{
        DatabaseManager.configureDatabase(createStatements);
    }
}
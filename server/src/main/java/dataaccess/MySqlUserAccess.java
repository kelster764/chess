package dataaccess;

import com.google.gson.Gson;
import model.UserData;
import model.GameData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class MySqlUserAccess implements UserDAO{
    public MySqlUserAccess() throws DataAccessException{
        configureDatabase();
    }
    public UserData createUser(UserData userData) throws DataAccessException {
        var statement = "INSERT INTO userData (username, password, email) VALUES (?, ?, ?)";
        String hashedPassword = BCrypt.hashpw(userData.password(), BCrypt.gensalt());
        DatabaseManager.executeUpdate(statement, userData.username(), hashedPassword, userData.email());
        //executeUpdate(statement, userData.username(), userData.password(), userData.email());
        return new UserData(userData.username(), userData.password(), userData.email());
    }

    public void clearUser() throws DataAccessException{
        var statement = "TRUNCATE userData";
        DatabaseManager.executeUpdate(statement);
    }
    public UserData getUser(String username) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username, password, email FROM userData WHERE username=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readUser(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
        return null;
    }

    private UserData readUser(ResultSet rs) throws DataAccessException{
        //var username = rs.getString("username");
        try {
            var username = rs.getString("username");
            var password = rs.getString("password");
            var email = rs.getString("email");
            var user = new UserData(username, password, email);
            return user;
        }catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
    }




    public final String[] createStatements = {

            """
            CREATE TABLE IF NOT EXISTS  userData (
               `username` varchar(256) NOT NULL,
               `password` varchar(256) NOT NULL,
               `email` varchar(256) NOT NULL,
                PRIMARY KEY (`username`),
                INDEX(`password`),
                INDEX(`email`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };


    private void configureDatabase()throws DataAccessException{
        DatabaseManager.configureDatabase(createStatements);
    }
}

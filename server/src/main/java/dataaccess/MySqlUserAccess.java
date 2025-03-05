package dataaccess;

import com.google.gson.Gson;
import model.UserData;
import model.GameData;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class MySqlUserAccess implements UserDAO{
    public MySqlDataAccess() throws DataAccessException {
        configureDatabase();
    }
    public UserData addUser(UserData userData) throws DataAccessException {
        var statement = "INSERT INTO userData (username, password, email, json) VALUES (?, ?, ?)";
        var json = new Gson().toJson(userData);
        executeUpdate(statement, userData.username(), userData.password(), userData.email(), json);
        return new UserData(userData.username(), userData.password(), userData.email());
    }

    public void clear() throws DataAccessException{
        var statement = "TRUNCATE userData";
        executeUpdate(statement);
    }

    private void executeUpdate(String statement, Object... params) throws DataAccessException{
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement)) {
                for (var i = 0; i < params.length; i++) {
                    var param = params[i];
                    if (param instanceof String p) ps.setString(i + 1, p);
                    else if (param == null) ps.setNull(i + 1, NULL);
                }
                ps.executeUpdate();

//                var rs = ps.getGeneratedKeys();
//                if (rs.next()) {
//                    //return rs.getInt(1);
//                }
//
//                return 0;
            }
        } catch (SQLException e) {
            throw new DataAccessException("bad request");
        }
    }



    private final String[] createStatements = {

            """
            CREATE TABLE IF NOT EXISTS  userData (
               'username' varchar(256) NOT NULL,
               'password' varchar(256) NOT NULL,
               'email' varchar(256) NOT NULL,
               `json` TEXT DEFAULT NULL,
                PRIMARY KEY (`username`),
                INDEX('password'),
                INDEX('email')
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };


    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Error: bad request");
        }
    }
}

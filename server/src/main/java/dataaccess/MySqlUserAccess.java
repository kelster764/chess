package dataaccess;

import com.google.gson.Gson;
import model.UserData;
import model.GameData;

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
        var json = new Gson().toJson(userData);
        executeUpdate(statement, userData.username(), userData.password(), userData.email());
        return new UserData(userData.username(), userData.password(), userData.email());
    }

    public void clearUser() throws DataAccessException{
        var statement = "TRUNCATE userData";
        executeUpdate(statement);
    }
    public UserData getUser(String username) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT id, json FROM userData WHERE username=?";
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

    private UserData readUser(ResultSet rs) throws SQLException {
        //var username = rs.getString("username");
        var json = rs.getString("json");
        var user = new Gson().fromJson(json, UserData.class);
        return user;
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


    private void configureDatabase(String[] statements) throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : statements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    private void configureDatabase()throws DataAccessException{
        configureDatabase(createStatements);
    }
}

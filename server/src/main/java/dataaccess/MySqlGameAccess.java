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

public class MySqlGameAccess implements GameDAO{
    public mySqlGameAccess() throws DataAccessException{
        configureDatabase();
    }

    public GameData createGame(GameData game) throws DataAccessException {
        var statement = "INSERT INTO gameData (whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?, ?)";
        var json = new Gson().toJson(game.game());
        executeUpdate(statement, game.whiteUsername(), game.blackUsername(), game.gameName(), json);
        return game;
    }

    public void clearGames() throws DataAccessException {
        var statement = "TRUNCATE gameData";
        executeUpdate(statement);
    }
    public GameData getGame(int gameID) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT whiteUsername, blackUsername, gameName, game FROM gameData WHERE gameID=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readGame(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
        return null;
    }

    private void executeUpdate(String statement, Object... params) throws DataAccessException{
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement)) {
                for (var i = 0; i < params.length; i++) {
                    var param = params[i];
                    if (param instanceof String p) ps.setString(i + 1, p);
                    else if (param instanceof Integer p) ps.setInt(i + 1, p);
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
//I need to figure out how to put the game object into the thing
            """
            CREATE TABLE IF NOT EXISTS  gameData (
               `gameID` int NOT NULL AUTO_INCREMENT,
               `whiteUsername` varchar(256),
               `blackUsername` varchar(256),
               `gameName` varchar(256) NOT NULL,
               `game` TEXT DEFAULT NULL,
                PRIMARY KEY (`gameID`),
                INDEX(whiteUsername),
                INDEX(blackUsername),
                INDEX(gameName),
                INDEX(game),
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

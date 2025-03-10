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





    public final String[] createStatements = {
//I need to figure out how to put the game object into the thing
            """
            CREATE TABLE IF NOT EXISTS  gameData (
               `gameID` int NOT NULL AUTO_INCREMENT,
               `whiteUsername` varchar(256),
               `blackUsername` varchar(256),
               `gameName` varchar(256) NOT NULL;
                PRIMARY KEY (`gameID`),
                INDEX(whiteUsername),
                INDEX(blackUsername),
                INDEX(gameName),
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

package dataaccess;
import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;
import dataaccess.DatabaseManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class MySqlGameAccess implements GameDAO{
    public MySqlGameAccess() throws DataAccessException{
        configureDatabase();
    }

    public GameData createGame(GameData game) throws DataAccessException {
        var statement = "INSERT INTO gameData (whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?)";
        var json = new Gson().toJson(new ChessGame());
        var gameID = DatabaseManager.executeUpdate(statement, game.whiteUsername(), game.blackUsername(), game.gameName(), json);
//        var gameID = executeUpdate(statement, game.whiteUsername(), game.blackUsername(), game.gameName(), json);
        return new GameData(gameID, game.whiteUsername(), game.blackUsername(), game.gameName(), new ChessGame());
    }

    public void clearGames() throws DataAccessException {
        var statement = "TRUNCATE gameData";
        DatabaseManager.executeUpdate(statement);
        //executeUpdate(statement);
    }

    public void deleteGame(int gameID) throws DataAccessException {
        var statement = "DELETE FROM gameData WHERE gameID=?";
        DatabaseManager.executeUpdate(statement, gameID);
    }

public void updateGame(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame chessGame) throws DataAccessException{
    //GameData game = new GameData(gameID, whiteUsername, blackUsername, gameName, chessGame);
    deleteGame(gameID);
    var json = new Gson().toJson(chessGame);
    var statement = "INSERT INTO gameData (gameID, whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?, ?)";
    DatabaseManager.executeUpdate(statement, gameID, whiteUsername, blackUsername, gameName, json);

}

    public GameData getGame(int gameID) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM gameData WHERE gameID=?";
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

    public Collection<GameData> listGames() throws DataAccessException {
        var result = new ArrayList<GameData>();
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM gameData";
            try (var ps = conn.prepareStatement(statement)) {
                try (var rs = ps.executeQuery()) {
                    while (rs.next()) {
                        result.add(readGame(rs));
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
        return result;
    }

    private GameData readGame(ResultSet rs) throws DataAccessException{
        //var username = rs.getString("username");
        try {
            var gameID = rs.getInt("gameID");
            var whiteUsername = rs.getString("whiteUsername");
            var blackUsername = rs.getString("blackUsername");
            var gameName = rs.getString("gameName");
            var json = rs.getString("game");
            var game = new Gson().fromJson(json, ChessGame.class);
//            if(game == null){
//                game = new ChessGame();
//            }
            var read = new GameData(gameID, whiteUsername, blackUsername, gameName, game);
            return read;
        }catch (Exception e) {
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
                INDEX(game(100))
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };


    private void configureDatabase()throws DataAccessException{
        DatabaseManager.configureDatabase(createStatements);
    }

}

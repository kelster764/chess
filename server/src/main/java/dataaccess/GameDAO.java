package dataaccess;
import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.Collection;



public interface GameDAO{
    void clearGames() throws DataAccessException;

    GameData createGame(GameData gameName) throws DataAccessException;

    GameData getGame(int gameID) throws DataAccessException;

    Collection<GameData> listGames() throws DataAccessException;

    void deleteGame(int gameID) throws DataAccessException;

    void updateGame(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) throws DataAccessException;

    // initialize all the other functions
}

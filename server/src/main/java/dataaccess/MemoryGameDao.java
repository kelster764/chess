package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;


//public record GameData(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {
//}
public class MemoryGameDao implements GameDAO {
    private int nextId = 1;
    final private HashMap<Integer, GameData> chessGames = new HashMap<>();

    public GameData createUser(GameData game) {
        game = new GameData(nextId++ , game.whiteUsername(), game.blackUsername(), game.gameName(), game.game());
        //int id = nextId ++;
        chessGames.put(game.gameID(), game);
        return game;
    }
    public Collection<GameData> listGames(){
        return chessGames.values();
    }

    public GameData getGame(int gameID){
        return chessGames.get(gameID);
    }

    public void deleteGame(int gameID){
        chessGames.remove(gameID);
    }
    public void updateGame(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame chessGame){
        GameData game = new GameData(gameID, whiteUsername, blackUsername, gameName, chessGame);
        deleteGame(gameID);
        chessGames.put(gameID, game);

    }

    public void clearGames() {
        chessGames.clear();
    }

}

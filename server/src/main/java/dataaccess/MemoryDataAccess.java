package dataaccess;

import chess.ChessGame;

import java.util.UUID;
import java.util.Collection;
import java.util.HashMap;

public class MemoryDataAccess implements DataAccess {
    private int nextId = 1;
    final private HashMap<int, ChessGame> chessGames = new HashMap<>();

    public ChessGame addGame(ChessGame game) {
        game = new ChessGame();
        int id = nextId ++;
        chessGames.put(id, game);
        return game;
    }
    public static String generateToken() {
        return UUID.randomUUID().toString();
    }
    public void clear() {chessGames.clear();
    }
}

package dataaccess;
import chess.ChessGame;
import model.AuthData;
import model.GameData;
import java.util.UUID;

import java.util.Collection;
import java.util.HashMap;

public class MemoryAuthAccess implements AuthDAO{;
    public HashMap<String, AuthData> authentications = new HashMap<>();

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }

    public AuthData createAuth(String userName) {
        String token = generateToken();
        AuthData auth = new AuthData(token, userName);
        //int id = nextId ++;
        authentications.put(token, auth);
        return auth;
    }
    public AuthData getAuth(String authToken){
        return authentications.get(authToken);
    }
    public void deleteAuth(String authToken){
        authentications.remove(authToken);
    }
    public void clearAuth() {
        authentications.clear();
    }
}

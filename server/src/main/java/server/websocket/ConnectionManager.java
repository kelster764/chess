package server.websocket;

import model.GameData;
import spark.Session;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<Integer, Set<Session>> sessionMap= new ConcurrentHashMap<>();

    public void addSessionToGame(Integer gameID, Session session){
        Set<Session >currentSet = sessionMap.getOrDefault(gameID, Set.of());
        currentSet.add(session);
        sessionMap.put(gameID, currentSet);
    }



}

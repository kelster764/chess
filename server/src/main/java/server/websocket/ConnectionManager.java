package server.websocket;

import model.GameData;
import spark.Session;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<Integer, Set<Session>> sessionMap= new ConcurrentHashMap<>();


}

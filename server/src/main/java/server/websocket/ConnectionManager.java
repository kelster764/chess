package server.websocket;

import model.GameData;
//import spark.Session;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.Notification;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<Integer, Map<Session, String>> sessionMap= new ConcurrentHashMap<>();

    public void addSessionToGame(Integer gameID, Session session, String userName){
//        Set<Session>currentSet = sessionMap.getOrDefault(gameID, Map.of());
//        currentSet.add(session);
//        sessionMap.put(gameID, currentSet);
        sessionMap.putIfAbsent(gameID, new ConcurrentHashMap<>());
        sessionMap.get(gameID).put(session, userName);
    }

    public void removeSessionFromGame(Integer gameID, Session session){
//        Set<Session>currentSet = sessionMap.getOrDefault(gameID, Set.of());
//        currentSet.remove(session);
//        sessionMap.put(gameID, currentSet);
        if (sessionMap.containsKey(gameID)){
            sessionMap.get(gameID).remove(session);
            if(sessionMap.isEmpty()){
                removeGame(gameID);
            }
        }
    }
    public void removeGame(Integer gameID){
        sessionMap.remove(gameID);
    }


    public Map<Session, String> getSessionsForGame(Integer gameID){
        Map<Session, String> currentMap = sessionMap.getOrDefault(gameID, new ConcurrentHashMap<>());
        return currentMap;
    }

    public void broadcast(Integer gameID, Session currentSession, Notification message) throws IOException {
        var idSessions = getSessionsForGame(gameID);
        if(message.type() == Notification.Type.LOAD_GAME){
            String msg = message.toString();
            currentSession.getRemote().sendString(msg);
        }
        else if (message.type() == Notification.Type.NOTIFICATION) {
            for (Map.Entry<Session, String> entry : idSessions.entrySet()) {
                Session session = entry.getKey();
                if (session.isOpen() && session != currentSession) {
                    String msg = message.toString();
                    session.getRemote().sendString(msg);
                }
            }
        }
    }
}

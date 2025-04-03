package server.websocket;

import model.GameData;
//import spark.Session;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.Notification;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<Integer, Set<Session>> sessionMap= new ConcurrentHashMap<>();

    public void addSessionToGame(Integer gameID, Session session){
        Set<Session>currentSet = sessionMap.getOrDefault(gameID, Set.of());
        currentSet.add(session);
        sessionMap.put(gameID, currentSet);
    }

    public void removeSessionFromGame(Integer gameID, Session session){
        Set<Session>currentSet = sessionMap.getOrDefault(gameID, Set.of());
        currentSet.remove(session);
        sessionMap.put(gameID, currentSet);
    }
    public void removeGame(Integer gameID){
        sessionMap.remove(gameID);
    }


    public Set<Session> getSessionsForGame(Integer gameID){
        Set<Session>currentSet = sessionMap.getOrDefault(gameID, Set.of());
        return currentSet;
    }

    public void broadcast(Integer gameID, Session currentSession, Notification message) throws IOException {
        var idSessions = getSessionsForGame(gameID);
        for (Session session : idSessions){
            if (session.isOpen() && session!= currentSession){
                String msg = message.toString();
                session.getRemote().sendString(msg);
            }
        }

    }


}

package server.websocket;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;
//import spark.Session;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.LoadGame;
import websocket.messages.Notification;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


public class ConnectionManager {
    public final ConcurrentHashMap<Integer, Map<Session, String>> sessionMap = new ConcurrentHashMap<>();

    public void addSessionToGame(Integer gameID, Session session, String userName) {
        sessionMap.putIfAbsent(gameID, new ConcurrentHashMap<>());
        sessionMap.get(gameID).put(session, userName);
    }

    public void removeSessionFromGame(Integer gameID, Session session) {
        if (sessionMap.containsKey(gameID)) {
            sessionMap.get(gameID).remove(session);
            if (sessionMap.isEmpty()) {
                removeGame(gameID);
            }
        }
    }

    public void removeGame(Integer gameID) {
        sessionMap.remove(gameID);
    }


    public Map<Session, String> getSessionsForGame(Integer gameID) {
        Map<Session, String> currentMap = sessionMap.getOrDefault(gameID, new ConcurrentHashMap<>());
        return currentMap;
    }

    public void broadcast(Integer gameID, Session currentSession, ServerMessage serverMessage) throws IOException {
        var idSessions = getSessionsForGame(gameID);
        String message = new Gson().toJson(serverMessage);
        for (Map.Entry<Session, String> entry : idSessions.entrySet()) {
            Session session = entry.getKey();
            if (session.isOpen() && session != currentSession) {
                String msg = message.toString();
                session.getRemote().sendString(msg);
            }
        }
    }

    public void broadcastToRoot(ServerMessage serverMessage, Session currentSession) throws IOException {
        String message = new Gson().toJson(serverMessage);
        currentSession.getRemote().sendString(message);
    }
}

    //        if(Objects.equals(userName, game.whiteUsername())){
//            //message = String.format("%s joined", gameData.whiteUsername());
//
//            currentSession.getRemote().sendString(message);
//        }
//        else if(Objects.equals(userName, game.blackUsername())){
//            //message = String.format("%s joined", gameData.whiteUsername());
//            currentSession.getRemote().sendString(message);
//        }
//        else{
//            currentSession.getRemote().sendString(message);
//        }
    //Notification notification = new Notification("user has joined game");
    //broadcast(gameID, currentSession, notification);



//    public void broadcastResign(Integer gameID, Session currentSession, Notification message) throws IOException {
//        var errorMessage = new Notification(Notification.Type.ERROR, "error");
//        var idSessions = getSessionsForGame(gameID);
//        String msg = message.toString();
//        String error = errorMessage.toString();
//        if (idSessions != null) {
//            if (message.type() == Notification.Type.NOTIFICATION || message.type() == Notification.Type.LOAD_GAME) {
//                for (Map.Entry<Session, String> entry : idSessions.entrySet()) {
//                    Session session = entry.getKey();
//                    if (session.isOpen()) {
//                        session.getRemote().sendString(msg);
//                    }
//                }
//            }
//            } else {
//            currentSession.getRemote().sendString(error);
//        }
//    }

//    public void broadcastConnect(Integer gameID, Session currentSession, Notification message) throws IOException {
//        var errorMessage = new Notification(Notification.Type.ERROR, "error");
//        var idSessions = getSessionsForGame(gameID);
//        String msg = message.toString();
//        String error = errorMessage.toString();
//        if (idSessions != null) {
//            if (message.type() == Notification.Type.LOAD_GAME) {
//                currentSession.getRemote().sendString(msg);
//            } else if (message.type() == Notification.Type.NOTIFICATION) {
//                for (Map.Entry<Session, String> entry : idSessions.entrySet()) {
//                    Session session = entry.getKey();
//                    if (session.isOpen() && session != currentSession) {
//                        session.getRemote().sendString(msg);
//                    }
//                }
//            }
//            }
//        else {
//            currentSession.getRemote().sendString(error);
//        }
//    }
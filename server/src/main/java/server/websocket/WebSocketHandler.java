package server.websocket;


import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;

import websocket.messages.Notification;
import websocket.messages.ServerMessage;
import websocket.commands.UserGameCommand;


@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        UserGameCommand userGameCommand = new Gson().fromJson(message, UserGameCommand.class);
        switch (userGameCommand.getCommandType()){
            case CONNECT -> connect(userGameCommand.getGameID(), session);
            //case MAKE_MOVE -> ;
            case LEAVE -> leave(userGameCommand.getGameID(), session);
            case RESIGN -> resign(userGameCommand.getGameID());
        }
    }
    private void connect(int gameID, Session session) throws IOException {
        connections.addSessionToGame(gameID, session);
        var messageLoad = new Notification(Notification.Type.LOAD_GAME, "game loading");
        var messageNotif = new Notification(Notification.Type.NOTIFICATION, "user joined");
        connections.broadcast(gameID, null, messageLoad);
        connections.broadcast(gameID, session, messageNotif);
    }

    private void leave(int gameID, Session session) throws IOException {
        connections.removeSessionFromGame(gameID, session);
        var messageNotif = new Notification(Notification.Type.NOTIFICATION, "user left");
        connections.broadcast(gameID, session, messageNotif);
    }

    private void resign(int gameID) throws IOException {
        connections.removeGame(gameID);
        var messageNotif = new Notification(Notification.Type.NOTIFICATION, "user resigned");
        connections.broadcast(gameID, null, messageNotif);
    }



}

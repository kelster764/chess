package server.websocket;


import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.MySqlAuthAccess;
import model.AuthData;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;

import websocket.messages.Notification;
import websocket.messages.ServerMessage;
import websocket.commands.UserGameCommand;
import dataaccess.*;

@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private AuthDAO authDao;

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException, DataAccessException {
        UserGameCommand userGameCommand = new Gson().fromJson(message, UserGameCommand.class);
        AuthData authData = authDao.getAuth(userGameCommand.getAuthToken());
        String userName = authData.username();
        switch (userGameCommand.getCommandType()){
            case CONNECT -> connect(userGameCommand.getGameID(), session, userName);
            case MAKE_MOVE -> move(userGameCommand.getGameID(), session, userName);
            case LEAVE -> leave(userGameCommand.getGameID(), session, userName);
            case RESIGN -> resign(userGameCommand.getGameID(), session, userName);
        }
    }
    private void connect(int gameID, Session session, String userName) throws IOException {
        connections.addSessionToGame(gameID, session, userName);
        var messageLoad = new Notification(Notification.Type.LOAD_GAME, "game loading");
        var messageNotif = new Notification(Notification.Type.NOTIFICATION, String.format("%s has joined", userName));
        connections.broadcastConnect(gameID, session, messageLoad);
        connections.broadcastConnect(gameID, session, messageNotif);
    }

    private void move(int gameID, Session session, String userName) throws IOException {
        //server verifies the calidity of the move
        //Game is updated to represent the move. Game is updated in the database.
        //needs to say what move was made
        var messageNotif = new Notification(Notification.Type.NOTIFICATION, String.format("%s has moved", userName));
        connections.broadcastResign(gameID, session, messageNotif);
        //If the move results in check, checkmate or stalemate the server sends a Notification message to all clients.

    }

    private void leave(int gameID, Session session, String userName) throws IOException {
        //update game to remove root client, but i think this is already done in userfacade??
        connections.removeSessionFromGame(gameID, session);
        var messageNotif = new Notification(Notification.Type.NOTIFICATION, String.format("%s has left", userName));
        connections.broadcastConnect(gameID, session, messageNotif);
    }

    private void resign(int gameID, Session session, String userName) throws IOException {
        //connections.removeGame(gameID);
        var messageNotif = new Notification(Notification.Type.NOTIFICATION, String.format("%s has resigned", userName));
        //all clients
        connections.broadcastResign(gameID, session, messageNotif);
        //I need to stop people from playing somehow...
    }



}

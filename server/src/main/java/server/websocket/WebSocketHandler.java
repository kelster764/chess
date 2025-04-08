package server.websocket;


import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.MySqlAuthAccess;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;

import websocket.messages.Error;
import websocket.messages.LoadGame;
import websocket.messages.Notification;
import websocket.messages.ServerMessage;
import websocket.commands.UserGameCommand;
import dataaccess.*;

@WebSocket
public class WebSocketHandler {
    private AuthDAO authDao;
    private GameDAO gameDAO;
    public WebSocketHandler(AuthDAO authDAO, GameDAO gameDao){
       this.authDao = authDAO;
       this.gameDAO = gameDao;
    }

    private final ConnectionManager connections = new ConnectionManager();


    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException, DataAccessException {
        UserGameCommand userGameCommand = new Gson().fromJson(message, UserGameCommand.class);
        String authToken = userGameCommand.getAuthToken();
        AuthData authData = authDao.getAuth(authToken);

        GameData gameData = gameDAO.getGame(userGameCommand.getGameID());
        String userName = "steve";
        switch (userGameCommand.getCommandType()){
            case CONNECT -> connect(gameData, session, authData);
            case MAKE_MOVE -> move(userGameCommand.getGameID(), session, userName);
            case LEAVE -> leave(userGameCommand.getGameID(), session, userName);
            case RESIGN -> resign(userGameCommand.getGameID(), session, userName);
        }
    }
    private void connect(GameData gameData, Session session, AuthData authData) throws IOException {
        try{String userName = authData.username();
            connections.addSessionToGame(gameData.gameID(), session, userName);

            //var messageLoad = new Notification(Notification.Type.LOAD_GAME, "game loading");
//        var messageNotif = new Notification(Notification.Type.NOTIFICATION, String.format("%s has joined", userName));
            var messageLoad = new LoadGame(gameData);
            connections.broadcastToRoot(messageLoad, session);
            Notification notification = new Notification(String.format("%s has joined", userName));
            connections.broadcast(gameData.gameID(), session, notification);
        }catch(Exception ex){
            connections.broadcastToRoot(new Error("error: not authenticated"), session);   }

//        if(authData.username() == null){
//            connections.broadcastToRoot(new Error("error: not authenticated"), session);
//        }

//        connections.broadcastConnect(gameData.gameID(), session, messageNotif);
    }

    private void move(int gameID, Session session, String userName) throws IOException {
        //server verifies the calidity of the move
        //Game is updated to represent the move. Game is updated in the database.
        //needs to say what move was made

        //var messageNotif = new Notification(Notification.Type.NOTIFICATION, String.format("%s has moved", userName));
        //connections.broadcastResign(gameID, session, messageNotif);

        //If the move results in check, checkmate or stalemate the server sends a Notification message to all clients.

    }

    private void leave(int gameID, Session session, String userName) throws IOException {
        //update game to remove root client, but i think this is already done in userfacade??
        connections.removeSessionFromGame(gameID, session);

        //var messageNotif = new Notification(Notification.Type.NOTIFICATION, String.format("%s has left", userName));
        //connections.broadcastConnect(gameID, session, messageNotif);
    }

    private void resign(int gameID, Session session, String userName) throws IOException {
        //connections.removeGame(gameID);

        //var messageNotif = new Notification(Notification.Type.NOTIFICATION, String.format("%s has resigned", userName));
        //all clients

        //connections.broadcastResign(gameID, session, messageNotif);
        //I need to stop people from playing somehow...
    }



}

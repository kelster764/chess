package ui;

import com.google.gson.Gson;
import exception.DataAccessException;
import websocket.commands.UserGameCommand;
import websocket.messages.LoadGame;
import websocket.messages.Notification;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketFacade extends Endpoint {
    Session session;
    ServerMessageHandler serverMessageHandler;

    public WebSocketFacade(String url, ServerMessageHandler serverMessageHandler) throws DataAccessException, URISyntaxException, DeploymentException, IOException {
        url = url.replace("http", "ws");
        URI socketURI = new URI(url + "/ws");

        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        this.session = container.connectToServer(this, socketURI);
        this.serverMessageHandler = serverMessageHandler;

        this.session.addMessageHandler(new MessageHandler.Whole<String>() {
            @Override
            public void onMessage(String message) {
                ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
                //String realmessage = serverMessage.getMessage();
                ServerMessage.ServerMessageType serverMessageType = serverMessage.getServerMessageType();
                if(serverMessageType == ServerMessage.ServerMessageType.NOTIFICATION){
                    Notification notification = new Gson().fromJson(message, Notification.class);
                    serverMessageHandler.notify(notification);
                }
                else if(serverMessageType == ServerMessage.ServerMessageType.LOAD_GAME){
                    LoadGame loadGame = new Gson().fromJson(message, LoadGame.class);
                    serverMessageHandler.load(loadGame);
                }
                else if(serverMessageType == ServerMessage.ServerMessageType.ERROR){
                    Error error = new Gson().fromJson(message, Error.class);
                    serverMessageHandler.error(error);
                }


                //recieves server message
                // going to be load game, or notification, or error
//                try {
//                    switch (serverMessage.getServerMessageType()) {
//                        cas -> connect(message);
//                        //case MAKE_MOVE -> move(gameData, session, authData, message);
//                        //case LEAVE -> leave(gameData, session, authData);
//                        //case RESIGN -> resign(gameData, session, authData);
//                    }
//                }catch(Exception ex){
//                    throw new RuntimeException(ex.getMessage());
//                }
            }
        });

    }









    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void connect(String message) throws DataAccessException, IOException {
        //var userGameCommand = message;
        //UserGameCommand userGameCommand = new Gson().fromJson(message, UserGameCommand.class);
        //Notification notification = new Notification("user has joined");
        //String serverMessage = new Gson().toJson(notification);
        this.session.getBasicRemote().sendText(message);
    }

    public void send(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }


}

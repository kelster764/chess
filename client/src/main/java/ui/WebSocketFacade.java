package ui;

import com.google.gson.Gson;
import exception.DataAccessException;
import websocket.commands.UserGameCommand;
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
                Notification serverMessage = new Gson().fromJson(message, Notification.class);
                //String realmessage = serverMessage.getMessage();
                serverMessageHandler.notify(serverMessage);
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

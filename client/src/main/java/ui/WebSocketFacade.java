package ui;

import com.google.gson.Gson;
import exception.DataAccessException;
import websocket.commands.UserGameCommand;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketFacade extends Endpoint {
    Session session;

    public WebSocketFacade(String url) throws DataAccessException, URISyntaxException, DeploymentException, IOException {
        url = url.replace("http", "ws");
        URI socketURI = new URI(url + "/ws");

        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        this.session = container.connectToServer(this, socketURI);

        this.session.addMessageHandler(new MessageHandler.Whole<String>() {
            @Override
            public void onMessage(String message) {
                UserGameCommand userGameCommand = new Gson().fromJson(message, UserGameCommand.class);
                try {
                    switch (userGameCommand.getCommandType()) {
                        case CONNECT -> connect(message);
                        //case MAKE_MOVE -> move(gameData, session, authData, message);
                        //case LEAVE -> leave(gameData, session, authData);
                        //case RESIGN -> resign(gameData, session, authData);
                    }
                }catch(Exception ex){
                    throw new RuntimeException(ex.getMessage());
                }
            }
        });

    }









    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void connect(String message) throws DataAccessException, IOException {
        var userGameCommand = message;
        this.session.getBasicRemote().sendText(message);
    }
}

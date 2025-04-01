package server.websocket;


import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;
import websocket.messages.ServerMessage;
import websocket.commands.UserGameCommand;


@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        UserGameCommand userGameCommand = new Gson().fromJson(message, UserGameCommand.class);
        switch (userGameCommand.getCommandType()){
            case CONNECT -> ;
            case MAKE_MOVE -> ;
            case LEAVE -> ;
            case RESIGN -> ;
            }
        }
    }
}

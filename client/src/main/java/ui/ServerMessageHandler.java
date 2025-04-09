package ui;

import websocket.messages.LoadGame;
import websocket.messages.Notification;
import websocket.messages.ServerMessage;

public interface ServerMessageHandler {
    void notify(Notification notification);
    void load(LoadGame loadGame);
    void error(Error error);
}

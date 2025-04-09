package websocket.messages;

import model.GameData;

public class Notification extends ServerMessage{
    String message;
    public Notification(String message){
        super(ServerMessageType.NOTIFICATION);
        this.message = message;
    }
    public String getMessage(){
        return message;
    }
}

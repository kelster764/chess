package websocket.messages;

import com.google.gson.Gson;

public record Notification(Type type, String message) {
    public enum Type {
        LOAD_GAME,
        ERROR,
        NOTIFICATION
    }
    public String toString() {
        return new Gson().toJson(this);
    }

}

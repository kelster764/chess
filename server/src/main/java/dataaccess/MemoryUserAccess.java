package dataaccess;

import model.UserData;
import model.GameData;

import java.util.Collection;
import java.util.HashMap;

//public record UserData(String username, String password, String email) {
//
//}
public class MemoryUserAccess implements UserDAO {
    //private int nextId = 1;
    public HashMap<String, UserData> users = new HashMap<>();

    public UserData getUser(String username){
        return users.get(username);
    }

    public UserData createUser(UserData user) {
        if(! users.containsKey(user.username())){
            users.put(user.username(), user);
        }

        return user;
    }
    public void clearUser() {
        users.clear();
    }
}

package dataaccess;

import model.UserData;
import model.GameData;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Collection;
import java.util.HashMap;

import static org.mindrot.jbcrypt.BCrypt.gensalt;

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
            String password = BCrypt.hashpw(user.password(), BCrypt.gensalt());
            users.put(user.username(), new UserData(user.username(), password, user.password()));
        }

        return user;
    }
    public void clearUser() {
        users.clear();
    }
}


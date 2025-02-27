package service;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.DisplayName;
import service.*;
import dataaccess.*;
import model.*;
import org.junit.jupiter.api.*;

import org.junit.jupiter.api.Test;

public class ClearTest {
    @Test
    @DisplayName("test clearing")
    public void clearPass(){
        MemoryAuthAccess authAccess = new MemoryAuthAccess();
        MemoryGameDao  gameAccess = new MemoryGameDao();
        MemoryUserAccess userAccess = new MemoryUserAccess();
        ClearService clearService = new ClearService(gameAccess, authAccess, userAccess);
        //AuthData authData = new RegisterService.
        String userName = "yomama";
        String password = "ishot";
        String email = "doIlooklike@gmail.com";



        UserData userData = new UserData(userName, password, email);


        userAccess.createUser(userData);


        Assertions.assertFalse(userAccess.users.isEmpty());
        try {
            clearService.clear();
        }catch(Exception ex){
            Assertions.fail();
        }

        Assertions.assertTrue(userAccess.users.isEmpty());

    }
}

package server;
import model.*;
import dataaccess.*;

import java.net.*;

public class ServerFacade {

    private final String serverUrl;

    public ServerFacade(String url) { serverUrl = url;}

    public UserData register(UserData userData){
        var path = "/user";
        return this.makeRequest("POST", path, userData, UserData.class);
    }


    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass) throws DataAccessException{

    }

}

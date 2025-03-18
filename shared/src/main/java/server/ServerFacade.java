package server;
import com.google.gson.Gson;
import model.*;
import exception.DataAccessException;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.*;

public class ServerFacade {

    private final String serverUrl;

    public ServerFacade(String url) { serverUrl = url;}

    public AuthData register(String username, String password, String email) throws DataAccessException {
        var path = "/user";
        UserData user = new UserData(username, password, email);
        return this.makeRequest("POST", path, user, AuthData.class);
    }

    public AuthData login(String username, String password) throws DataAccessException{
        var path = "/session";
        UserData user = new UserData(username, password, null);
        return this.makeRequest("POST", path, user, AuthData.class);
    }

    public void logout(String authToken) throws DataAccessException{
        var path = "/session";
        this.makeRequest("DELETE", path, null, null);
    }


    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass) throws DataAccessException{
        try{
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            writeBody(request, http);
            http.connect();
            //throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        }catch(Exception ex){
            throw new DataAccessException(ex.getMessage());

        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }


//    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, DataAccessException {
//        var status = http.getResponseCode();
//        if (!isSuccessful(status)) {
//            try (InputStream respErr = http.getErrorStream()) {
//                }catch(Exception ex){
//                throw new DataAccessException(ex.getMessage());
//            }
//        }
//    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }

    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }



}

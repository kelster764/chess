package server;
import com.google.gson.Gson;
import model.*;
import exception.DataAccessException;

import javax.xml.crypto.Data;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.*;

public class ServerFacade {

    private final String serverUrl;

    public ServerFacade(String url) { serverUrl = url;}

    public void clear() throws DataAccessException{
        var path = "/db";
        this.makeRequest("DELETE", path, null, null,  null);
    }

    public AuthData register(String username, String password, String email) throws DataAccessException {
        var path = "/user";
        UserData user = new UserData(username, password, email);
        return this.makeRequest("POST", path, user, null,  AuthData.class);
    }

    public AuthData login(String username, String password) throws DataAccessException{
        var path = "/session";
        UserData user = new UserData(username, password, null);
        return this.makeRequest("POST", path, user, null, AuthData.class);
    }

    public void logout(String authToken) throws DataAccessException{
        var path = "/session";
        this.makeRequest("DELETE", path, null, authToken, null);
    }

    public GameData createGame(String gameName, String authToken) throws DataAccessException{
        var path = "/game";
        GameData game = new GameData(0, null, null, gameName, null);
        return this.makeRequest("POST", path, game, authToken, GameData.class);
    }
    
    public GameData[] listGames(String authToken) throws DataAccessException{
        var path = "/game";
        record listGameResponse(GameData[] game){
        }
        var response = this.makeRequest("GET", path, null, authToken, listGameResponse.class);
        return response.game();
    }

    public void joinGame(int gameID, String color, String authToken) throws DataAccessException{
        var path = "/game";
        ColorData colorData = new ColorData(color, gameID);
        this.makeRequest("PUT", path, colorData, authToken, null);
    }


    private <T> T makeRequest(String method, String path, Object request, String header, Class<T> responseClass) throws DataAccessException{
        try{
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            writeHeader(header, http);

            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
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


    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, DataAccessException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            try (InputStream respErr = http.getErrorStream()) {
                if (respErr != null) {
                    throw new DataAccessException("not null");
                }
                }catch(Exception ex){
                throw new DataAccessException(ex.getMessage());
            }
        }
    }

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
    private static void writeHeader(String header, HttpURLConnection http){
        if (header != null) {
            http.addRequestProperty("authorization", header);
        }
    }



}

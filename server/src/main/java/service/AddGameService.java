package service;

import dataaccess.*;
import model.AuthData;
import model.GameData;

public class AddGameService {
    private final AuthDAO authDao;
    private final GameDAO gameDao;

    public AddGameService(AuthDAO authDao, GameDAO gameDao){
        this.authDao = authDao;
        this.gameDao = gameDao;
    }
    public GameData addGame(String authToken, GameData gameName) throws DataAccessException{
        AuthData authData = authDao.getAuth(authToken);
        if(authData == null){
            throw new DataAccessException("Error: unauthorized");
        }
        GameData game = gameDao.createGame(gameName);
        //int gameID = game.gameID();
        return game;
    }
}

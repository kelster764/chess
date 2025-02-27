package service;

import dataaccess.*;
import model.AuthData;
import model.GameData;

import java.util.Collection;

public class ListGameService {
    private final AuthDAO authDao;
    private final GameDAO gameDao;

    public ListGameService(AuthDAO authDao, GameDAO gameDao){
        this.authDao = authDao;
        this.gameDao = gameDao;
    }
    public Collection<GameData> listGame(String authToken) throws DataAccessException{
        AuthData authData = authDao.getAuth(authToken);

        if(authData == null){
            throw new DataAccessException("Error: unauthorized");
        }
        Collection<GameData> games= gameDao.listGames();
        //int gameID = game.gameID();
        return games;
    }
}

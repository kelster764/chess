package service;

import chess.ChessGame;
import dataaccess.*;
import model.*;
import java.util.Collection;

public class JoinGameService {
    private final AuthDAO authDao;
    private final GameDAO gameDao;
    //private final UserDAO userDao;

    public JoinGameService(AuthDAO authDao, GameDAO gameDao){
        this.authDao = authDao;
        this.gameDao = gameDao;
        //this.userDao = userDao;
    }
    public void joinGame(String authToken, ColorData colorData) throws DataAccessException{
        AuthData authData = authDao.getAuth(authToken);
        //UserData user = userDao.
        if(authData == null){
            throw new DataAccessException("Error: unauthorized");
        }

        int gameID = colorData.gameID();
        GameData game = gameDao.getGame(gameID);
        String color = colorData.playerColor();
        if(color == null || (!color.equals("BLACK") && !color.equals("WHITE")) || game == null){
            throw new DataAccessException("Error: bad request");
        }
        if(color.equals("WHITE") && game.whiteUsername()!= null) {
            throw new DataAccessException("Error: already taken");
        }
        if(color.equals("BLACK") && game.blackUsername()!= null) {
            throw new DataAccessException("Error: already taken");
        }

        if(color.equals("WHITE")) {
//            if(game.game() == null){
//                gameDao.updateGame(gameID, authData.username(), game.blackUsername(), game.gameName(), new ChessGame());
//            }
//            else {
                gameDao.updateGame(gameID, authData.username(), game.blackUsername(), game.gameName(), game.game());
//            }
        }
        if(color.equals("BLACK")) {
//            if(game.game() == null){
//                gameDao.updateGame(gameID, game.whiteUsername(), authData.username(), game.gameName(), new ChessGame());
//            }

            gameDao.updateGame(gameID, game.whiteUsername(), authData.username(), game.gameName(), game.game());
        }
//        if(game.game() == null) {
//            gameDao.updateGame(gameID, game.whiteUsername(), game.blackUsername(), game.gameName(), new ChessGame());
//        }
    }
}
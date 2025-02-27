package service;

import dataaccess.GameDAO;
import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import dataaccess.DataAccessException;

public class ClearService {
    private final GameDAO gameDao;
    private final AuthDAO authDao;
    private final UserDAO userDao;
    public ClearService(GameDAO gameDao, AuthDAO authDao, UserDAO userDao){
        this.gameDao = gameDao;
        this.authDao = authDao;
        this.userDao = userDao;
    }
    public void clear() throws DataAccessException {
        gameDao.clearGames();
        authDao.clearAuth();
        userDao.clearUser();
    }
}
package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;

public class LoginService {
    private final AuthDAO authDao;
    private final UserDAO userDao;

    public LoginService(AuthDAO authDao, UserDAO userDao){
        this.authDao = authDao;
        this.userDao = userDao;
    }
    public AuthData loginUser(UserData userData) throws DataAccessException {
        UserData user = userDao.getUser(userData.username());
        if (user == null) {
            throw new DataAccessException("Error: unauthorized");
        }
        if (! user.password().equals(userData.password()) ) {
            throw new DataAccessException("Error: unauthorized");
        }
        AuthData authData = authDao.createAuth(userData.username());
        return authData;
        }
}

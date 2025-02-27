package service;

import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import dataaccess.DataAccessException;
import model.*;

public class RegisterService  {
    private final AuthDAO authDao;
    private final UserDAO userDao;

    public RegisterService(AuthDAO authDao, UserDAO userDao){
        this.authDao = authDao;
        this.userDao = userDao;
    }
    public AuthData registerUser(UserData userData) throws DataAccessException {
        if (userDao.getUser(userData.username()) != null) {
                throw new DataAccessException("Error: already taken") ;
        }
        if (userData.username() == null || userData.password() == null || userData.email() == null){
            throw new DataAccessException("Error: bad request") ;
        }
        //create actual auth data
        userDao.createUser(userData);
        AuthData authData = authDao.createAuth(userData.username());
        return authData;
    }

}

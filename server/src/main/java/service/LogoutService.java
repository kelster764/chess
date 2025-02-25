package service;
import dataaccess.AuthDAO;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;


public class LogoutService {
    private final AuthDAO authDao;
   // private final UserDAO userDao;

    public LogoutService(AuthDAO authDao) {
        this.authDao = authDao;
        //this.userDao = userDao;
    }

    public void LogoutUser(String authtoken) throws DataAccessException{
        AuthData authData = authDao.getAuth(authtoken);
        if(authData == null){
            throw new DataAccessException("Error: unauthorized");
        }
        authDao.deleteAuth(authtoken);
    }

}

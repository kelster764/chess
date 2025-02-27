package service;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import model.AuthData;


public class LogoutService {
    private final AuthDAO authDao;
   // private final UserDAO userDao;

    public LogoutService(AuthDAO authDao) {
        this.authDao = authDao;
        //this.userDao = userDao;
    }

    public void logoutUser(String authtoken) throws DataAccessException{
        AuthData authData = authDao.getAuth(authtoken);
        if(authData == null){
            throw new DataAccessException("Error: unauthorized");
        }
        authDao.deleteAuth(authtoken);
    }

}

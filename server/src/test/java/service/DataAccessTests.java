package service;
import chess.ChessGame;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.DisplayName;
import dataaccess.*;
import model.*;
import org.junit.jupiter.api.*;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;


public class DataAccessTests {
    private static final Logger LOG = LoggerFactory.getLogger(DataAccessTests.class);
    private UserDAO userDAO;


    @BeforeEach
    public void sqlSetUp() {
        try {
            userDAO = new MySqlUserAccess();
        } catch (DataAccessException e) {
            System.out.println(e.getMessage());
            userDAO = new MemoryUserAccess();
        }
    }

    @Test
    @DisplayName("test clearing")
    public void usersql() throws DataAccessException{
        userDAO.createUser(new UserData("cheese", "cheeseisgreat", "cheese@gmail.com"));
    }
}

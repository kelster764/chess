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
    private static final Logger LOG = LoggerFactory.getLogger(UnitTests.class);
    public MySqlUserAccess mysql = new MySqlUserAccess();

    @Test
    @DisplayName("test clearing")
    public void usersql() throws DataAccessException{
        mysql.createUser(new UserData("cheese", "cheeseisgreat", "cheese@gmail.com"));
    }
}

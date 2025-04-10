package server.websocket;


import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import com.google.gson.Gson;
import dataaccess.AuthDAO;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;
import java.util.Objects;

import websocket.commands.ChessMoveCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGame;
import websocket.messages.Notification;
import websocket.commands.UserGameCommand;
import dataaccess.*;

@WebSocket
public class WebSocketHandler {
    private AuthDAO authDao;
    private GameDAO gameDAO;
    public WebSocketHandler(AuthDAO authDAO, GameDAO gameDao){
       this.authDao = authDAO;
       this.gameDAO = gameDao;
    }

    private final ConnectionManager connections = new ConnectionManager();


    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException, DataAccessException {
        UserGameCommand userGameCommand = new Gson().fromJson(message, UserGameCommand.class);
        String authToken = userGameCommand.getAuthToken();
        AuthData authData = authDao.getAuth(authToken);

        GameData gameData = gameDAO.getGame(userGameCommand.getGameID());

        switch (userGameCommand.getCommandType()){
            case CONNECT -> connect(gameData, session, authData);
            case MAKE_MOVE -> move(gameData, session, authData, message);
            case LEAVE -> leave(gameData, session, authData);
            case RESIGN -> resign(gameData, session, authData);
        }
    }
    private void connect(GameData gameData, Session session, AuthData authData) throws IOException {
        try{String userName = authData.username();
            connections.addSessionToGame(gameData.gameID(), session, userName);

            //var messageLoad = new Notification(Notification.Type.LOAD_GAME, "game loading");
//        var messageNotif = new Notification(Notification.Type.NOTIFICATION, String.format("%s has joined", userName));
            var messageLoad = new LoadGame(gameData);
            connections.broadcastToRoot(messageLoad, session);
            Notification notification = new Notification(String.format("%s has joined", userName));
            connections.broadcast(gameData.gameID(), session, notification);
        }catch(Exception ex){
            connections.broadcastToRoot(new ErrorMessage("error:" + ex.getMessage()), session);   }
    }

    private String moveNotification(ChessMove chessMove, String userName){
        ChessPosition startPos = chessMove.getStartPosition();
        ChessPosition endPos = chessMove.getEndPosition();

        int startRow = startPos.getRow();
        int startCol = startPos.getColumn();
        char colCharStart = (char) ('a' + startCol - 1);
        String startPrint = colCharStart + Integer.toString(startRow);

        int endRow = endPos.getRow();
        int endCol = endPos.getColumn();
        char colChar = (char) ('a' + endCol - 1);
        String endPrint = colChar + Integer.toString(endRow);

        String message = String.format("%s moved from %s to %s", userName,  startPrint, endPrint);
        return message;
    }


    private void move(GameData gameData, Session session, AuthData authData,String message) throws IOException {
        try{
            ChessMoveCommand chessMoveCommand = new Gson().fromJson(message, ChessMoveCommand.class);
            //GameData chessCurrentGame = gameDAO.getGame(gameData.gameID());
            //ChessGame chessGame = chessCurrentGame.game();

            String userName = authData.username();

            ChessGame chessGame = gameData.game();
            boolean isTurnWhite = Objects.equals(userName, gameData.whiteUsername()) && chessGame.color == ChessGame.TeamColor.WHITE;
            boolean isTurnBlack = Objects.equals(userName, gameData.blackUsername()) && chessGame.color == ChessGame.TeamColor.BLACK;
            boolean isNotObserver = Objects.equals(userName, gameData.whiteUsername()) || Objects.equals(userName, gameData.blackUsername());
            ChessMove chessMove = chessMoveCommand.getMove();
            if((!chessGame.isGameOver() && (isTurnBlack || isTurnWhite)) || !isNotObserver) {
                if(isNotObserver) {
                    chessGame.makeMove(chessMove);
                    gameDAO.updateGame(gameData.gameID(), gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), chessGame);
                    GameData updatedGame = gameDAO.getGame(gameData.gameID());
                    LoadGame loadGame = new LoadGame(updatedGame);
                    connections.broadcastToRoot(loadGame, session);
                    connections.broadcast(gameData.gameID(), session, loadGame);

                    if(chessGame.isInCheck(chessGame.color)){
                        Notification notification;
                        if(chessGame.color == ChessGame.TeamColor.WHITE){
                            notification = new Notification(String.format("%s is in check", updatedGame.whiteUsername()));
                        }
                        else{
                            notification = new Notification(String.format("%s is in check", updatedGame.blackUsername()));
                        }
                        connections.broadcast(gameData.gameID(), session, notification);
                        connections.broadcastToRoot(notification, session);
                    }
                    if(chessGame.isInCheckmate(chessGame.color)){
                        Notification notification;
                        if(chessGame.color == ChessGame.TeamColor.WHITE){
                            notification = new Notification(String.format("%s is in checkmate", updatedGame.whiteUsername()));
                        }
                        else{
                            notification = new Notification(String.format("%s is in checkmate", updatedGame.blackUsername()));
                        }
                        connections.broadcast(gameData.gameID(), session, notification);
                        connections.broadcastToRoot(notification, session);
                    }
                    else if(chessGame.isInStalemate(chessGame.color)){
                        Notification notification;
                        if(chessGame.color == ChessGame.TeamColor.WHITE){
                            notification = new Notification(String.format("%s is in stalemate", updatedGame.whiteUsername()));
                        }
                        else{
                            notification = new Notification(String.format("%s is in stalemate", updatedGame.blackUsername()));
                        }
                        connections.broadcast(gameData.gameID(), session, notification);
                        connections.broadcastToRoot(notification, session);
                    }
                    String moveMessage = moveNotification(chessMove, userName);
                    Notification notification = new Notification(moveMessage);
                    connections.broadcast(gameData.gameID(), session, notification);
                }
                else{
                    connections.broadcastToRoot(new ErrorMessage("error: observer cannot move"), session);
                }
            }
            else{
                if(chessGame.isGameOver()) {
                    connections.broadcastToRoot(new ErrorMessage("error: game is already over"), session);
                }
                else{
                    connections.broadcastToRoot(new ErrorMessage("error: not your turn"), session);
                }

            }


        }catch(Exception ex){
            connections.broadcastToRoot(new ErrorMessage("error:" + ex.getMessage()), session);
        }
        //server verifies the calidity of the move
        //Game is updated to represent the move. Game is updated in the database.
        //needs to say what move was made

        //var messageNotif = new Notification(Notification.Type.NOTIFICATION, String.format("%s has moved", userName));
        //connections.broadcastResign(gameID, session, messageNotif);

        //If the move results in check, checkmate or stalemate the server sends a Notification message to all clients.

    }

    private void leave(GameData gameData, Session session, AuthData authData) throws IOException {
        //update game to remove root client, but i think this is already done in userfacade??
        try {
            int gameID = gameData.gameID();
            connections.removeSessionFromGame(gameID, session);
            if(Objects.equals(authData.username(), gameData.blackUsername())){
                gameDAO.updateGame(gameID, gameData.whiteUsername(), null, gameData.gameName(), gameData.game());
            }
            else if(Objects.equals(authData.username(), gameData.whiteUsername())){
                gameDAO.updateGame(gameID, null, gameData.blackUsername(), gameData.gameName(), gameData.game());
            }
            Notification notification = new Notification(String.format("%s has left", authData.username()));
            connections.broadcast(gameID, session, notification);
        }catch(Exception ex){
            connections.broadcastToRoot(new ErrorMessage("error:" + ex.getMessage()), session);
        }

        //var messageNotif = new Notification(Notification.Type.NOTIFICATION, String.format("%s has left", userName));
        //connections.broadcastConnect(gameID, session, messageNotif);
    }

    private void resign(GameData gameData, Session session, AuthData authData) throws IOException {
        try{
            ChessGame chessGame = gameData.game();
            int gameID = gameData.gameID();
            String userName = authData.username();
            boolean isNotObserver = Objects.equals(userName, gameData.whiteUsername()) || Objects.equals(userName, gameData.blackUsername());
            if(!chessGame.isGameOver() && isNotObserver) {
                chessGame.resign();
                gameDAO.updateGame(gameID, gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), chessGame);
                Notification notification = new Notification(String.format("%s has resigned", authData.username()));
                connections.broadcast(gameID, session, notification);
                connections.broadcastToRoot(notification, session);
                //connections.removeGame(gameID);
            }
            else{
                connections.broadcastToRoot(new ErrorMessage("error: cannot resign"), session);
            }

        }catch(Exception ex){
            connections.broadcastToRoot(new ErrorMessage("error:" + ex.getMessage()), session);
        }
        //connections.removeGame(gameID);

        //var messageNotif = new Notification(Notification.Type.NOTIFICATION, String.format("%s has resigned", userName));
        //all clients

        //connections.broadcastResign(gameID, session, messageNotif);
        //I need to stop people from playing somehow...
    }

    public void send(Session session, String msg) throws IOException {
        session.getRemote().sendString(msg);
    }



}

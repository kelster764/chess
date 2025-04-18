package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private ChessBoard board;
    public TeamColor color;
    private boolean gameOver;

    public ChessGame() {
        board = new ChessBoard();
        board.resetBoard();
        this.color = TeamColor.WHITE;
        this.gameOver = false;

    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return color;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        color = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    public TeamColor getOppositeColor(TeamColor teamColor){
        if(teamColor == TeamColor.WHITE){
            return TeamColor.BLACK;
        }
        return TeamColor.WHITE;
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        //ChessBoard myboard = this.board;
        //still need to account for checkmate, stalemate, all of that
        ChessPiece piece = board.getPiece(startPosition);
        if(piece == null){
            return null;
        }
        //ArrayList<ChessMove> theMoves= new ArrayList<>();
        Collection<ChessMove> moves = piece.pieceMoves(board, startPosition);
        ArrayList<ChessMove> validMoves = new ArrayList<>();
        //theMoves.addAll(piece.pieceMoves(board, startPosition));
            for(ChessMove move: moves){
                ChessPiece tempPiece = board.getPiece(move.getEndPosition());
                board.addPiece(move.getEndPosition(),piece);
                board.addPiece(startPosition, null);
                if(!isInCheck(piece.getTeamColor())){
                    validMoves.add(move);
                }
                board.addPiece(startPosition, piece);
                board.addPiece(move.getEndPosition(), tempPiece);
            }
        return validMoves;

    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece piece = board.getPiece(move.getStartPosition());
        if(piece == null){
            throw new InvalidMoveException("you can't do that??");
        }
        if(piece.getTeamColor() != this.color){
            throw new InvalidMoveException("yo mama");
        }
        ChessPosition endPosition = move.getEndPosition();
        ChessPiece.PieceType type = piece.getPieceType();
        Collection<ChessMove> validMoves = validMoves(move.getStartPosition());
        if(!validMoves.contains(move)){
            throw new InvalidMoveException("you can't do that??");
        }
        if(move.getPromotionPiece() != null){
            type = move.getPromotionPiece();
        }
        board.addPiece(endPosition, new ChessPiece(piece.getTeamColor(), type));
        board.addPiece(move.getStartPosition(), null);
        this.color = getOppositeColor(piece.getTeamColor());
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        //get moves for oppisite color
        //if the endposition for moves has king at all
        //return true
        for(int row = 1; row <= 8; row++){
            for(int col = 1; col <= 8; col++){
                ChessPosition myPosition =  new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(myPosition);
                //TeamColor oppcolor = getOppositeColor(teamColor));
                Collection<ChessMove> theMoves = new ArrayList<>();
                if (piece != null && piece.getTeamColor() == getOppositeColor(teamColor)) {
                    theMoves.addAll(piece.pieceMoves(board, myPosition));
                }
                for(ChessMove move: theMoves){
                    ChessPosition endPosition = move.getEndPosition();
                    ChessPiece endpiece = board.getPiece(endPosition);
                    if(endpiece!= null &&  endpiece.getTeamColor() == teamColor && endpiece.getPieceType() == ChessPiece.PieceType.KING){
                        return true;
                    }
                }
            }
        }
        return false;
    }



    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */

    public Collection<ChessMove> checkAndStalematehelp(TeamColor teamColor){
        ArrayList<ChessMove> validmoves = new ArrayList<>();
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition myPosition = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(myPosition);
                //TeamColor oppcolor = getOppositeColor(teamColor));
                if (piece != null && piece.getTeamColor() == teamColor) {
                    validmoves.addAll(validMoves(new ChessPosition(row, col)));
                }
            }
        }
        return validmoves;
    }

    public boolean isInCheckmate(TeamColor teamColor) {
        ArrayList<ChessMove> validmoves = new ArrayList<>();
        validmoves.addAll(checkAndStalematehelp(teamColor));
        return isInCheck(teamColor) && validmoves.isEmpty();
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        ArrayList<ChessMove> validmoves = new ArrayList<>();
        validmoves.addAll(checkAndStalematehelp(teamColor));
        return !isInCheck(teamColor) && validmoves.isEmpty();
    }

    public void resign(){
        gameOver = true;
    }

    public Boolean isGameOver(){
        if (isInCheckmate(TeamColor.BLACK)){
            gameOver = true;
        }
        if (isInCheckmate(TeamColor.WHITE)){
            gameOver = true;
        }
        if (isInStalemate(TeamColor.BLACK)){
            gameOver = true;
        }
        if (isInStalemate(TeamColor.WHITE)){
            gameOver = true;
        }
        return gameOver;

    }
    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */

    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {

        return board;
    }
}

package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    private ChessGame.TeamColor pieceColor;
    private ChessPiece.PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> theMoves = new ArrayList<>();
        ChessPiece piece = board.getPiece(myPosition);
        PieceType piecetype = piece.getPieceType();
        ChessGame.TeamColor pieceColor = piece.getTeamColor();
        if(piecetype == PieceType.ROOK){
            theMoves.addAll(RookMove(board, myPosition, pieceColor));
        }
        //return new ArrayList<>();
        //an array list of chessmoves chessmoves being the class that contains the start end and promotion
        return theMoves;
    }
    //ChessPiece piece = board.getPiece(new ChessPosition(row,col));
    //            if(piece != null && piece.getTeamColor() == color){break;}
    //            rookMoves.add(new ChessMove(rookPosition, new ChessPosition(row,col), null));
    //            if(piece != null && piece.getTeamColor() != color){break;}
    //        }
//
//    private int GetValid(ChessBoard board, ChessPosition myposition, int row, int col, ChessGame.TeamColor color){
//        ChessPiece piece = board.getPiece(new ChessPosition(row,col));
//        if(piece != null && piece.getTeamColor() != color){
//            return 1;
//        }
//        if(piece == null) {
//            return new ChessMove(myposition, new ChessPosition(row, col), null);
//        }
//    }
    private Collection<ChessMove> RookMove(ChessBoard board, ChessPosition rookPosition, ChessGame.TeamColor color){
        ArrayList<ChessMove> rookMoves = new ArrayList<>();
        int row = rookPosition.getRow();
        int col = rookPosition.getColumn();
        //going up
        for(row = rookPosition.getRow()+1; row <= 8; row++){
            ChessPiece piece = board.getPiece(new ChessPosition(row,col));
            if(piece != null && piece.getTeamColor() == color){break;}
            rookMoves.add(new ChessMove(rookPosition, new ChessPosition(row,col), null));
            if(piece != null && piece.getTeamColor() != color){break;}
        }
        //going down
        for(row = rookPosition.getRow()-1; row >= 1; row--){
            ChessPiece piece = board.getPiece(new ChessPosition(row,col));
            if(piece != null && piece.getTeamColor() == color){break;}
            rookMoves.add(new ChessMove(rookPosition, new ChessPosition(row,col), null));
            if(piece != null && piece.getTeamColor() != color){break;}
        }
        row = rookPosition.getRow();
        //going right
        for(col = rookPosition.getColumn()+1; col <= 8; col++) {
            ChessPiece piece = board.getPiece(new ChessPosition(row,col));
            if(piece != null && piece.getTeamColor() == color){break;}
            rookMoves.add(new ChessMove(rookPosition, new ChessPosition(row,col), null));
            if(piece != null && piece.getTeamColor() != color){break;}
        }
        for(col = rookPosition.getColumn()-1; col >= 1; col--) {
            ChessPiece piece = board.getPiece(new ChessPosition(row,col));
            if(piece != null && piece.getTeamColor() == color){break;}
            rookMoves.add(new ChessMove(rookPosition, new ChessPosition(row,col), null));
            if(piece != null && piece.getTeamColor() != color){break;}
        }
        return rookMoves;
    }
//    private Collection<ChessMove> BishopMove(ChessBoard board, ChessPosition bishopPosition, ChessGame.TeamColor color){
//        ArrayList<ChessMove> bishopMoves = new ArrayList<>();
//        int row = bishopPosition.getRow();
//        int col = bishopPosition.getColumn();
//        //upright
//        for(col = bishopPosition.getColumn()+1; col <= 8; col++){
//            row += 1;
//            if(row > 8){
//                row = bishopPosition.getRow();
//                break;
//            }
//            ChessPiece piece = board.getPiece(new ChessPosition(row,col));
//            if(piece != null && piece.getTeamColor() == color){break;}
//            bishopMoves.add(new ChessMove(bishopPosition, new ChessPosition(row,col), null));
//            if(piece != null && piece.getTeamColor() != color){break;}
//        }
//
//    }


}

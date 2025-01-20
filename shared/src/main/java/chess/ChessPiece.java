package chess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

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
        if (piecetype == PieceType.ROOK) {
            theMoves.addAll(RookMove(board, myPosition, pieceColor));
        }
        if (piecetype == PieceType.BISHOP) {
            theMoves.addAll(BishopMove(board, myPosition, pieceColor));
        }
        if (piecetype == PieceType.QUEEN) {
            theMoves.addAll(BishopMove(board, myPosition, pieceColor));
            theMoves.addAll(RookMove(board, myPosition, pieceColor));
        }
        if(piecetype == PieceType.KING) {
            theMoves.addAll(KingMove(board, myPosition, pieceColor));
        }
        if(piecetype == PieceType.KNIGHT){
            theMoves.addAll(KnightMove(board, myPosition, pieceColor));
        }
        if(piecetype == PieceType.PAWN){
            theMoves.addAll(PawnMove(board, myPosition, pieceColor));
        }
        //return new ArrayList<>();
        //an array list of chessmoves chessmoves being the class that contains the start end and promotion
        return theMoves;
    }

    private Collection<ChessMove> RookBishHelper(int[][] directions, ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor color){
        ArrayList<ChessMove> moves = new ArrayList<>();
        for (int[] direction : directions) {
            for (int i = 1; i <= 8; i++) {
                int x = direction[0] * i;
                int y = direction[1] * i;
                int row = myPosition.getRow() + x;
                int col = myPosition.getColumn() + y;
                if (row > 8 | col > 8 | row < 1 | col < 1) {
                    break;
                }
                ChessPiece piece = board.getPiece(new ChessPosition(row, col));
                if (piece != null && piece.getTeamColor() == color) {
                    break;
                }
                moves.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
                if (piece != null && piece.getTeamColor() != color) {
                    break;
                }

            }

        }
        return moves;
    }
    private Collection<ChessMove> KingKnighthelper(int[][] directions, ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor color){
        ArrayList<ChessMove> moves = new ArrayList<>();
        for (int[] direction : directions) {
            int x = direction[0];
            int y = direction[1];
            int row = myPosition.getRow() + x;
            int col = myPosition.getColumn() + y;
            if (8 >= row && 8 >= col && row >= 1 && col >= 1) {
                ChessPiece piece = board.getPiece(new ChessPosition(row, col));
                if (piece != null && piece.getTeamColor() != color) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
                }
                if (piece == null) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
                }
            }

        }
        return moves;
    }

    private Collection<ChessMove> RookMove(ChessBoard board, ChessPosition rookPosition, ChessGame.TeamColor color) {
        ArrayList<ChessMove> rookMoves = new ArrayList<>();
        int[][] directions = {
                {1, 0},
                {0, -1}, {0, 1},
                {-1, 0}
        };
        rookMoves.addAll(RookBishHelper(directions, board, rookPosition, color));
        return rookMoves;
    }
    private Collection<ChessMove> BishopMove(ChessBoard board, ChessPosition bishopPosition, ChessGame.TeamColor color) {
        ArrayList<ChessMove> bishopMoves = new ArrayList<>();
        int[][] directions = {
                //upleft, upright
                {-1, 1}, {1, 1},
                //downleft, downright
                {-1, -1}, {1, -1}
        };
        bishopMoves.addAll(RookBishHelper(directions, board, bishopPosition, color));
        return bishopMoves;
    }
    private Collection<ChessMove> KingMove(ChessBoard board, ChessPosition kingPosition, ChessGame.TeamColor color) {
        ArrayList<ChessMove> kingMoves = new ArrayList<>();
        int[][] directions = {
                //upleft, up, upright
                {-1, 1}, {1, 0}, {1, 1},
                //middle
                {0, -1}, {0, 1},
                //downleft, down, downright
                {-1, -1}, {-1, 0}, {1, -1}
        };
        kingMoves.addAll(KingKnighthelper(directions, board, kingPosition, color));
        return kingMoves;
    }
    private Collection<ChessMove> KnightMove(ChessBoard board, ChessPosition knightPosition, ChessGame.TeamColor color) {
        ArrayList<ChessMove> knightMoves = new ArrayList<>();
        int[][] directions = {
                //upleft, upright
                {1, -2}, {2, -1}, {2, 1},{1,2},
                //downleft, downright
                {-2, -1}, {-1, -2}, {-2,1},{-1,2}
        };
        knightMoves.addAll(KingKnighthelper(directions, board, knightPosition, color));
        return knightMoves;
    }
    private Collection<ChessMove> PawnMove(ChessBoard board, ChessPosition pawnPosition, ChessGame.TeamColor color) {
        ArrayList<ChessMove> pawnMoves = new ArrayList<>();
        pawnMoves.addAll(PawnAdvance(board, pawnPosition,color ));

        return pawnMoves;
    }

    private Collection<ChessMove> PawnPromotion(ChessPosition pawnPosition, int row, int col){
        ArrayList<ChessMove> pawnMoves = new ArrayList<>();
        ArrayList <PieceType> pieceTypes = new ArrayList<>(Arrays.asList(PieceType.QUEEN, PieceType.BISHOP,PieceType.ROOK, PieceType.KNIGHT));
        for (PieceType pieceType: pieceTypes){
            pawnMoves.add(new ChessMove(pawnPosition, new ChessPosition(row, col), pieceType));
        }
        return pawnMoves;
    }

    private Collection<ChessMove> PawnCapture(int[][] directions, ChessBoard board, ChessPosition pawnPosition, ChessGame.TeamColor color){
        ArrayList<ChessMove> pawnMoves = new ArrayList<>();
        for(int[] direction: directions){
            int x = direction[0];
            int y = direction[1];
            int row = pawnPosition.getRow() + x;
            int col = pawnPosition.getColumn() + y;

            //advances in middle of board
            if(8 > row && 8 >= col && row > 1 && col >= 1){
                ChessPiece piece = board.getPiece(new ChessPosition(row, col));
                if(piece != null && piece.getTeamColor() != color){
                    pawnMoves.add(new ChessMove(pawnPosition, new ChessPosition(row, col), null));
                }
            }
            //edge capture
            if((8 == row || 1 == row) && 8 >= col && col >= 1){
                ChessPiece piece = board.getPiece(new ChessPosition(row, col));
                if(piece != null && piece.getTeamColor() != color){
                    pawnMoves.addAll(PawnPromotion(pawnPosition, row, col));
                }

            }
        }
        return pawnMoves;
    }

    private Collection<ChessMove> PawnAdvance(ChessBoard board, ChessPosition pawnPosition, ChessGame.TeamColor color) {
        ArrayList<ChessMove> pawnMoves = new ArrayList<>();
        int row = pawnPosition.getRow();
        int col = pawnPosition.getColumn();
        if (color == ChessGame.TeamColor.BLACK) {
            int [][] capturedirect = {
                    {-1,-1}, {-1,1}
            };
            if (row > 2 && board.getPiece(new ChessPosition(row - 1, col)) == null) {
                pawnMoves.add(new ChessMove(pawnPosition, new ChessPosition(row - 1, col), null));
            }
            if (row == 7 && board.getPiece(new ChessPosition(row - 1, col)) == null && board.getPiece(new ChessPosition(row - 2, col)) == null) {
                pawnMoves.add(new ChessMove(pawnPosition, new ChessPosition(row - 2, col), null));
            }
            if (row == 2 && board.getPiece(new ChessPosition(row - 1, col)) == null) {
                pawnMoves.addAll(PawnPromotion(pawnPosition, row-1, col));
            }
            pawnMoves.addAll(PawnCapture(capturedirect, board, pawnPosition, color));
        }
        if (color == ChessGame.TeamColor.WHITE) {
            int [][] capturedirect = {
                    {1,-1}, {1,1}
            };
            if (row < 7 && board.getPiece(new ChessPosition(row + 1, col)) == null) {
                pawnMoves.add(new ChessMove(pawnPosition, new ChessPosition(row + 1, col), null));
            }
            if (row == 2 && board.getPiece(new ChessPosition(row + 1, col)) == null && board.getPiece(new ChessPosition(row + 2, col)) == null) {
                pawnMoves.add(new ChessMove(pawnPosition, new ChessPosition(row + 2, col), null));
            }
            if (row == 7 && board.getPiece(new ChessPosition(row + 1, col)) == null) {
                pawnMoves.addAll(PawnPromotion(pawnPosition, row+1, col));
            }
            pawnMoves.addAll(PawnCapture(capturedirect, board, pawnPosition, color));
        }
        return pawnMoves;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public String toString() {
        return "ChessPiece{" +
                "pieceColor=" + pieceColor +
                ", type=" + type +
                '}';
    }
}


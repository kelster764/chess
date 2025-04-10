package ui;
import chess.*;
import com.google.gson.Gson;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static ui.EscapeSequences.*;


public class ChessPrint {
    private static final int BOARD_SIZE_IN_SQUARES = 10;
    private static final int SQUARE_SIZE_IN_PADDED_CHARS = 3;
    private static final int LINE_WIDTH_IN_PADDED_CHARS = 1;

    public static final String KING = " K ";
    public static final String QUEEN = " Q ";
    public static final String BISHOP = " B ";
    public static final String KNIGHT = " N ";
    public static final String ROOK = " R ";
    public static final String PAWN = " P ";
    public static final String EMPTY = " \u2003 ";
    public static String color = "WHITE";
    public static ChessGame chess = new ChessGame();
    public static ChessBoard chessBoard = new ChessBoard();
    //public static Collection<ChessMove> validMoves;
    public static Collection<ChessPosition> highLightPositions = new ArrayList<>();


    public static void main(String[] args) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        out.print(ERASE_SCREEN);
        highLightPositions = new ArrayList<>();

        if (args[0].equalsIgnoreCase("black")){
            color = "BLACK";
        }
        else{
            color = "WHITE";
        }
        String chessJson = args[1];
        chess = new Gson().fromJson(chessJson, ChessGame.class);
        //ChessGame chess = new ChessGame();
        chessBoard = chess.getBoard();



        Collection<ChessMove> validMoves;
        //Collection<ChessPosition> highLight = new ArrayList<>();
        if(args.length >2 ){
            String position = args[2];
            int startCol = position.charAt(0) - 'a' + 1;
            int startRow = Character.getNumericValue(position.charAt(1));

            ChessPosition chessPosition = new ChessPosition(startRow, startCol);
            validMoves = chess.validMoves(chessPosition);
            for(ChessMove move : validMoves){
                ChessPosition endPosition = move.getEndPosition();
                highLightPositions.add(endPosition);
            }

        }


        drawHeaders(out);

        drawChessBoard(out);

        drawHeaders(out);
    }

    private static ChessPosition convertToPosition(int row, int col){
        ChessPosition chessPosition = new ChessPosition(row, col);
        return chessPosition;
    }


    private static void drawHeaders(PrintStream out) {
        String[] headers = {"   "," a ", " b ", " c ", " d ", " e ", " f ", " g ", " h ", "   "};
        if(Objects.equals(color, "BLACK")){
            Collections.reverse(Arrays.asList(headers));
        }
        for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {
            drawHeader(out, headers[boardCol]);
        }
        setDef(out);
        out.println();
    }
    private static void drawHeader(PrintStream out, String headerText) {
        int prefixLength = SQUARE_SIZE_IN_PADDED_CHARS / 2;
        int suffixLength = SQUARE_SIZE_IN_PADDED_CHARS - prefixLength - 1;
        printHeaderText(out, headerText);
    }

    private static void printHeaderText(PrintStream out, String player) {
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_GREEN);

        out.print(player);

        setBlack(out);
    }

    private static void drawChessBoard(PrintStream out) {
        int colStart = Objects.equals(color, "WHITE") ? 8 : 1;
        int colEnd = Objects.equals(color, "WHITE") ? 0 : 9;
        int colStep = Objects.equals(color, "WHITE")  ? -1 : 1;

        for (int startRow = colStart; startRow != colEnd; startRow+= colStep) {
            drawRowOfSquares(out, startRow, chessBoard);
        }
    }

    private static void drawRowOfSquares(PrintStream out, int boardRow, ChessBoard chessBoard) {
        int colStart = Objects.equals(color, "WHITE") ? 1 : 8;
        int colEnd = Objects.equals(color, "WHITE") ? 8 : 1;
        int colStep = Objects.equals(color, "WHITE")  ? 1 : -1;

        String blankPiece = "   ";
        String rowLabel = String.format(" "+String.valueOf(String.valueOf(boardRow)+ " "));
        setMagenta(out);
        printPlayer(out, rowLabel, ChessGame.TeamColor.BLACK);
        int startSquare = boardRow % 2;
        for (int boardCol = colStart; boardCol != colEnd + colStep; boardCol+= colStep) {
            ChessPosition chessPosition = new ChessPosition(boardRow, boardCol);
            ChessPiece chessPiece = chessBoard.getPiece(chessPosition);
            //String rowLabel = String.format(" "+String.valueOf(String.valueOf(boardRow)+ " "));

            if (startSquare % 2 == 1 &&  Objects.equals(color, "WHITE")){
                    setGray(out);
                }
                else if (startSquare % 2 == 0 &&  Objects.equals(color, "BLACK")){
                    setGray(out);
                }
                else{
                    setWhite(out);
                }

             if(highLightPositions.contains(chessPosition)){
                 setYellow(out);
             }
            if(chessPiece != null) {
                printPlayer(out, chessPiece.getPieceType().toString(), chessPiece.getTeamColor());
            }
            else{
                printPlayer(out, blankPiece, ChessGame.TeamColor.BLACK);
            }

            startSquare++;
        }
        setMagenta(out);
        printPlayer(out, rowLabel, ChessGame.TeamColor.BLACK);
        setDef(out);

        out.println();
    }
    private static void printPlayer(PrintStream out, String player, ChessGame.TeamColor teamColor) {

        Boolean isWhite = teamColor == ChessGame.TeamColor.WHITE;
        if(isWhite){
            out.print(SET_TEXT_COLOR_BLUE);
        }else {
            out.print(SET_TEXT_COLOR_BLACK);
        }
        String outPlayer = player;
        if(Objects.equals(player, "KING")){
            outPlayer = KING;
        }
        else if(Objects.equals(player, "QUEEN")){
            outPlayer = QUEEN;
        }
        else if(Objects.equals(player, "BISHOP")){
            outPlayer = BISHOP;
        }
        else if(Objects.equals(player, "KNIGHT")){
            outPlayer = KNIGHT;
        }
        else if(Objects.equals(player, "ROOK")){
            outPlayer = ROOK;
        }
        else if(Objects.equals(player, "PAWN")){
            outPlayer = PAWN;
        }
        out.print(outPlayer);
    }


    private static void drawHorizontalLine(PrintStream out) {

        int boardSizeInSpaces = 14;


        for (int lineRow = 0; lineRow < LINE_WIDTH_IN_PADDED_CHARS; ++lineRow) {
            setWhite(out);
            out.print(EMPTY.repeat(boardSizeInSpaces));
            setDef(out);
            out.println();
        }
    }



    private static void setWhite(PrintStream out) {
        out.print(SET_BG_COLOR_WHITE);
    }

    private static void setMagenta(PrintStream out) {
        out.print(SET_BG_COLOR_MAGENTA);
        out.print(SET_TEXT_COLOR_WHITE);
    }
    private static void setGray(PrintStream out) {
        out.print(SET_BG_COLOR_LIGHT_GREY);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private static void setYellow(PrintStream out){
        out.print(SET_BG_COLOR_YELLOW);
    }

    private static void setRed(PrintStream out) {
        out.print(SET_BG_COLOR_RED);
        out.print(SET_TEXT_COLOR_RED);
    }


    private static void setBlack(PrintStream out) {
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_BLACK);
    }

    private static void setDef(PrintStream out) {
        out.print(RESET_BG_COLOR);
        out.print(RESET_TEXT_COLOR);
    }


}

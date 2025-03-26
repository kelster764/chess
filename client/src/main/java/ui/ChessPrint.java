package ui;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;

import static ui.EscapeSequences.*;


public class ChessPrint {
    private static final int BOARD_SIZE_IN_SQUARES = 10;
    private static final int SQUARE_SIZE_IN_PADDED_CHARS = 3;
    private static final int LINE_WIDTH_IN_PADDED_CHARS = 1;

    public static final String WHITE_KING = "♔";
    public static final String WHITE_QUEEN = "♕";
    public static final String WHITE_BISHOP = "♗";
    public static final String WHITE_KNIGHT = "♘";
    public static final String WHITE_ROOK = "♖";
    public static final String WHITE_PAWN = "♙";
    public static final String BLACK_KING = " K ";
    public static final String BLACK_QUEEN = " Q ";
    public static final String BLACK_BISHOP = " B ";
    public static final String BLACK_KNIGHT = " N ";
    public static final String BLACK_ROOK = " R ";
    public static final String BLACK_PAWN = " P ";
    public static final String EMPTY = " \u2003 ";
    public static String color = "WHITE";


    public static void main(String[] args) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        out.print(ERASE_SCREEN);

        if(args.length > 0){
            if (args[0].equalsIgnoreCase("black")){
                color = "BLACK";
            }
        }

        drawHeaders(out);

        drawChessBoard(out);

        drawHeaders(out);
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
            drawRowOfSquares(out, startRow);
        }
    }

    private static void drawRowOfSquares(PrintStream out, int boardRow) {
        int colStart = Objects.equals(color, "WHITE") ? 1 : 8;
        int colEnd = Objects.equals(color, "WHITE") ? 8 : 1;
        int colStep = Objects.equals(color, "WHITE")  ? 1 : -1;

        String rowLabel = String.format(" "+String.valueOf(String.valueOf(boardRow)+ " "));
        String[] blackPieces = {rowLabel,BLACK_ROOK,BLACK_KNIGHT,BLACK_BISHOP,BLACK_QUEEN,BLACK_KING,
                BLACK_BISHOP, BLACK_KNIGHT, BLACK_ROOK, rowLabel};
        String[] pawnPieces = {rowLabel,BLACK_PAWN,BLACK_PAWN,BLACK_PAWN,BLACK_PAWN,BLACK_PAWN,
                BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, rowLabel};
        String[] blankPieces = {rowLabel,"   ","   ","   ","   ","   ",
                "   ", "   ", "   ", rowLabel};
                setMagenta(out);
                printPlayer(out, blackPieces[0], false);
                int startSquare = boardRow % 2;
            for (int boardCol = colStart; boardCol != colEnd + colStep; boardCol+= colStep) {

                if (startSquare % 2 == 1 &&  Objects.equals(color, "WHITE")){
                    setGray(out);
                }
                else if (startSquare % 2 == 0 &&  Objects.equals(color, "BLACK")){
                    setGray(out);
                }
                else{
                    setWhite(out);
                }
                if(boardRow == 7){
                    printPlayer(out, pawnPieces[boardCol], false);
                }
                else if(boardRow == 8){
                    printPlayer(out, blackPieces[boardCol], false);
                }
                else if(boardRow == 1){
                    printPlayer(out, blackPieces[boardCol], true);
                }
                else if(boardRow == 2){
                    printPlayer(out, pawnPieces[boardCol], true);
                }
                else{
                    printPlayer(out, blankPieces[boardCol], false);
                }

                startSquare++;
                }
        setMagenta(out);
        printPlayer(out, blackPieces[BOARD_SIZE_IN_SQUARES - 1], false);
            setDef(out);

            out.println();
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

    private static void printPlayer(PrintStream out, String player, boolean isWhite) {
        if(isWhite){
            out.print(SET_TEXT_COLOR_BLUE);
        }else {
            out.print(SET_TEXT_COLOR_BLACK);
        }
        out.print(player);
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

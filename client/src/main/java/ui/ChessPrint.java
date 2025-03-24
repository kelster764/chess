package ui;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static ui.EscapeSequences.*;


public class ChessPrint {
    private static final int BOARD_SIZE_IN_SQUARES = 10;
    private static final int SQUARE_SIZE_IN_PADDED_CHARS = 3;
    private static final int LINE_WIDTH_IN_PADDED_CHARS = 1;

    public static final String WHITE_KING = " ♔ ";
    public static final String WHITE_QUEEN = " ♕ ";
    public static final String WHITE_BISHOP = " ♗ ";
    public static final String WHITE_KNIGHT = " ♘ ";
    public static final String WHITE_ROOK = " ♖ ";
    public static final String WHITE_PAWN = " ♙ ";
    public static final String BLACK_KING = " ♚ ";
    public static final String BLACK_QUEEN = " ♛ ";
    public static final String BLACK_BISHOP = " ♝ ";
    public static final String BLACK_KNIGHT = " ♞ ";
    public static final String BLACK_ROOK = " ♜ ";
    public static final String BLACK_PAWN = " ♟ ";
    public static final String EMPTY = " \u2003 ";


    public static void main(String[] args) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        out.print(ERASE_SCREEN);

        drawHeaders(out);

        drawChessBoard(out);

        drawHeaders(out);

        //out.print(SET_BG_COLOR_BLACK);
        //out.print(SET_TEXT_COLOR_WHITE);
    }


    private static void drawHeaders(PrintStream out) {
        //setGray(out);

        String[] headers = {" ","a", "b", "c", "d", "e", "f", "g", "h", " "};
        for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {
            drawHeader(out, headers[boardCol]);
        }
    }
    private static void drawHeader(PrintStream out, String headerText) {
        int prefixLength = SQUARE_SIZE_IN_PADDED_CHARS / 2;
        int suffixLength = SQUARE_SIZE_IN_PADDED_CHARS - prefixLength - 1;

        out.print(EMPTY.repeat(prefixLength));
        printHeaderText(out, headerText);
        out.print(EMPTY.repeat(suffixLength));
    }

    private static void printHeaderText(PrintStream out, String player) {
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_GREEN);

        out.print(player);

        setBlack(out);
    }

    private static void drawChessBoard(PrintStream out) {

        for (int boardRow = 1; boardRow < BOARD_SIZE_IN_SQUARES-1; ++boardRow) {

            drawRowOfSquares(out, boardRow);

//            if (boardRow < BOARD_SIZE_IN_SQUARES - 1) {
//                // Draw horizontal row separator.
//                drawHorizontalLine(out);
//                setBlack(out);
//            }
        }
    }

    private static void drawRowOfSquares(PrintStream out, int boardRow) {
        String[] blackPieces = {String.valueOf(boardRow),BLACK_ROOK,BLACK_BISHOP,BLACK_KNIGHT,BLACK_QUEEN,BLACK_KING,
                BLACK_BISHOP, BLACK_KNIGHT, BLACK_ROOK, String.valueOf(boardRow)};
            for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {
                setWhite(out);
                    int prefixLength = SQUARE_SIZE_IN_PADDED_CHARS / 2;
                    int suffixLength = SQUARE_SIZE_IN_PADDED_CHARS - prefixLength - 1;

                    out.print(EMPTY.repeat(prefixLength));
                    printPlayer(out, blackPieces[boardCol]);
                    out.print(EMPTY.repeat(suffixLength));
                }

                setBlack(out);

            out.println();
    }

//    private static void drawRowOfSquares(PrintStream out, int boardRow) {
//        String[] blackPieces = {String.valueOf(boardRow),BLACK_ROOK,BLACK_BISHOP,BLACK_KNIGHT,BLACK_QUEEN,BLACK_KING,
//        BLACK_BISHOP, BLACK_KNIGHT, BLACK_ROOK, String.valueOf(boardRow)};
//
//        for (int squareRow = 0; squareRow < SQUARE_SIZE_IN_PADDED_CHARS; ++squareRow) {
//            for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {
//                setWhite(out);
//
//                if (squareRow == SQUARE_SIZE_IN_PADDED_CHARS / 2) {
//                    int prefixLength = SQUARE_SIZE_IN_PADDED_CHARS / 2;
//                    int suffixLength = SQUARE_SIZE_IN_PADDED_CHARS - prefixLength - 1;
//
//                    out.print(EMPTY.repeat(prefixLength));
//                    printPlayer(out, blackPieces[boardCol]);
//                    out.print(EMPTY.repeat(suffixLength));
//                }
//                else {
//                    out.print(EMPTY.repeat(SQUARE_SIZE_IN_PADDED_CHARS));
//                }
//
//                if (boardCol < BOARD_SIZE_IN_SQUARES - 1) {
//                    // Draw vertical column separator.
//                    setRed(out);
//                    out.print(EMPTY.repeat(LINE_WIDTH_IN_PADDED_CHARS));
//                }
//
//                setBlack(out);
//            }
//
//            out.println();
//        }
//    }
    private static void printPlayer(PrintStream out, String player) {
        out.print(SET_BG_COLOR_WHITE);
        out.print(SET_TEXT_COLOR_BLACK);

        out.print(player);

        setWhite(out);
    }

    private static void setWhite(PrintStream out) {
        out.print(SET_BG_COLOR_WHITE);
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


}

package aoc.day14;

import aoc.ChristmasAssert;
import aoc.FileUtil;

public class ParabolicReflector {

    private static boolean log = false;

    private static final char SQUARE_STONE = '#';
    private static final char ROLLING_STONE = 'O';
    private static final char EMPTY = '.';

    private static int height;
    private static int width;
    private static char[][] rockField;

    public static void main(String[] args) {
        rockField = FileUtil.readToGrid("day14");
        height = rockField.length;
        width = rockField[0].length;

        for (int i = 0; i < 1000000000; i++) {
            tiltNorth();
            tiltWest();
            tiltSouth();
            tiltEast();
            if (i % 10000 == 0) System.out.printf("cycle %,d - TOTAL: %d%n", i, analyze());
        }
        ChristmasAssert.test(analyze(), 106689L);
    }

    private static void tiltNorth() {

        for (int col = 0; col < width; col++) {
            var start = 0;
            int rollingRocks = 0;
            for (int row = 0; row < height; row++) {
                var curr = rockField[row][col];
                if (curr == ROLLING_STONE) {
                    rollingRocks++;
                }
                if (curr == SQUARE_STONE || row == height - 1) {
                    for (int i = start; i <= row; i++) {
                        if (rockField[i][col] == SQUARE_STONE) continue;
                        if (rollingRocks > 0) {
                            rockField[i][col] = ROLLING_STONE;
                            rollingRocks--;
                        } else {
                            rockField[i][col] = EMPTY;
                        }
                    }
                    start = row;
                }
            }
        }
    }

    private static void tiltSouth() {

        for (int col = 0; col < width; col++) {
            var start = 0;
            int emptyNodes = 0;
            for (int row = 0; row < height; row++) {
                var curr = rockField[row][col];
                if (curr == EMPTY) {
                    emptyNodes++;
                }
                if (curr == SQUARE_STONE || row == height - 1) {
                    for (int i = start; i <= row; i++) {
                        if (rockField[i][col] == SQUARE_STONE) continue;
                        if (emptyNodes > 0) {
                            rockField[i][col] = EMPTY;
                            emptyNodes--;
                        } else {
                            rockField[i][col] = ROLLING_STONE;
                        }
                    }
                    start = row;
                }
            }
        }
    }

    private static void tiltWest() {
        for (int row = 0; row < height; row++) {
            var start = 0;
            int rollingRocks = 0;
            for (int col = 0; col < width; col++) {
                var curr = rockField[row][col];
                if (curr == ROLLING_STONE) {
                    rollingRocks++;
                }
                if (curr == SQUARE_STONE || col == width - 1) {
                    for (int i = start; i <= col; i++) {
                        if (rockField[row][i] == SQUARE_STONE) continue;
                        if (rollingRocks > 0) {
                            rockField[row][i] = ROLLING_STONE;
                            rollingRocks--;
                        } else {
                            rockField[row][i] = EMPTY;
                        }
                    }
                    start = col;
                }
            }
        }
    }

    private static void tiltEast() {
        for (int row = 0; row < height; row++) {
            var start = 0;
            int empties = 0;
            for (int col = 0; col < width; col++) {
                var curr = rockField[row][col];
                if (curr == EMPTY) {
                    empties++;
                }
                if (curr == SQUARE_STONE || col == width - 1) {
                    for (int i = start; i <= col; i++) {
                        if (rockField[row][i] == SQUARE_STONE) continue;
                        if (empties > 0) {
                            rockField[row][i] = EMPTY;
                            empties--;
                        } else {
                            rockField[row][i] = ROLLING_STONE;
                        }
                    }
                    start = col;
                }
            }
        }
    }


    private static long analyze() {
        if (log) System.out.println();

        int rollingStones = 0;
        int squareStones = 0;
        int emptySpots = 0;
        long total = 0L;
        for (int i = 0; i < rockField.length; i++) {
            int rs = 0;
            for (int j = 0; j < rockField[0].length; j++) {
                char c = rockField[i][j];
                if (c == 'O') rs++;
                if (c == '#') squareStones++;
                if (c == '.') emptySpots++;
                if (log) System.out.print(c + " ");
            }
            int multiplier = rockField.length - i;
            int rowResult = rs * multiplier;
            if (log) System.out.printf(" - %3d * %3d = %d%n", multiplier, rs, rowResult);
            total += rowResult;
            rollingStones += rs;
        }
        if (log) System.out.println("TOTAL         : " + total);
        if (log) System.out.println("Rolling stones: " + rollingStones);
        if (log) System.out.println("Square stones : " + squareStones);
        if (log) System.out.println("Empty spots   : " + emptySpots);
        if (log) System.out.println();
        return total;
    }

}

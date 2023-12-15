package aoc.day14;

import aoc.FileUtil;

public class ParabolicReflector {


    public static final char SQUARE_STONE = '#';
    public static final char ROLLING_STONE = 'O';
    public static final char EMPTY = '.';

    public static void main(String[] args) {

        var input = FileUtil.readToGrid("day14");
        tiltNorth(input);
        analyze(input);


    }

    private static void tiltNorth(char[][] input) {
        int height = input.length;
        int width = input[0].length;

        for (int col = 0; col < width; col++) {
            var start = 0;
            int rollingRocks = 0;
            for (int row = 0; row < height; row++) {
                var curr = input[row][col];
                if (curr == ROLLING_STONE) {
                    rollingRocks++;
                }
                if (curr == SQUARE_STONE || row == height-1) {
                    for (int i = start; i <= row; i++) {
                        if (input[i][col] == SQUARE_STONE) continue;
                        if (rollingRocks > 0) {
                            input[i][col] = ROLLING_STONE;
                            rollingRocks--;
                        } else {
                            input[i][col] = EMPTY;
                        }
                    }
                    start = row;
                }
            }
        }
    }

    private static void analyze(char[][] input) {
        System.out.println();

        int rollingStones = 0;
        int squareStones = 0;
        int emptySpots = 0;
        int total = 0;
        for (int i = 0; i < input.length; i++) {
            int rs = 0;
            for (int j = 0; j < input[0].length; j++) {
                char c = input[i][j];
                if (c == 'O') rs++;
                if (c == '#') squareStones++;
                if (c == '.') emptySpots++;
                System.out.print(c + " ");
            }
            int multiplier = input.length - i;
            int rowResult = rs * multiplier;
            System.out.printf(" - %3d * %3d = %d%n", multiplier, rs, rowResult);
            total += rowResult;
            rollingStones += rs;
        }
        System.out.println("TOTAL         : " + total);
        System.out.println("Rolling stones: " + rollingStones);
        System.out.println("Square stones : " + squareStones);
        System.out.println("Empty spots   : " + emptySpots);
        System.out.println();
    }

}

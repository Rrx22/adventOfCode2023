package aoc.day16;

import aoc.ChristmasAssert;
import aoc.FileUtil;

import static aoc.day16.MirrorContraption.Direction.*;

public class MirrorContraption {

    Tile[][] grid;

    void main() {
        mapGrid();
        energizeTiles(0, 0, RIGHT);
        ChristmasAssert.test(analyzeGrid(false), 6816L);

        findAndSetToOptimalConfiguration();
        ChristmasAssert.test(analyzeGrid(true), 8163L);
    }

    private void findAndSetToOptimalConfiguration() {
        int maxX = grid.length;
        int maxY = grid[0].length;

        var optimal = new Configuration(0, 0, 0L, UP);

        for (int i = 0; i < maxX; i++) {
            var config= changeAndAnalyzeConfiguration(i, 0, RIGHT);
            if (config.result > optimal.result) {
                optimal = config;
            }
        }
        for (int i = 0; i < maxX; i++) {
            var config = changeAndAnalyzeConfiguration(i, grid[0].length - 1, LEFT);
            if (config.result > optimal.result) {
                optimal = config;
            }
        }
        for (int i = 0; i < maxY; i++) {
            var config = changeAndAnalyzeConfiguration(0, i, DOWN);
            if (config.result > optimal.result) {
                optimal = config;
            }
        }
        for (int i = 0; i < maxY; i++) {
            var config = changeAndAnalyzeConfiguration(grid.length - 1, i, UP);
            if (config.result > optimal.result) {
                optimal = config;
            }
        }

        System.out.println(STR."RESULT: \{optimal.direction} [\{optimal.x}, \{optimal.y}] \{optimal.result}");
        grid = mapGrid();
        energizeTiles(optimal.x, optimal.y, optimal.direction);
    }

    Configuration changeAndAnalyzeConfiguration(int x, int y, Direction right) {
        grid = mapGrid();
        energizeTiles(x, y, right);
        long result = analyzeGrid(false);
        return new Configuration(x, y, result, right);
    }

    private void energizeTiles(int x, int y, Direction direction) {
        while (!outOfBounds(x, y)) {
            var tile = grid[x][y];
            var wasAlreadyEnergized = tile.isEnergized;
            tile.isEnergized = true;

            if ('/' == tile.type) { // Mirror type
                direction = switch (direction) {
                    case UP -> RIGHT;
                    case DOWN -> LEFT;
                    case RIGHT -> UP;
                    case LEFT -> DOWN;
                };
            } else if ('\\' == tile.type) { // Mirror type
                direction = switch (direction) {
                    case UP -> LEFT;
                    case DOWN -> RIGHT;
                    case RIGHT -> DOWN;
                    case LEFT -> UP;
                };
            } else if ('-' == tile.type) { // splitter type
                if (direction == UP || direction == DOWN) {
                    if (!wasAlreadyEnergized) {
                        energizeTiles(x, y, LEFT);
                        energizeTiles(x, y, RIGHT);
                    }
                    break;
                }
            } else if ('|' == tile.type) { // splitter type
                if (direction == LEFT || direction == RIGHT) {
                    if (!wasAlreadyEnergized) {
                        energizeTiles(x, y, UP);
                        energizeTiles(x, y, DOWN);
                    }
                    break;
                }
            }
            x = x + direction.x;
            y = y + direction.y;
        }
    }

    private boolean outOfBounds(int x, int y) {
        return x < 0 || x >= grid.length || y < 0 || y >= grid[0].length;
    }

    Tile[][] mapGrid() {
        var input = FileUtil.readFile("day16");
        grid = new Tile[input.size()][input.getFirst().length()];
        for (int i = 0; i < input.size(); i++) {
            var charArr = input.get(i).toCharArray();
            for (int j = 0; j < charArr.length; j++) {
                char c = charArr[j];
                grid[i][j] = new Tile(c);
            }
        }
        return grid;
    }

    private long analyzeGrid(boolean print) {
        if (print) System.out.println("\n");
        long energizedGrids = 0L;
        for (int i = 0; i < grid.length; i++) {
            Tile[] tiles = grid[i];
            if (print) System.out.printf("%3d  ", i);
            for (int j = 0; j < grid[0].length; j++) {
                Tile tile = tiles[j];
                if (tile.isEnergized) {
                    energizedGrids++;
                }
                if (print) System.out.print(STR."\{tile.isEnergized ? '#' : tile.type} ");
            }
            if (print) System.out.println();
        }

        if (print) System.out.println();
        return energizedGrids;
    }

    record Configuration(int x, int y, long result, Direction direction) { }

    static class Tile {
        public final char type;
        public boolean isEnergized;

        public Tile(char type) {
            this.type = type;
            isEnergized = false;
        }
    }

    enum Direction {
        UP(-1, 0),
        DOWN(1, 0),
        LEFT(0, -1),
        RIGHT(0, 1);

        public final int x;
        public final int y;

        Direction(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }



}

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

        identifyAndSetOptimalConfiguration();
        ChristmasAssert.test(analyzeGrid(true), 8163L);
    }

    void identifyAndSetOptimalConfiguration() {
        var optimal = new Configuration(0, 0, 0L, UP);
        for (var i = 0; i < grid.length; i++) {
            optimal = compare(optimal, tryNewConfiguration(UP, grid.length - 1, i));
            optimal = compare(optimal, tryNewConfiguration(DOWN, 0, i));
            optimal = compare(optimal, tryNewConfiguration(LEFT, i, grid[0].length - 1));
            optimal = compare(optimal, tryNewConfiguration(RIGHT, i, 0));
        }
        grid = mapGrid();
        energizeTiles(optimal.x, optimal.y, optimal.direction);
    }

    Configuration compare(Configuration optimal, Configuration newConfig) {
        return (newConfig.result > optimal.result) ? newConfig : optimal;
    }

    Configuration tryNewConfiguration(Direction right, int x, int y) {
        grid = mapGrid();
        energizeTiles(x, y, right);
        var result = analyzeGrid(false);
        return new Configuration(x, y, result, right);
    }

    void energizeTiles(int x, int y, Direction direction) {
        while (x >= 0 && x < grid.length && y >= 0 && y < grid[0].length) {
            var tile = grid[x][y];
            var wasAlreadyEnergized = tile.isEnergized; // if a splitter splits & is already energized, we do not need to traverse it again
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

    Tile[][] mapGrid() {
        var input = FileUtil.readFile("day16");
        grid = new Tile[input.size()][input.getFirst().length()];
        for (int i = 0; i < input.size(); i++) {
            var charArr = input.get(i).toCharArray();
            for (int j = 0; j < charArr.length; j++) {
                var c = charArr[j];
                grid[i][j] = new Tile(c);
            }
        }
        return grid;
    }

    long analyzeGrid(boolean print) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        var energizedGrids = 0L;
        for (var i = 0; i < grid.length; i++) {
            var tiles = grid[i];
            sb.append(String.format("%3d  ", i));
            for (var j = 0; j < grid[0].length; j++) {
                var tile = tiles[j];
                if (tile.isEnergized) {
                    energizedGrids++;
                }
                sb.append(STR."\{tile.isEnergized ? '#' : tile.type} ");
            }
            sb.append("\n");
        }
        if (print) System.out.println(sb);
        return energizedGrids;
    }

    record Configuration(int x, int y, long result, Direction direction) { }

    static class Tile {
        public final char type;
        public boolean isEnergized;
        public Tile(char type) {
            this.type = type;
        }
    }

    enum Direction {
        UP(-1, 0),
        DOWN(1, 0),
        LEFT(0, -1),
        RIGHT(0, 1);
        public final int x, y;
        Direction(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
}

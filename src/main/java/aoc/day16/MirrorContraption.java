package aoc.day16;

import aoc.ChristmasAssert;
import aoc.FileUtil;

import static aoc.day16.MirrorContraption.Direction.*;

public class MirrorContraption {

    Tile[][] grid;

    void main() {
        mapGrid();
        energizeTiles(RIGHT, 0, 0);
        ChristmasAssert.test(analyzeGrid(), 6816L);

        identifyAndSetOptimalConfiguration();
        ChristmasAssert.test(analyzeGrid(), 8163L);
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
        energizeTiles(optimal.direction, optimal.x, optimal.y);
    }

    Configuration compare(Configuration optimal, Configuration newConfig) {
        return (newConfig.result > optimal.result) ? newConfig : optimal;
    }

    Configuration tryNewConfiguration(Direction direction, int x, int y) {
        grid = mapGrid();
        energizeTiles(direction, x, y);
        var result = analyzeGrid();
        return new Configuration(x, y, result, direction);
    }

    void energizeTiles(Direction direction, int x, int y) {
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
            } else if ('-' == tile.type && (direction == UP || direction == DOWN)) { // splitter type
                if (!wasAlreadyEnergized) {
                    energizeTiles(LEFT, x, y);
                    energizeTiles(RIGHT, x, y);
                }
                break;
            } else if ('|' == tile.type && (direction == LEFT || direction == RIGHT)) {
                if (!wasAlreadyEnergized) {
                    energizeTiles(UP, x, y);
                    energizeTiles(DOWN, x, y);
                }
                break;

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

    long analyzeGrid() {
        var energizedGrids = 0L;
        for (var tiles : grid) {
            for (var j = 0; j < grid[0].length; j++) {
                var tile = tiles[j];
                if (tile.isEnergized) {
                    energizedGrids++;
                }
            }
        }
        return energizedGrids;
    }

    record Configuration(int x, int y, long result, Direction direction) {
    }

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

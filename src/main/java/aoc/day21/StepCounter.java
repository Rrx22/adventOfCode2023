package aoc.day21;

import aoc.ChristmasException;
import aoc.FileUtil;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

import static aoc.day21.StepCounter.Direction.*;

public class StepCounter {

    char[][] grid;

    void main() {
        grid = FileUtil.readToGrid("day21");
        int steps = 64;
        System.out.println(STR."Taking \{steps} steps gives \{takeSteps(steps)} possible destinations.");
    }

    int takeSteps(int steps) {
        var points = Set.of(locateStartPoint());
        for (int i = 0; i < steps; i++) {
            points = takeAStep(points);
        }
        print(points);
        return points.size();
    }

    Set<Point> takeAStep(Set<Point> startingPoints) {
        Set<Point> destinations = new HashSet<>();
        for (var point : startingPoints) {
            move(UP, point, destinations);
            move(DOWN, point, destinations);
            move(LEFT, point, destinations);
            move(RIGHT, point, destinations);
        }
        return destinations;
    }

    void move(Direction direction, Point point, Set<Point> destinations) {
        int x = point.x + direction.x;
        int y = point.y + direction.y;
        if (x < 0 || x >= grid.length || y < 0 || y >= grid[0].length) {
            return;
        }
        if (grid[x][y] != '#') {
            Point newPoint = new Point(x, y);
            destinations.add(newPoint);
        }
    }


    Point locateStartPoint() {
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                if (grid[i][j] == 'S') return new Point(i, j);
            }
        }
        throw new ChristmasException("No starting point found");
    }

    void print(Set<Point> destinations) {
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                System.out.print(destinations.contains(new Point(i, j)) ? 'O' : grid[i][j]);
            }
            System.out.println();
        }
    }

    enum Direction {
        UP(-1, 0), DOWN(1, 0), LEFT(0, -1), RIGHT(0, 1);
        public final int x, y;
        Direction(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
}

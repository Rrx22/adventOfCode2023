package aoc.day18;

import aoc.ChristmasException;
import aoc.FileUtil;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class LavaductLagoon {

    void main() {
        System.out.println(dig(false));
        System.out.println(dig(true));
    }

    long dig(boolean useHexidecimalInstructions) {
        var digPlan = readDigPlan(useHexidecimalInstructions);
        int x = 0;
        int y = 0;
        List<Point> trench = new ArrayList<>();
        trench.add(new Point(x, y));

        for (var instruction : digPlan) {
            for (int i = 0; i < instruction.meters; i++) {
                x += instruction.moveX();
                y += instruction.moveY();
                trench.add(new Point(x, y));
            }
        }
        return countPointsInsideMap(trench, useHexidecimalInstructions);
    }

    long countPointsInsideMap(List<Point> trench, boolean hexaLogs) {
        long count = 0L;
        int minX = trench.stream().mapToInt(n -> n.x).min().getAsInt();
        int maxX = trench.stream().mapToInt(n -> n.x).max().getAsInt();
        int minY = trench.stream().mapToInt(n -> n.y).min().getAsInt();
        int maxY = trench.stream().mapToInt(n -> n.y).max().getAsInt();

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                Point point = new Point(x, y);
                var isDug = trench.contains(point);
                if (isDug) {
                    count++;
                    if (!hexaLogs) System.out.print('#');
                } else if (isPointInsidePolygon(point, trench)) {
                    count++;
                    if (!hexaLogs) System.out.print(".");
                } else {
                    if (!hexaLogs) System.out.print(" ");
                }
                if (hexaLogs && count % 25000 == 0) System.out.printf("%,d%n", 952408144115L - count);
            }
            if (!hexaLogs) System.out.println();
        }
        return count;
    }

    boolean isPointInsidePolygon(Point point, List<Point> polygon) {
        long intersectCount = 0L;

        for (int i = 0; i < polygon.size(); i++) {
            Point p1 = polygon.get(i);
            Point p2 = polygon.get((i + 1) % polygon.size());

            if ((p1.y >= point.y) != (p2.y >= point.y) &&
                    point.x < (p2.x - p1.x) * (point.y - p1.y) / (p2.y - p1.y) + p1.x) {
                intersectCount++;
            }
        }

        return intersectCount % 2 == 1;
    }

    private List<Instruction> readDigPlan(boolean hexaDecimalDigging) {
        var input = FileUtil.readFile("day18");
        List<Instruction> digPlan = new ArrayList<>();
        for (var line : input) {
            var i = line.split("\\s+");
            if (hexaDecimalDigging) {
                var hex = i[2];
                hex = hex.replaceAll("[()#]", "");
                var meters = Integer.parseInt(hex.substring(0, 5), 16); // HEX > DEC
                var direction = switch (hex.charAt(5)) {
                    case '0' -> 'R';
                    case '1' -> 'D';
                    case '2' -> 'L';
                    case '3' -> 'U';
                    default -> throw new ChristmasException("Unexpected value: " + hex.charAt(5));
                };
                Instruction instruction = new Instruction(direction, meters);
                digPlan.add(instruction);
                System.out.println(instruction);
            } else {
                digPlan.add(new Instruction(i[0].charAt(0), Integer.parseInt(i[1])));
            }
        }
        return digPlan;
    }

    record Instruction(char direction, int meters) {
        int moveX() {
            return switch (direction) {
                case 'U' -> -1;
                case 'D' -> 1;
                case 'R', 'L' -> 0;
                default -> throw new ChristmasException("Unexpected value: " + direction);
            };
        }

        public int moveY() {
            return switch (direction) {
                case 'L' -> -1;
                case 'R' -> 1;
                case 'U', 'D' -> 0;
                default -> throw new ChristmasException("Unexpected value: " + direction);
            };
        }
    }
}

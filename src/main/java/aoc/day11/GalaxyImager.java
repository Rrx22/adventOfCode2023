package aoc.day11;

import aoc.FileUtil;

import java.util.ArrayList;
import java.util.List;

public class GalaxyImager {

    public static void main(String[] args) {
        var universe = FileUtil.readToGrid("day11");
        computeGalaxyPaths(universe, 1);
        computeGalaxyPaths(universe, 1000000);
    }

    private static void computeGalaxyPaths(char[][] universe , int expansion) {
        expansion = (expansion == 1) ? expansion : expansion - 1;

        List<int[]> galaxyCoords = collectGalaxyLocations(universe);
        List<Integer> emptyRows = collectEmptyRows(universe);
        List<Integer> emptyCols = collectEmptyCols(universe);

        long sum = 0;
        for (int i = 0; i < galaxyCoords.size() - 1; i++) {
            for (int j = i + 1; j < galaxyCoords.size(); j++) { // this is to prevent double pathing (i.e. galaxy1->galaxy7 and galaxy7->galaxy1)
                int[] galaxy1 = galaxyCoords.get(i);
                int[] galaxy2 = galaxyCoords.get(j);
                sum += oneDimensionalDistance(galaxy1[0], galaxy2[0], emptyRows, expansion); // vertical distance
                sum += oneDimensionalDistance(galaxy1[1], galaxy2[1], emptyCols, expansion); // horizontal distance
            }
        }
        System.out.println(STR. "Result for expansion factor \{ expansion == 1 ? expansion : expansion + 1 } is: \{ sum }" );
    }

    private static List<int[]> collectGalaxyLocations(char[][] universe) {
        List<int[]> galaxies = new ArrayList<>();
        for (int i = 0; i < universe.length; i++) {
            for (int j = 0; j < universe[0].length; j++) {
                char c = universe[i][j];
                if (c != '.') {
                    galaxies.add(new int[]{i, j});
                }
            }
        }
        return galaxies;
    }

    private static long oneDimensionalDistance(int galaxy1, int galaxy2, List<Integer> galaxyFreeIndexes, int expansion) {
        long multiplier = galaxyFreeIndexes.stream().filter(row ->
                row > Math.min(galaxy1, galaxy2) && row < Math.max(galaxy1, galaxy2)
        ).count();
        long multitude = expansion * multiplier;
        long distance = Math.abs(galaxy1 - galaxy2);
        return distance + multitude;
    }

    private static List<Integer> collectEmptyRows(char[][] universe) {
        List<Integer> emptyRows = new ArrayList<>();
        for (int i = 0; i < universe.length; i++) {
            var line = String.valueOf(universe[i]);
            if (line.replace(".", "").isEmpty()) {
                emptyRows.add(i);
            }
        }
        return emptyRows;
    }

    private static List<Integer> collectEmptyCols(char[][] universe) {
        List<Integer> emptyCols = new ArrayList<>();
        for (int col = 0; col < universe[0].length; col++) {
            boolean isEmptyCol = true;
            for (char[] chars : universe) {
                if (chars[col] != '.') {
                    isEmptyCol = false;
                    break;
                }
            }
            if (isEmptyCol) {
                emptyCols.add(col);
            }
        }
        return emptyCols;
    }
}

package aoc.day9;

import aoc.FileUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Oasis
 * And
 * Sand
 * Instability
 * Sensor
 */
public class OASIS {

    public static void main(String[] args) {
        var oasisReading = FileUtil.readFile("day9");
        var forecast = OASIS.analyze(oasisReading, false);
        var historic = OASIS.analyze(oasisReading, true);
        System.out.println("\nO.A.S.I.S. FORECAST ANALYSIS: " + forecast);
        System.out.println("O.A.S.I.S. HISTORIC ANALYSIS: " + historic);
    }

    private static int analyze(List<String> oasisReading, boolean historic) {
        int data = 0;
        System.out.println("\n" + (historic ? "HISTORIC" : "FORECAST") + " DATA");
        for (var s : oasisReading) {
            var arr = s.split("\\s+");
            var numbers = Arrays.stream(arr).map(Integer::parseInt).toList();
            var prediction = extrapolate(historic ? numbers.reversed() : numbers);
            System.out.println((historic ? numbers.reversed() : numbers) + " -> " + prediction);
            data += prediction;
        }
        return data;
    }

    public static int extrapolate(List<Integer> row) {
        List<Integer> sequences = new ArrayList<>();
        for (int i = 1; i < row.size(); i++) {
            sequences.add(row.get(i) - row.get(i - 1));
        }

        if (sequences.stream().allMatch(i -> i == 0)) {
            return row.getLast();
        }
        return row.getLast() + extrapolate(sequences);
    }
}

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
        int forecastData = analyzeReading(oasisReading, false);
        int historicData = analyzeReading(oasisReading, true);
        System.out.println("\nO.A.S.I.S. FORECAST ANALYSIS: " + forecastData);
        System.out.println("O.A.S.I.S. HISTORIC ANALYSIS: " + historicData);
    }

    private static int analyzeReading(List<String> oasisReading, boolean historic) {
        int data = 0;
        System.out.println((historic ? "HISTORIC" : "FORECAST") + " DATA");
        for (var s : oasisReading) {
            var arr = s.split("\\s+");
            var numbers = Arrays.stream(arr).map(Integer::parseInt).toList();
            int prediction = extrapolate(historic ? numbers.reversed() : numbers);
            System.out.println((historic ? numbers.reversed() : numbers) + " -> " + prediction);
            data += prediction;
        }
        return data;
    }

    public static int extrapolate(List<Integer> row) {
        List<Integer> sequences = new ArrayList<>();

        for (int i = 1; i < row.size(); i++) {
            int sequence = row.get(i) - row.get(i - 1);
            sequences.add(sequence);
        }

        if (!sequences.stream().allMatch(i -> i == 0)) {
            return row.getLast() + extrapolate(sequences);
        }
        return row.getLast();
    }

}















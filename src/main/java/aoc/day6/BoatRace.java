package aoc.day6;

import aoc.FileUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BoatRace {

    public static void main(String[] args) {
        var input = FileUtil.readFile("day6");
        var times = input.get(0).replace("Time:", "").trim().split("\\s+");
        var distances = input.get(1).replace("Distance:", "").trim().split("\\s+");

        System.out.println("Result of calculations with bad kerning: " + wrongRaceOutput(times, distances));
        System.out.println("Real results: " + realRace(times, distances));
    }

    private static int realRace(String[] timeStrings, String[] distanceStrings) {
        var time = String.join("", timeStrings);
        var distances = String.join("", distanceStrings);
        return computeOptionsFor(Long.parseLong(time), Long.parseLong(distances));
    }

    private static int wrongRaceOutput(String[] timeStrings, String[] distanceStrings) {
        var times = Arrays.stream(timeStrings).map(Integer::parseInt).toList();
        var distances = Arrays.stream(distanceStrings).map(Integer::parseInt).toList();
        List<Integer> results = new ArrayList<>();
        for (int i = 0; i < times.size(); i++) {
            int options = computeOptionsFor(times.get(i), distances.get(i));
            results.add(options);
        }
        return results.stream().reduce(1, (a, b) -> a * b);
    }

    private static int computeOptionsFor(long time, long distance) {
        int optionsCounter = 0;
        for (int millisButtonPressed = 0; millisButtonPressed < time; millisButtonPressed++) {
            long result = millisButtonPressed * (time - millisButtonPressed);
            if (result > distance) {
                optionsCounter += 1;
            }
        }
        System.out.printf("Time %d has %d options%n", time, optionsCounter);
        return optionsCounter;
    }
}

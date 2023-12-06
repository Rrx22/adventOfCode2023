package aoc.day6;

import aoc.FileUtil;

import java.util.Arrays;

public class BoatRace {

    public static void main(String[] args) {
        var input = FileUtil.readFile("day6");
        var times = input.get(0).replace("Time:", "").trim().split("\\s+");
        var distances = input.get(1).replace("Distance:", "").trim().split("\\s+");
        System.out.println("Race options using poorly kerned input: " + raceWithPoorKerning(times, distances));
        System.out.println("Race options using correct input      : " + race(times, distances));
    }

    private static int race(String[] timeStrings, String[] distanceStrings) {
        var time = String.join("", timeStrings);
        var distances = String.join("", distanceStrings);
        return computeOptionsFor(Long.parseLong(time), Long.parseLong(distances));
    }

    private static int raceWithPoorKerning(String[] timeStrings, String[] distanceStrings) {
        var times = Arrays.stream(timeStrings).map(Integer::parseInt).toList();
        var distances = Arrays.stream(distanceStrings).map(Integer::parseInt).toList();
        int result = 1;
        for (int i = 0; i < times.size(); i++) {
            result *= computeOptionsFor(times.get(i), distances.get(i));
        }
        return result;
    }

    private static int computeOptionsFor(long raceTime, long distanceToWin) {
        int numberOfOptions = 0;
        for (int buttonPressedTime = 0; buttonPressedTime < raceTime; buttonPressedTime++) {
            long distanceWhenPressed = buttonPressedTime * (raceTime - buttonPressedTime);
            if (distanceWhenPressed > distanceToWin) {
                numberOfOptions += 1;
            }
        }
        System.out.printf("Time %d has %d options%n", raceTime, numberOfOptions);
        return numberOfOptions;
    }
}

package aoc.day6;

import aoc.FileUtil;

import java.util.Arrays;

import static java.lang.StringTemplate.STR;

public class BoatRace {

    public static void main(String[] args) {
        var input = FileUtil.readFile("day6");
        var times = input.get(0).replace("Time:", "").trim().split("\\s+");
        var distances = input.get(1).replace("Distance:", "").trim().split("\\s+");
        System.out.println("Race options using poorly kerned input: " + raceWithPoorKerning(times, distances));
        System.out.println("----------------");
        System.out.println("Race options using correct input      : " + race(times, distances));
    }

    private static long race(String[] timeStrings, String[] distanceStrings) {
        var time = String.join("", timeStrings);
        var distances = String.join("", distanceStrings);
        return computeOptionsFor(Long.parseLong(time), Long.parseLong(distances));
    }

    private static long raceWithPoorKerning(String[] timeStrings, String[] distanceStrings) {
        var times = Arrays.stream(timeStrings).map(Integer::parseInt).toList();
        var distances = Arrays.stream(distanceStrings).map(Integer::parseInt).toList();
        long result = 1;
        for (int i = 0; i < times.size(); i++) {
            result *= computeOptionsFor(times.get(i), distances.get(i));
        }
        return result;
    }

    private static long computeOptionsFor(long raceTime, long distanceToWin) {
        for (int buttonPressedTime = 0; buttonPressedTime < raceTime; buttonPressedTime++) {
            long leftOverRaceTime = raceTime - buttonPressedTime;
            long distanceWhenPressed = buttonPressedTime * leftOverRaceTime;
            if (distanceWhenPressed > distanceToWin) {
                long numberOfOptions = leftOverRaceTime - buttonPressedTime + 1;
                System.out.println(STR."Time \{raceTime} has \{numberOfOptions} options (Press button anywhere from \{buttonPressedTime} to \{leftOverRaceTime} milliSeconds!)");
                return numberOfOptions;
            }
        }
        return 0;
    }
}

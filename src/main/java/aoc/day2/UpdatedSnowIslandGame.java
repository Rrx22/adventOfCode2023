package aoc.day2;

import aoc.FileUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class UpdatedSnowIslandGame {

    public static void main(String[] args) {
        List<String> games = FileUtil.readFile("day2-1");
        System.out.println(playSnowIslandGame(games));
    }

    private static int playSnowIslandGame(List<String> gameLines) {
        int totalSnowIslandGamePower = 0;
        for (var line : gameLines) {
            var splitLine = line.split(": ");
            var grabs = mapToBagGrabs(splitLine[1]);
            totalSnowIslandGamePower += computeSnowIslandGamePower(grabs);
        }
        return totalSnowIslandGamePower;
    }

    private static int computeSnowIslandGamePower(List<BagGrab> grabs) {
        return grabs.stream()
                .collect(Collectors.toMap(BagGrab::color, BagGrab::amount, (a, b) -> a > b ? a : b))
                .values()
                .stream()
                .reduce(1, (a, b) -> a * b);
    }

    private static List<BagGrab> mapToBagGrabs(String game) {
        var allGrabs = game.split("; |, ");
         return Arrays.stream(allGrabs)
                .map(s -> s.split(" "))
                .map(a -> new BagGrab(Integer.parseInt(a[0]), a[1]))
                .toList();
    }

    record BagGrab(int amount, String color) {
    }
}

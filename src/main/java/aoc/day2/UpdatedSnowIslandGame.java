package aoc.day2;

import aoc.FileUtil;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class UpdatedSnowIslandGame {

    public static void main(String[] args) {

        List<String> games = FileUtil.readFile("day2-1");

        int gameOutcome = playSnowIslandGame(games);
        System.out.println(gameOutcome);
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
        var minimumAmounts = grabs.stream()
                .collect(Collectors.toMap(
                        BagGrab::color,
                        BagGrab::amount,
                        (amount1, amount2) -> amount1 > amount2 ? amount1 : amount2
                ));
        return minimumAmounts.get("red") * minimumAmounts.get("green") * minimumAmounts.get("blue");
    }

    private static List<BagGrab> mapToBagGrabs(String game) {
        List<BagGrab> bagGrabs = new ArrayList<>();

        var allGrabs = game.split("; |, ");
        for (var grab : allGrabs) {
            var splitGrab = grab.split(" ");
            var amount = Integer.parseInt(splitGrab[0]);
            var color = splitGrab[1];
            bagGrabs.add(new BagGrab(amount, color));
        }
        return bagGrabs;
    }

    record BagGrab(int amount, String color) {}
}

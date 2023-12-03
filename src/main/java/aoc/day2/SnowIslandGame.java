package aoc.day2;

import aoc.FileUtil;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class SnowIslandGame {

    private static List<BagGrab> ruleBook;

    public static void main(String[] args) throws URISyntaxException, IOException {

        List<String> games = FileUtil.readFile("day2-1");

        setRules();

        int snowIslandGameOutcome = playSnowIslandGame(games);
        System.out.println(snowIslandGameOutcome);
    }

    private static int playSnowIslandGame(List<String> gameLines) {
        int sumOfPossibleGameIDs = 0;

        for (var line : gameLines) {
            var splitLine = line.split(": ");
            var bagGrabs = mapToBagGrabs(splitLine[1]);
            if (isGamePossible(bagGrabs)) {
                var gameID = Integer.parseInt(splitLine[0].replace("Game ", ""));
                sumOfPossibleGameIDs += gameID;
            }
        }
        return sumOfPossibleGameIDs;
    }

    private static List<BagGrab> mapToBagGrabs(String game) {
        List<BagGrab> bagGrabs = new ArrayList<>();

        var allGrabs = game.split("; |, ");
        for (var grab : allGrabs) {
            var splitAmountAndColor = grab.split(" ");
            var amount = Integer.parseInt(splitAmountAndColor[0]);
            var color = splitAmountAndColor[1];
            bagGrabs.add(new BagGrab(amount, color));
        }
        return bagGrabs;
    }

    private static boolean isGamePossible(List<BagGrab> bagGrabs) {
        for (var bagGrab : bagGrabs) {
            var rule = ruleBook.stream()
                    .filter(grabRule -> bagGrab.color.equals(grabRule.color))
                    .findFirst()
                    .orElseThrow();
            if (bagGrab.amount > rule.amount) {
                return false;
            }
        }
        return true;
    }

    private static void setRules() {
        ruleBook = List.of(
                new BagGrab(12, "red"),
                new BagGrab(13, "green"),
                new BagGrab(14, "blue")
        );
    }
    record BagGrab(int amount, String color) {}
}

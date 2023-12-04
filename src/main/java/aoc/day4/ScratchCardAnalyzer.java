package aoc.day4;

import aoc.FileUtil;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ScratchCardAnalyzer {

    private static List<ScratchCard> scratchCards;

    public static void main(String[] args) {
        var inputList = FileUtil.readFile("day4");
        scratchCards = inputList.stream().map(ScratchCardAnalyzer::mapToScratchCard).toList();
        System.out.println("Total scratch cards value: " + analyzeScratchCards());
        System.out.println("Amount of scratch cards: " + analyzeScratchCardsCorrectly());
    }

    private static int analyzeScratchCards() {
        return scratchCards.stream().map(ScratchCard::getTotalValue).reduce(0, Integer::sum);
    }

    private static long analyzeScratchCardsCorrectly() {
        return generateListOfCopies(scratchCards).values().stream().reduce(0, Integer::sum);
    }

    private static Map<Integer, Integer> generateListOfCopies(List<ScratchCard> scratchCards) {
        Map<Integer, Integer> copies = scratchCards.stream()
                .map(sc -> Map.entry(sc.id, 1))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        for (var card : scratchCards) {
            for (int i = 1; i <= card.getMatches(); i++) {
                copies.put(card.id + i, copies.get(card.id + i) + copies.get(card.id));
            }
        }
        return copies;
    }

    private static ScratchCard mapToScratchCard(String row) {
        var idSplit = row.split(":");
        var scratchCardSplit = idSplit[1].split("\\|");

        int scratchCardID = Integer.parseInt(idSplit[0].replace("Card ", "").strip());
        var winningNumbers = scratchCardSplit[0];
        var ownNumbers = scratchCardSplit[1];
        return new ScratchCard(scratchCardID, convertToList(winningNumbers), convertToList(ownNumbers));
    }

    private static List<Integer> convertToList(String numbers) {
        return Arrays.stream(numbers.split(" "))
                .filter(s -> !s.isEmpty())
                .map(Integer::parseInt)
                .toList();
    }

    record ScratchCard(int id, List<Integer> winningNumbers, List<Integer> ownNumbers) {
        int getMatches() {
            return (int) ownNumbers.stream()
                    .filter(winningNumbers::contains)
                    .count();
        }

        int getTotalValue() {
            return 1 << getMatches() - 1;
        }
    }

}

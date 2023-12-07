package aoc.day7;

import aoc.ChristmasException;
import aoc.FileUtil;

import java.util.*;
import java.util.stream.Collectors;

public class CamelCardGame {

    static boolean jokerRule = false;
    static boolean log = false;

    public static void main(String[] args) {
        var t0 = System.currentTimeMillis();
        Game game = setUpCamelCards();
        System.out.printf("Won %d playing Camel Cards in %dms!%n", game.play(), System.currentTimeMillis() - t0);

        t0 = System.currentTimeMillis();
        jokerRule = true;
        game = setUpCamelCards();
        System.out.printf("Won %d playing Camel Cards in %dms!%n", game.play(), System.currentTimeMillis() - t0);
    }

    private static Game setUpCamelCards() {
        var input = FileUtil.readFile("day7");
        Set<Hand> hands = new TreeSet<>();
        for (var line : input) {
            hands.add(createHand(line));
        }
        return new Game(hands);
    }

    private static Hand createHand(String line) {
        var arr = line.split(" ");
        int bet = Integer.parseInt(arr[1]);
        List<Card> cards = new ArrayList<>();
        for (char c : arr[0].toCharArray()) {
            cards.add(new Card(c));
        }
        String type = deriveType(arr[0]);
        return new Hand(cards, bet, type);
    }

    private static String deriveType(String hand) {
        Map<Character, Integer> cards = new HashMap<>();
        for (var c : hand.toCharArray()) {
            int value = cards.getOrDefault(c, 0);
            cards.put(c, value + 1);
        }

        if (jokerRule && cards.containsKey('J')) {
            var jokers = cards.get('J');
            if (jokers == 5) {
                cards.remove('J');
                cards.put('A', 5);
            } else {
                int max = cards.entrySet().stream()
                        .filter(entry -> entry.getKey() != 'J')
                        .mapToInt(Map.Entry::getValue)
                        .max()
                        .orElse(0);
                Map.Entry<Character, Integer> cardToUseJoker = cards.entrySet().stream()
                        .filter(entry -> entry.getKey() != 'J')
                        .filter(e -> e.getValue() == max)
                        .toList().get(0);
                cards.put(cardToUseJoker.getKey(), cardToUseJoker.getValue() + jokers);
                cards.remove('J');
            }
        }

        int pairs = 0;
        if (cards.containsValue(2)) {
            for (var card : cards.values()) {
                if (card == 2) {
                    pairs++;
                }
            }
        }

        boolean hasOnePair = pairs == 1;
        boolean hasTwoPairs = pairs == 2;
        boolean hasThreeOfAKind = cards.containsValue(3);
        boolean hasFourOfAKind = cards.containsValue(4);
        boolean hasFiveOfAKind = cards.containsValue(5);

        if (hasOnePair && hasThreeOfAKind) return "FULL_HOUSE";
        if (hasOnePair) return "ONE_PAIR";
        if (hasTwoPairs) return "TWO_PAIRS";
        if (hasThreeOfAKind) return "THREE_OF_A_KIND";
        if (hasFourOfAKind) return "FOUR_OF_A_KIND";
        if (hasFiveOfAKind) return "FIVE_OF_A_KIND";
        return "HIGH_CARD";
    }


    record Game(Set<Hand> hands) {
        public long play() {
            long winnings = 0L;
            long ranking = 1L;
            for (Hand hand : hands) {
                winnings += ranking * hand.betAmount;
                if (log) System.out.println(ranking + ": " + hand);
                ranking++;
            }
            return winnings;
        }
    }

    record Hand(List<Card> cards, int betAmount, String type) implements Comparable<Hand> {
        @Override
        public String toString() {
            return cards.stream().map(c -> String.valueOf(c.label)).collect(Collectors.joining())
                    + " " + betAmount
                    + " " + type;
        }

        int getTypeValue() {
            return switch (type) {
                case "HIGH_CARD" -> 1;
                case "ONE_PAIR" -> 2;
                case "TWO_PAIRS" -> 3;
                case "THREE_OF_A_KIND" -> 4;
                case "FULL_HOUSE" -> 5;
                case "FOUR_OF_A_KIND" -> 6;
                case "FIVE_OF_A_KIND" -> 7;
                default -> throw new ChristmasException("Unsupported type!");
            };
        }

        @Override
        public int compareTo(Hand otherHand) { //treeSet is automatically ordering based on this method implementation
            if (getTypeValue() != otherHand.getTypeValue()) { // check primary condition: TYPE
                return getTypeValue() - otherHand.getTypeValue();
            }
            for (int i = 0; i < cards.size(); i++) { // check secondary condition: INDIVIDUAL CARD LABELS
                var thisValue = cards.get(i).getValue();
                var otherValue = otherHand.cards.get(i).getValue();
                if (thisValue != otherValue) {
                    return thisValue - otherValue;
                }
            }
            return 0;
        }
    }

    record Card(char label) {
        int getValue() {
            return switch (label) {
                case '2' -> 2;
                case '3' -> 3;
                case '4' -> 4;
                case '5' -> 5;
                case '6' -> 6;
                case '7' -> 7;
                case '8' -> 8;
                case '9' -> 9;
                case 'T' -> 10;
                case 'J' -> jokerRule ? 1 : 11;
                case 'Q' -> 12;
                case 'K' -> 13;
                case 'A' -> 14;
                default -> throw new ChristmasException("Unsupported label");
            };
        }
    }
}

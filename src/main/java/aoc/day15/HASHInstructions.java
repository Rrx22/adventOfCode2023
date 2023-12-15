package aoc.day15;

import aoc.ChristmasAssert;
import aoc.FileUtil;

import java.util.*;
import java.util.function.Predicate;

public class HASHInstructions {
    /**
     * HASHMAP
     * Holiday
     * ASCII
     * String
     * Helper
     * Manual
     * Arrangement
     * Procedure
     */
    private static final Map<Long, Deque<Lens>> HASHMAP = new HashMap<>();

    public static void main(String[] args) {
        ChristmasAssert.test(decryptHASH(), 507666L);
        ChristmasAssert.test(executeHASHMAP(), 233537L);
    }

    private static long decryptHASH() {
        var csvFile = FileUtil.readFile("day15").getFirst();
        long sum = 0L;
        var hashes = csvFile.split(",");
        for (var hash : hashes) {
            sum += decrypt(hash);
            updateHASHMAP(hash);
        }
        return sum;
    }

    private static long decrypt(String hash) {
        return hash.chars().reduce(0, (acc, value) -> (acc + value) * 17 % 256); // accumulator + value
    }

    private static void updateHASHMAP(String hash) {
        var idx = hash.contains("=") ? hash.indexOf('=') : hash.indexOf('-');
        var label = hash.substring(0, idx);
        var box = decrypt(label);
        HASHMAP.computeIfAbsent(box, k -> new ArrayDeque<>());

        var lenses = HASHMAP.get(box);
        Predicate<Lens> labelFunction = l -> l.label.equals(label);
        if (hash.charAt(idx) == '-') { // if operation is '-', remove the lens from the Deque
            lenses.removeIf(labelFunction);
            return;
        }

        var focalLength = Long.parseLong(String.valueOf(hash.charAt(hash.length() - 1))); // here operation is '='
        if (lenses.stream().anyMatch(labelFunction)) {  // update the existing lens
            lenses.stream().filter(labelFunction).findFirst().orElseThrow().focalLength = focalLength;
        } else { // add the lens
            lenses.addLast(new Lens(label, focalLength));
        }
    }

    private static long executeHASHMAP() {
        long sum = 0L;
        for (var entry : HASHMAP.entrySet()) { // for each box
            int max = entry.getValue().size();
            for (int i = 1; i <= max; i++) {
                sum += (entry.getKey() + 1) * i * entry.getValue().pop().focalLength; // calculate each LENS entry's result
            }
        }
        return sum;
    }

    static class Lens {
        public final String label;
        public long focalLength;
        public Lens(String label, long focalLength) {
            this.label = label;
            this.focalLength = focalLength;
        }
    }
}
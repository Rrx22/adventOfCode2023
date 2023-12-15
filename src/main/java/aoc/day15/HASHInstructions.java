package aoc.day15;

import aoc.ChristmasAssert;
import aoc.FileUtil;

import java.util.*;

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
    final Map<Long, Deque<Lens>> HASHMAP = new HashMap<>();

    void main() {
        ChristmasAssert.test(decryptHASH(), 507666L);
        ChristmasAssert.test(executeHASHMAP(), 233537L);
    }

    long decryptHASH() {
        var csvFile = FileUtil.readFile("day15").getFirst();
        long sum = 0L;
        for (var hash : csvFile.split(",")) {
            sum += decrypt(hash);
            updateHASHMAP(hash);
        }
        return sum;
    }

    void updateHASHMAP(String hash) {
        var idx = hash.contains("=") ? hash.indexOf('=') : hash.indexOf('-');
        var label = hash.substring(0, idx);
        var box = decrypt(label);
        HASHMAP.computeIfAbsent(box, k -> new ArrayDeque<>());

        var lenses = HASHMAP.get(box);
        if (hash.charAt(idx) == '-') { // if operation is '-', remove the lens from the Deque
            lenses.removeIf(l -> l.label.equals(label));
            return;
        }

        var focalLength = Long.parseLong(String.valueOf(hash.charAt(hash.length() - 1))); // here operation is '='
        if (lenses.stream().anyMatch(l -> l.label.equals(label))) {  // update the existing lens
            lenses.stream().filter(l -> l.label.equals(label)).findFirst().orElseThrow().focalLength = focalLength;
        } else { // add the lens
            lenses.addLast(new Lens(label, focalLength));
        }
    }

    long executeHASHMAP() {
        long sum = 0L;
        for (var entry : HASHMAP.entrySet()) { // for each box
            int max = entry.getValue().size();
            for (int i = 1; i <= max; i++) {
                sum += (entry.getKey() + 1) * i * entry.getValue().pop().focalLength; // calculate each LENS entry's result
            }
        }
        return sum;
    }

    long decrypt(String hash) {
        return hash.chars().reduce(0, (acc, value) -> (acc + value) * 17 % 256); // accumulator + value
    }

    class Lens {
        public final String label;
        public long focalLength;

        public Lens(String label, long focalLength) {
            this.label = label;
            this.focalLength = focalLength;
        }
    }
}
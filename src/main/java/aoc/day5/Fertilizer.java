package aoc.day5;

import aoc.FileUtil;

import java.util.*;

public class Fertilizer {

    /**
     * <a href="https://en.wikipedia.org/wiki/Almanac">Almanac</a>
     */
    private static List<String> almanac;

    public static void main(String[] args) {
        almanac = FileUtil.readFile("day5");
        System.out.println("Plant too few seeds  : " + plantFewSeeds() + " is " + (plantFewSeeds() == 346433842));
//        System.out.println("Plant many many seeds:  " + plantManySeeds());
    }


    private static long plantFewSeeds() {
        var seeds = getNumbers(almanac.get(0).replace("seeds: ", "").trim());
        return
        return findLowestLocationNumber(seeds);
    }

    private static long plantManySeeds() {
        var fewSeeds = getNumbers(almanac.get(0).replace("seeds: ", "").trim());
        List<Long> seeds = new ArrayList<>();
        for (int i = 0; i < fewSeeds.size(); i += 2) {
            long start = fewSeeds.get(i);
            long range = fewSeeds.get(i + 1);
            for (long j = start; j < start + range; j++) {
                seeds.add(j);
            }
            seeds.add(start);
        }
        return findLowestLocationNumber(seeds);
    }

    private static long findLowestLocationNumber(List<Long> seeds) {
        var soil = decryptInstructions("seed-to-soil map:", seeds);
        var fertilizer = decryptInstructions("soil-to-fertilizer map:", soil);
        var water = decryptInstructions("fertilizer-to-water map:", fertilizer);
        var light = decryptInstructions("water-to-light map:", water);
        var temperature = decryptInstructions("light-to-temperature map:", light);
        var humidity = decryptInstructions("temperature-to-humidity map:", temperature);
        var location = decryptInstructions("humidity-to-location map:", humidity);

        return location.stream().min(Long::compareTo).orElse(0L);
    }

    private static List<Long> decryptInstructions(String title, List<Long> refNumbers) {
        List<Instruction> instructions = new ArrayList<>();
        int index = almanac.indexOf(title) + 1;
        String line = almanac.get(index);
        while (!line.isEmpty()) {
            var numbers = getNumbers(line);
            long dest = numbers.get(0);
            long src = numbers.get(1);
            long range = numbers.get(2);
            instructions.add(new Instruction(dest, src, range));
            index += 1;
            line = index >= almanac.size() ? "" : almanac.get(index);
        }

        List<Long> results = new ArrayList<>();
        for (var number : refNumbers) {
            Long value = 0L;
            for (var instruction : instructions) {
                if (instruction.isBetween(number)) {
                    value = instruction.computeValue(number);
                }
            }
            results.add(value == 0L ? number : value);
        }
        return results;
    }

    private static List<Long> getNumbers(String line) {
        var numbers = line.split(" ");
        List<Long> list = new ArrayList<>();
        for (String number : numbers) {
            try {
                Long parseInt = Long.parseLong(number);
                list.add(parseInt);
            } catch (NumberFormatException e) {
                System.out.println(e.getMessage());
            }
        }
        return list;
    }

    record Instruction(long dest, long src, long range) {
        boolean isBetween(long val) {
            if (val >= src && val < src + range) {
                return true;
            }
            return false;
        }

        public Long computeValue(Long value) {
            return dest + value - src;
        }
    }

}

package aoc.day5;

import aoc.FileUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Fertilizer {

    private static List<Instruction> toLocationInstruction;
    private static List<Instruction> toSoilInstructions;
    private static List<Instruction> toFertilizerInstructions;
    private static List<Instruction> toWaterInstruction;
    private static List<Instruction> toLightInstruction;
    private static List<Instruction> toTemperatureInstruction;
    private static List<Instruction> toHumidityInstruction;

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        var almanac = FileUtil.readFile("day5");
        initInstructions(almanac);

        // roughly 2ms
        var startTime = System.currentTimeMillis();
        var result = plantFewSeeds(almanac.get(0));
        var endTime = System.currentTimeMillis();
        System.out.println("Plant too few seeds  : " + result + " in " + (endTime - startTime) + "ms");

        // roughly 2 minutes
        startTime = System.currentTimeMillis();
        result = plantManySeeds(almanac.get(0));
        endTime = System.currentTimeMillis();
        System.out.println("Plant many many seeds:  " + result + " in " + (endTime - startTime) + "ms");
    }

    private static void initInstructions(List<String> almanac) {
        toSoilInstructions = parseInstructions("seed-to-soil map:", almanac);
        toFertilizerInstructions = parseInstructions("soil-to-fertilizer map:", almanac);
        toWaterInstruction = parseInstructions("fertilizer-to-water map:", almanac);
        toLightInstruction = parseInstructions("water-to-light map:", almanac);
        toTemperatureInstruction = parseInstructions("light-to-temperature map:", almanac);
        toHumidityInstruction = parseInstructions("temperature-to-humidity map:", almanac);
        toLocationInstruction = parseInstructions("humidity-to-location map:", almanac);
    }

    private static long plantFewSeeds(String seeds) {
        var fewSeeds = getNumbers(seeds.replace("seeds: ", "").trim());
        return fewSeeds.stream()
                .mapToLong(Fertilizer::findLocationNumber)
                .min()
                .orElse(0L);
    }

    private static long plantManySeeds(String seeds) throws ExecutionException, InterruptedException {
        var fewSeeds = getNumbers(seeds.replace("seeds: ", "").trim());

        ExecutorService executorService = Executors.newFixedThreadPool(8);
        List<Future<Long>> lowestLocations = new ArrayList<>();
        for (int i = 0; i < fewSeeds.size(); i += 2) {
            int processNumber = i + 1;
            Future future = executorService.submit(() -> getLowestLocationNumber(fewSeeds.get(processNumber - 1), fewSeeds.get(processNumber), processNumber));
            lowestLocations.add(future);
        }
        executorService.shutdown();

        long lowestLocationNumber = Long.MAX_VALUE;
        for (var l : lowestLocations) {
            long location = l.get();
            if (location < lowestLocationNumber) lowestLocationNumber = location;
        }
        return lowestLocationNumber;
    }

    private static long getLowestLocationNumber(long start, long range, int processNumber) {
        long lowestLocationNumber = Long.MAX_VALUE;
        var startTime = System.currentTimeMillis();
        for (long i = start; i < (start + range); i++) {
            long locationNumber = findLocationNumber(i);
            if (locationNumber < lowestLocationNumber) {
                lowestLocationNumber = locationNumber;
            }
        }
        System.out.println("Seed " + processNumber + ": " + lowestLocationNumber + " (" + (System.currentTimeMillis() - startTime) + "ms)");
        return lowestLocationNumber;
    }

    private static long findLocationNumber(long seed) {
        var soil = decryptInstructions(toSoilInstructions, seed);
        var fertilizer = decryptInstructions(toFertilizerInstructions, soil);
        var water = decryptInstructions(toWaterInstruction, fertilizer);
        var light = decryptInstructions(toLightInstruction, water);
        var temperature = decryptInstructions(toTemperatureInstruction, light);
        var humidity = decryptInstructions(toHumidityInstruction, temperature);
        return decryptInstructions(toLocationInstruction, humidity);
    }

    private static long decryptInstructions(List<Instruction> instructions, long refNumber) {
        long value = 0L;
        for (var instruction : instructions) {
            if (instruction.isBetween(refNumber)) {
                value = instruction.computeValue(refNumber);
            }
        }
        return (value == 0L) ? refNumber : value;
    }

    private static List<Instruction> parseInstructions(String title, List<String> almanac) {
        List<Instruction> instructions = new ArrayList<>();
        Iterator<String> iterator = almanac.listIterator(almanac.indexOf(title) + 1);
        while (iterator.hasNext()) {
            String line = iterator.next();
            if (line.isEmpty()) break;
            List<Long> numbers = getNumbers(line);
            instructions.add(new Instruction(numbers.get(0), numbers.get(1), numbers.get(2)));
        }
        return instructions;
    }

    private static List<Long> getNumbers(String line) {
        return Arrays.stream(line.split("\\s+"))
                .map(Long::parseLong)
                .toList();
    }

    record Instruction(long dest, long src, long range) {
        boolean isBetween(long val) {
            return val >= src && val < src + range;
        }

        public Long computeValue(Long value) {
            return dest + value - src;
        }
    }
}
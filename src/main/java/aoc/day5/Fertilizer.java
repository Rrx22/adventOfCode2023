package aoc.day5;

import aoc.ChristmasException;
import aoc.FileUtil;

import java.util.*;
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

    public static void main(String[] args) {
        var almanac = FileUtil.readFile("day5");
        parseInstructions(almanac);
        System.out.println("Plant too few seeds  : " + plantFewSeeds(almanac.get(0)) + " is " + (plantFewSeeds(almanac.get(0)) == 346433842));
        System.out.println("Plant many many seeds:  " + plantManySeeds(almanac.get(0)));
    }

    private static void parseInstructions(List<String> almanac) {
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
                .orElse(0);
    }

    private static long plantManySeeds(String seeds) {
        var fewSeeds = getNumbers(seeds.replace("seeds: ", "").trim());

        ExecutorService executorService = Executors.newFixedThreadPool(8);
        List<Future<Long>> lowestLocations = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            int processNumber = i + 1;
            Future future = executorService.submit(() -> getLowestLocationNumber(fewSeeds.get(processNumber - 1), fewSeeds.get(processNumber), processNumber));
            lowestLocations.add(future);
        }

        executorService.shutdown();

        long lowestLocationNumber = Long.MAX_VALUE;
        for (var l : lowestLocations) {
            try {
                long location = l.get();
                if (location < lowestLocationNumber) lowestLocationNumber = location;
            } catch (InterruptedException | ExecutionException e) {
                throw new ChristmasException(e.getMessage(), e);
            }
        }
        return lowestLocationNumber;
    }

    private static long getLowestLocationNumber(long start, long range, int processNumber) {
        System.out.println("Elf-" + processNumber + ": START");
        long lowestLocationNumber = Long.MAX_VALUE;
        for (long j = start; j < start + range; j++) {
            long locationNumber = findLocationNumber(j);
            boolean b = locationNumber < lowestLocationNumber;
            if (b) {
                lowestLocationNumber = locationNumber;
                System.out.println("Elf-" + processNumber + ": new lowest number " + lowestLocationNumber);
            }
        }
        System.out.println("Elf-" + processNumber + ": FINISHED WITH NUMBER " + lowestLocationNumber);
        return lowestLocationNumber;
    }

    private static long findLocationNumber(long seed) {
        var soil = decryptInstructions(toSoilInstructions, seed);
        var fertilizer = decryptInstructions(toFertilizerInstructions, soil);
        var water = decryptInstructions(toWaterInstruction, fertilizer);
        var light = decryptInstructions(toLightInstruction, water);
        var temperature = decryptInstructions(toTemperatureInstruction, light);
        var humidity = decryptInstructions(toHumidityInstruction, temperature);
        var location = decryptInstructions(toLocationInstruction, humidity);
        return location;
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
        return instructions;
    }

    private static List<Long> getNumbers(String line) {
        var numbers = line.split(" ");
        List<Long> list = new ArrayList<>();
        for (String number : numbers) {
            list.add(Long.parseLong(number));
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

package aoc.day1;

import aoc.FileUtil;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class Calibrator {

    public static void main(String[] args) {
        var calibrationDocument = FileUtil.readFile("day1");

        var calibrationValue = findCalibrationValue(calibrationDocument);

        System.out.println(calibrationValue);
    }

    private static int findCalibrationValue(List<String> calibrationDocument) {
        return calibrationDocument
                .stream()
                .mapToInt(Calibrator::collectNumberFromLine)
                .sum();
    }

    private static int collectNumberFromLine(String line) {
        List<Character> numbers = new ArrayList<>();
        for (var c : line.toCharArray()) {
            if (Character.isDigit(c)) {
                numbers.add(c);
            }
        }
        var first = numbers.getFirst().toString();
        var last = numbers.getLast().toString();

        return Integer.parseInt(first + last);
    }

}

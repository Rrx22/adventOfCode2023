package aoc.day01;

import aoc.FileUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class UpdatedCalibrator {

    private static final Map<String, String> numberMap = new HashMap<>();

    public static void main(String[] args) {

        var calibrationDocument = FileUtil.readFile("day1-2");
        fillNumberMap();

        var calibratedValue = findCalibrationValue(calibrationDocument);
        System.out.println(calibratedValue);
    }

    private static int findCalibrationValue(List<String> calibrationDocument) {
        return calibrationDocument
                .stream()
                .mapToInt(UpdatedCalibrator::collectNumberFromLine)
                .sum();
    }

    private static int collectNumberFromLine(String line) {
        Pattern numberPattern = Pattern.compile("(?=(one|two|three|four|five|six|seven|eight|nine|\\d))");
        var matcher = numberPattern.matcher(line);

        List<String> numbers = new ArrayList<>();
        while (matcher.find()) {
            var foundSeqVerbal = matcher.group(1);
            numbers.add(isNumeric(foundSeqVerbal)
                    ? String.valueOf(foundSeqVerbal.charAt(0))
                    : numberMap.getOrDefault(foundSeqVerbal.toLowerCase(), ""));
        }

        var first = numbers.getFirst();
        var last = numbers.getLast();
        return Integer.parseInt(first + last);
    }

    public static boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static void fillNumberMap() {
        numberMap.put("one", "1");
        numberMap.put("two", "2");
        numberMap.put("three", "3");
        numberMap.put("four", "4");
        numberMap.put("five", "5");
        numberMap.put("six", "6");
        numberMap.put("seven", "7");
        numberMap.put("eight", "8");
        numberMap.put("nine", "9");
        numberMap.put("zero", "0");
    }
}

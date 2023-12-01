package nl.rrx.day1;

import nl.rrx.FileUtil;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class Callibator {

    public static void main(String[] args) throws URISyntaxException, IOException {
        var testCalibrationDocument = List.of(
                "1abc2",
                "pqr3stu8vwx",
                "a1b2c3d4e5f",
                "treb7uchet");
        var actualCalibrationDocument = FileUtil.readFile("day1");

        var x = findCalibrationValue(testCalibrationDocument);
        var y = findCalibrationValue(actualCalibrationDocument);

        System.out.println(x);
        System.out.println(y);
    }

    private static int findCalibrationValue(List<String> calibrationDocument) {
        int sum = 0;
        for(var line : calibrationDocument) {
            int sum1 = collectNumberFromLine(line);
            sum += sum1;
        }
        return sum;
    }

    private static int collectNumberFromLine(String line) {
        List<Character> numbers = new ArrayList<>();
        for (var c : line.toCharArray()) {
            if (Character.isDigit(c)) {
                numbers.add(c);
            }
        }
        var first = numbers.getFirst();
        var last = numbers.getLast();

        String value = first.toString() + last.toString();
        return Integer.parseInt(value);
    }

}

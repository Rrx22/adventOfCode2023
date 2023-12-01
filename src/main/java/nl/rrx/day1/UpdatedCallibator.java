package nl.rrx.day1;

import nl.rrx.FileUtil;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UpdatedCallibator {

    public static void main(String[] args) throws URISyntaxException, IOException {
        var calibrationDocument = FileUtil.readFile("day1-2");

        var x = findCalibrationValue(calibrationDocument);

        System.out.println(x);
    }

    private static int findCalibrationValue(List<String> calibrationDocument) {
        int sum = 0;
        for (var line : calibrationDocument) {
            int sum1 = collectNumberFromLine(line);
            sum += sum1;
        }
        return sum;
    }

    private static int collectNumberFromLine(String line) {
        //RegEx matching with separate groups and a lookaround
        Pattern pattern = Pattern.compile("(?=(one|two|three|four|five|six|seven|eight|nine|zero|\\d))");

        Matcher match = pattern.matcher(line);
        List<String> numbers = new ArrayList<>();

        while(match.find()) {
            String foundSeqVerbal = match.group(1);
                String number = isNumeric(foundSeqVerbal)
                        ? String.valueOf(foundSeqVerbal.charAt(0))
                        : convertToDigit(foundSeqVerbal);
                numbers.add(number);
            }

        String first = numbers.getFirst();
        String last = numbers.getLast();
        return Integer.parseInt(first + last);
    }

    public static String convertToDigit(String verbalNum) {
        Map<String, String> verbalMap = new HashMap<>();
        verbalMap.put("one", "1");
        verbalMap.put("two", "2");
        verbalMap.put("three", "3");
        verbalMap.put("four", "4");
        verbalMap.put("five", "5");
        verbalMap.put("six", "6");
        verbalMap.put("seven", "7");
        verbalMap.put("eight", "8");
        verbalMap.put("nine", "9");
        verbalMap.put("zero", "0");

        String digitNum = verbalMap.getOrDefault(verbalNum.toLowerCase(), "");

        return digitNum;
    }

    public static boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }

}

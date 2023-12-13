package aoc.day13;

import aoc.ChristmasAssert;
import aoc.FileUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReflectionNavigator {

    private static int counter = 0;
    private static final Map<Integer, Long> smudgedResults = new HashMap<>();

    public static void main(String[] args) {
        List<List<String>> mirrorPatterns = organiseMirrorPatterns(FileUtil.readFile("day13"));
        ChristmasAssert.test(makeMirrorNotes(mirrorPatterns), 35521L);
        ChristmasAssert.test(unsmudgeNotes(mirrorPatterns), 400L);
    }

    private static long makeMirrorNotes(List<List<String>> mirrorPattern) {
        long sum = 0L;
        for (List<String> pattern : mirrorPattern) {
            long result = navigatePattern(pattern, true) + navigatePattern(pattern, false);
            sum += result;
            smudgedResults.put(counter++, result);
        }
        return sum;
    }

    private static long unsmudgeNotes(List<List<String>> mirrorPattern) {
        counter = 0;
        long sum = 0L;
        for (List<String> pattern : mirrorPattern) {
            var t0 = System.currentTimeMillis();
            System.out.println("STARTED " + (counter + 1));
            sum += unsmudgeNote(pattern);
            System.out.println(STR."FINISHED \{ counter + 1 } IN \{ System.currentTimeMillis() - t0 }ms");
        }
        return sum;
    }

    private static long unsmudgeNote(List<String> pattern) {
        long currentVal = smudgedResults.get(counter++);
        long attempts = 0L;
        long result;
        List<String> copyList = new ArrayList<>(pattern);
        while (true) {
            for (int i = 0; i < pattern.size(); i++) {
                String line = pattern.get(i);
                for (int j = 0; j < line.length(); j++) {
                    if (attempts > (long) pattern.size() * pattern.get(0).length()) {
                        System.out.println("ERROR TRIED ALL OPTIONS");
                        return 0L;
                    }
                    char c = line.charAt(j) == '.' ? '#' : '.';
                    String start = (j == 0) ? "" : line.substring(0, j);
                    String end = (j == line.length() - 1) ? "" : line.substring(j + 1);
                    copyList.set(i, start + c + end);

                    result = navigatePattern(copyList, true);
                    if (result == 0) {
                        result = navigatePattern(copyList, false);
                    }
                    if (result != 0L && result != currentVal) {
                        System.out.println(STR."PlACED \{ c } AT [\{ i }, \{ j }] - NEW RESULT = \{ result }");
                        return result;
                    }
                    attempts++;
                }
            }
        }
    }

    private static long navigatePattern(List<String> pattern, boolean scanHorizontally) {
        int max = scanHorizontally ? pattern.size() : pattern.get(1).length();
        System.out.println("ANALYZING " + (scanHorizontally ? "ROW" : "COL"));

        for (int i = 1; i < max; i++) {
            var curr = scanHorizontally ? pattern.get(i) : getColumn(i, pattern);
            var prev = scanHorizontally ? pattern.get(i - 1) : getColumn(i - 1, pattern);

            if (curr.equals(prev)) {
                int checkCount = Math.min(i - 1, max - i - 1);
                boolean match = true;
                System.out.printf("CHECK: [%d, %d]    %s - %s%n", checkCount, checkCount + 1, prev, curr);

                for (int j = 0; j < checkCount; j++) {
                    int currIdx = i - j - 2;
                    int reflIdx = i + j + 1;
                    String original = scanHorizontally ? pattern.get(currIdx) : getColumn(currIdx, pattern);
                    String reflection = scanHorizontally ? pattern.get(reflIdx) : getColumn(reflIdx, pattern);
                    if (!original.equals(reflection)) {
                        System.out.printf("MISS : [%d, %d] %s - %s%n", currIdx, reflIdx, original, reflection);
                        match = false;
                        break;
                    } else {
                        System.out.printf("MATCH: [%d, %d] %s - %s%n", currIdx, reflIdx, original, reflection);
                    }
                }
                if (match) {
                    long result = scanHorizontally ? (i * 100L) : i;
                    System.out.println("GOOD : " + result);
                    return result;
                }
            }
        }
        System.out.println("FAIL : " + 0L);
        return 0L;
    }

    private static String getColumn(int index, List<String> pattern) {
        StringBuilder sb = new StringBuilder();
        for (String line : pattern) {
            sb.append(line.charAt(index));
        }
        return sb.toString();
    }

    private static List<List<String>> organiseMirrorPatterns(List<String> input) {
        List<List<String>> mirrorPatterns = new ArrayList<>();
        List<String> pattern = new ArrayList<>();
        for (String line : input) {
            if (line.isEmpty()) {
                mirrorPatterns.add(pattern);
                pattern = new ArrayList<>();
            } else {
                pattern.add(line);
            }
        }
        mirrorPatterns.add(pattern);
        return mirrorPatterns;
    }
}

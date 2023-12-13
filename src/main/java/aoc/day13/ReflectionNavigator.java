package aoc.day13;

import aoc.ChristmasAssert;
import aoc.FileUtil;

import java.util.*;

public class ReflectionNavigator {

    private static final boolean log = false;

    public static void main(String[] args) {
        List<List<String>> mirrorPatterns = organiseMirrorPatterns(FileUtil.readFile("day13"));
        ChristmasAssert.test(makeMirrorNotes(mirrorPatterns), 35521L);
        ChristmasAssert.test(unsmudgeNotes(mirrorPatterns), 34795L);
    }

    private static long makeMirrorNotes(List<List<String>> mirrorPattern) {
        long sum = 0L;
        for (List<String> pattern : mirrorPattern) {
            long rowResult = navigatePattern(pattern, true);
            long colResult = navigatePattern(pattern, false);
            sum += rowResult + colResult;
        }
        return sum;
    }

    private static long navigatePattern(List<String> pattern, boolean scanHorizontally) {
        int max = scanHorizontally ? pattern.size() : pattern.get(1).length();
        if (log) System.out.println("ANALYZING " + (scanHorizontally ? "ROW" : "COL"));

        for (int i = 1; i < max; i++) {
            var curr = scanHorizontally ? pattern.get(i) : getColumn(i, pattern);
            var prev = scanHorizontally ? pattern.get(i - 1) : getColumn(i - 1, pattern);

            if (curr.equals(prev)) {
                int checkCount = Math.min(i - 1, max - i - 1);
                boolean match = true;
                if (log) System.out.printf("CHECK: [%d, %d]    %s - %s%n", checkCount, checkCount + 1, prev, curr);

                for (int j = 0; j < checkCount; j++) {
                    int currIdx = i - j - 2;
                    int reflIdx = i + j + 1;
                    String original = scanHorizontally ? pattern.get(currIdx) : getColumn(currIdx, pattern);
                    String reflection = scanHorizontally ? pattern.get(reflIdx) : getColumn(reflIdx, pattern);
                    if (!original.equals(reflection)) {
                        if (log) System.out.printf("MISS : [%d, %d] %s - %s%n", currIdx, reflIdx, original, reflection);
                        match = false;
                        break;
                    } else {
                        if (log) System.out.printf("MATCH: [%d, %d] %s - %s%n", currIdx, reflIdx, original, reflection);
                    }
                }
                if (match) {
                    long result = scanHorizontally ? (i * 100L) : i;
                    if (log) System.out.println("GOOD : " + result);
                    return result;
                }
            }
        }
        if (log) System.out.println("FAIL : " + 0L);
        return 0L;
    }

    private static long unsmudgeNotes(List<List<String>> mirrorPatterns) {
        long sum = 0L;
        for (var pattern : mirrorPatterns) {
            sum += unsmudgeNote(pattern, false) * 100L;
            sum += unsmudgeNote(pattern, true);
        }
        return sum;
    }

    private static int unsmudgeNote(final List<String> pattern, boolean columns) {
        int max = columns ? pattern.get(0).length() : pattern.size();
        for (int i = 1; i < max; i++) {
            String prev = columns ? getColumn(i - 1, pattern) : pattern.get(i - 1);
            String current = columns ? getColumn(i, pattern) : pattern.get(i);
            int initialSmudges = getNrSmudges(prev, current);
            if (initialSmudges == 0 || initialSmudges == 1) {
                int elementsToCheck = Math.min(i - 1, max - i - 1);
                boolean matches = true;
                boolean smudgeFound = false;
                for (int el = 0; el < elementsToCheck; el++) {
                    String first = columns ? getColumn(i - el - 2, pattern) : pattern.get(i - el - 2);
                    String second = columns ? getColumn(i + el + 1, pattern) : pattern.get(i + el + 1);
                    int nrSmudges = getNrSmudges(first, second);
                    if (nrSmudges > 1 || (initialSmudges == 1 && nrSmudges == 1) || (nrSmudges == 1 && smudgeFound)) {
                        matches = false;
                        break;
                    } else if (nrSmudges == 1) {
                        smudgeFound = true;
                    }
                }
                if (matches && (initialSmudges == 1 ^ smudgeFound)) {
                    return i;
                }
            }
        }
        return 0;
    }

    private static int getNrSmudges(String s1, String s2) {
        int total = 0;
        for (int i = 0; i < s1.length(); i++) {
            if (s1.charAt(i) != s2.charAt(i)) {
                total++;
            }
        }
        return total;
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

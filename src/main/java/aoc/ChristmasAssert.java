package aoc;

import java.util.Objects;

public class ChristmasAssert {

    private ChristmasAssert() { }

    public static void test(Long actual, Long expected) {
        if (!Objects.equals(actual, expected)) {
            throw new ChristmasException("\nExpected: " + expected.toString() + "\nActual:   " + actual.toString());
        }
    }
}

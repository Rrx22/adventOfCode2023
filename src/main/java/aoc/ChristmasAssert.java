package aoc;

import java.util.Objects;

public class ChristmasAssert {

    private ChristmasAssert() { }

    public static void test(Long actual, Long expected) throws ChristmasException {
        if (!Objects.equals(actual, expected)) {
            throw new ChristmasException("\nExpected: " + expected + "\nActual:   " + actual);
        }
        System.out.println("-----------\nCHRISTMAS-SUCCESS: Correct value " + actual + " was detected\n-----------");
    }

    public static void test(boolean assertion, Long value) {
        test(assertion, value, "Assertion failed for value: " + value);
    }

    public static void test(boolean assertion, Long value, String message) {
        if (!assertion) {
            throw new ChristmasException("\n" + message);
        }
        System.out.println("-----------\nCHRISTMAS-SUCCESS: " + "Assertion was true for value: " + value + "\n-----------");
    }
}

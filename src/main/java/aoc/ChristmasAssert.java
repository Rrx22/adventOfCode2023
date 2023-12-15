package aoc;

import java.util.Objects;

public class ChristmasAssert {

    private ChristmasAssert() { }

    public static void test(Long actual, Long expected) throws ChristmasException {
        if (!Objects.equals(actual, expected)) {
            throw new ChristmasException("\nExpected: " + expected + "\nActual:   " + actual);
        }
        System.out.println("-----------\nCHRISTMAS-SUCCESS: " + actual + " is the RIGHT answer!\n-----------");
    }

    public static void test(boolean assertion, Long value) {
        test(assertion, value, "Your CUSTOM christmas assertion was FALSE for " + value);
    }

    public static void test(boolean assertion, Long value, String errorMessage) {
        if (!assertion) {
            throw new ChristmasException("\n" + errorMessage);
        }
        System.out.println("-----------\nCHRISTMAS-SUCCESS: " + "Your CUSTOM christmas assertion was TRUE for : " + value + "!\n-----------");
    }
}

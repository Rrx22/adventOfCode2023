package aoc;

public class ChristmasException extends RuntimeException {
    public ChristmasException(String message, Throwable cause) {
        super("Ho Ho Ho! " + message, cause);
    }
}

package aoc;

public class ChrismasException extends RuntimeException {
    public ChrismasException(String message, Throwable cause) {
        super("Ho Ho Ho! " + message, cause);
    }
}

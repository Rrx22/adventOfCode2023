package aoc;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class FileUtil {
    private FileUtil() {
    }

    public static List<String> readFile(String fileName)  {
        try {
            var resourcePath = Paths.get(ClassLoader.getSystemResource(fileName).toURI());
            return Files.readAllLines(resourcePath);
        } catch (URISyntaxException | IOException e) {
            throw new ChristmasException(e.getMessage(), e);
        }
    }
}

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

    public static char[][] readToGrid(String fileName) {
        var list = readFile(fileName);
        char[][] grid = new char[list.size()][];
        for (int i = 0; i < list.size(); i++) {
            grid[i] = list.get(i).toCharArray();
        }
        return grid;
    }
}

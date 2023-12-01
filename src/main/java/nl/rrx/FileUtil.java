package nl.rrx;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class FileUtil {
    private FileUtil() {
    }

    public static List<String> readFile(String fileName) throws URISyntaxException, IOException {
            var uri = ClassLoader.getSystemResource("realDeal/").toURI();
            var mainPath = Paths.get(uri).toString();
            var path = Paths.get(mainPath, fileName);
            return Files.readAllLines(path);
    }
}

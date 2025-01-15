package util;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Optional;

public abstract class FileReader {

    public static Optional<byte[]> readFile(String fileName) {
        try {
            return Optional.of(Files.readAllBytes(new File(fileName).toPath()));
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    public static Optional<StringBuilder> readFileAsStringBuilder(String fileName) {

        try {
            StringBuilder htmlContent = new StringBuilder();
            Files.lines(new File(fileName).toPath(), StandardCharsets.UTF_8)
                    .forEach(htmlContent::append);
            return Optional.of(htmlContent);
        } catch (IOException e) {
            return Optional.empty();
        }
    }
}

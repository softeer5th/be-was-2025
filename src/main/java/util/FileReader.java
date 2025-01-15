package util;

import java.io.File;
import java.io.IOException;
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

    public static Optional<String> readFileAsString(String fileName) {
        try {
            return Optional.of(Files.readString(new File(fileName).toPath()));
        } catch (IOException e) {
            return Optional.empty();
        }
    }
}

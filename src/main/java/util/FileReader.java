package util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;

public abstract class FileReader {
    private static final Logger logger = LoggerFactory.getLogger(FileReader.class);

    public static Optional<byte[]> readFile(String fileName) {
        try {
            return Optional.of(Files.readAllBytes(new File(fileName).toPath()));
        } catch (IOException e) {
            return Optional.empty();
        }
    }
}

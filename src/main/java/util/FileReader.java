package util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public abstract class FileReader {
    private static final Logger logger = LoggerFactory.getLogger(FileReader.class);

    public static byte[] readFile(String fileName) {
        try {
            return Files.readAllBytes(new File(fileName).toPath());
        } catch (IOException e) {
            logger.error(e.getMessage());
            return null;
        }
    }
}

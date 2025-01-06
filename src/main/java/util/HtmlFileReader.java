package util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public abstract class HtmlFileReader {
    private static final Logger logger = LoggerFactory.getLogger(HtmlFileReader.class);

    public static byte[] readHtmlFile(String fileName) {
        try {
            return Files.readAllBytes(new File(fileName).toPath());
        } catch (IOException e) {
            logger.error(e.getMessage());
            return null;
        }
    }
}

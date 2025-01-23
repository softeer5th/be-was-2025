package webserver.reader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class StaticFileReader {
    private static final ClassLoader classLoader = StaticFileReader.class.getClassLoader();
    private static final Logger logger = LoggerFactory.getLogger(StaticFileReader.class.getName());
    private static final String STATIC_FILE_PATH = "static/";

    public static Optional<String> getStaticFile(String filePath) {
        Optional<byte[]> content = getStaticFileBytes(filePath);
        if (content.isEmpty()) return Optional.empty();
        String decoded = new String(content.get(), StandardCharsets.UTF_8);
        return Optional.of(decoded);
    }

    public static Optional<byte []> getStaticFileBytes(String filePath) {
        try {
            URL url = classLoader.getResource(STATIC_FILE_PATH +filePath);
            if (url == null) {
                throw new IOException("File not found: " + filePath);
            }
            File file = new File(url.getFile());
            if (file.isDirectory()) {
                return Optional.empty();
            }
            InputStream inputStream = new BufferedInputStream(new FileInputStream(file));
            byte[] content = inputStream.readAllBytes();
            return Optional.of(content);
        } catch (IOException e) {
            logger.debug(e.getMessage());
            return Optional.empty();
        }
    }
}

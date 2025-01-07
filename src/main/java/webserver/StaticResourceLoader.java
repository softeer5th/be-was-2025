package webserver;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class StaticResourceLoader {
    private final String baseDirectory;

    public StaticResourceLoader(String baseDirectory) {
        this.baseDirectory = baseDirectory;
    }

    public byte[] load(String path) throws IOException {
        if ("/".equals(path)) {
            path = "/index.html";
        }

        Path filePath = Paths.get(baseDirectory + path);
        if (!Files.exists(filePath)) {
            return null;
        }
        return Files.readAllBytes(filePath);
    }
}
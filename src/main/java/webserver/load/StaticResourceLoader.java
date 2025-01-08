package webserver.load;

import webserver.load.LoadResult;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class StaticResourceLoader {
    private final String baseDirectory;

    public StaticResourceLoader(String baseDirectory) {
        this.baseDirectory = baseDirectory;
    }

    public LoadResult load(String path) throws IOException {
        String mappedPath = switch (path) {
            case "/" -> "/index.html";
            case "/registration" -> "/registration/index.html";
            default -> path;
        };

        Path filePath = Paths.get(baseDirectory + mappedPath);
        if (!Files.exists(filePath)) {
            return new LoadResult(null, mappedPath);
        }
        return new LoadResult(Files.readAllBytes(filePath), mappedPath);
    }
}
package webserver;

import global.model.LoadResult;

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
        String cleanedPath = removeQueryParameters(path);

        String mappedPath = switch (cleanedPath) {
            case "/" -> "/index.html";
            case "/registration" -> "/registration/index.html";
            default -> path;
        };

        Path filePath = Paths.get(baseDirectory + mappedPath);
        if (!Files.exists(filePath)) {
            return new LoadResult(null, mappedPath, "text/html");
        }
        return new LoadResult(Files.readAllBytes(filePath), mappedPath, "text/html");
    }

    private String removeQueryParameters(String path) {
        int questionMarkIndex = path.indexOf('?');
        if (questionMarkIndex != -1) {
            return path.substring(0, questionMarkIndex);
        }
        return path;
    }
}
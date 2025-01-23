package webserver;

import global.model.LoadResult;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Pattern;

public class StaticResourceLoader {
    private final String baseDirectory;
    private static final Pattern ARTICLE_COMMENT_PATTERN = Pattern.compile("^/article/[^/]+/comment$");

    public StaticResourceLoader(String baseDirectory) {
        this.baseDirectory = baseDirectory;
    }

    public LoadResult load(String path) throws IOException {
        String cleanedPath = removeQueryParameters(path);

        if (ARTICLE_COMMENT_PATTERN.matcher(cleanedPath).matches()) {
            cleanedPath = "/comment";
        }

        String mappedPath = switch (cleanedPath) {
            case "/", "/index.html" -> "/index.html";
            case "/registration" -> "/registration/index.html";
            case "/user/login.html" -> "/login/index.html";
            case "/user/login_failed.html" -> "/login/login_failed.html";
            case "/mypage" -> "/mypage/index.html";
            case "/write.html" -> "/article/index.html";
            case "/comment" -> "/comment/index.html";
            default -> null;
        };

        if (mappedPath != null) {
            Path filePath = Paths.get(baseDirectory + mappedPath);
            if (!Files.exists(filePath)) {
                return new LoadResult(null, mappedPath, "text/html", null);
            }
            return new LoadResult(
                    Files.readAllBytes(filePath),
                    mappedPath,
                    "text/html",
                    null
            );
        }

        Path resourceFilePath = Paths.get(baseDirectory + cleanedPath);
        if (Files.exists(resourceFilePath)) {
            return new LoadResult(
                    Files.readAllBytes(resourceFilePath),
                    cleanedPath,
                    "text/html",
                    null
            );
        }

        return new LoadResult(null, null, "text/html", null);
    }

    private String removeQueryParameters(String path) {
        int questionMarkIndex = path.indexOf('?');
        if (questionMarkIndex != -1) {
            return path.substring(0, questionMarkIndex);
        }
        return path;
    }
}
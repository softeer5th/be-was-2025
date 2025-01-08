package resolver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.RequestHandler;
import webserver.message.HTTPRequest;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Optional;

public class StaticResourceResolver implements ResourceResolver {
    private static final ClassLoader classLoader = RequestHandler.class.getClassLoader();
    private static final StaticResourceResolver instance = new StaticResourceResolver();
    private static final Logger logger = LoggerFactory.getLogger(StaticResourceResolver.class);
    private StaticResourceResolver() {}

    public static StaticResourceResolver getInstance() {
        return instance;
    }

    @Override
    public byte[] resolve(HTTPRequest request) {
        Optional<String> resource = findResource(request.getUri());
        if (resource.isEmpty()) {
            return "NOT FOUND".getBytes();
        }
        try {
            return Files.readAllBytes(new File(resource.get()).toPath());
        } catch (IOException e) {
            logger.error(e.getMessage());
            return "SERVER ERROR".getBytes();
        }
    }

    private Optional<String> findResource(String url) {
        url = url.startsWith("/") ? url.replaceFirst("/", "static/"): url;
        URL resource = classLoader.getResource(url);
        if (resource == null) {
            return Optional.empty();
        }
        String filePath = resource.getFile();
        return Optional.of(filePath);
    }
}

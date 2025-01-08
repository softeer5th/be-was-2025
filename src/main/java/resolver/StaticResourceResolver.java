package resolver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.RequestHandler;
import webserver.enumeration.HTTPContentType;
import webserver.message.HTTPRequest;
import webserver.message.header.records.AcceptRecord;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StaticResourceResolver implements ResourceResolver {
    private static final ClassLoader classLoader = RequestHandler.class.getClassLoader();
    private static final StaticResourceResolver instance = new StaticResourceResolver();
    private static final Logger logger = LoggerFactory.getLogger(StaticResourceResolver.class);

    private static final Pattern EXT_PATTERN = Pattern.compile("(\\.\\w+)$");

    private StaticResourceResolver() {}

    public static StaticResourceResolver getInstance() {
        return instance;
    }

    @Override
    public byte[] resolve(HTTPRequest request) {
        String url = request.getUri();
        url = url.startsWith("/") ? url.replaceFirst("/", "static/"): url;
        try {
            Optional<byte []> acceptTypedFile = tryAcceptTypedFile(request, url);
            if (acceptTypedFile.isPresent()) {
                return acceptTypedFile.get();
            }
            Optional<byte[]> namedFile = loadResourceAsBytes(url);
            return namedFile.orElseGet("NOT FOUND"::getBytes);
        } catch (IOException ioException) {
            logger.error(ioException.getMessage());
            return "INTERNAL SERVER ERROR".getBytes();
        }
    }

    private Optional<byte []> tryAcceptTypedFile(HTTPRequest request, String url) throws IOException {
        Optional<AcceptRecord[]> optionalHeader = request.getHeader("accept", AcceptRecord[].class);
        if (optionalHeader.isEmpty()) {
            return Optional.empty();
        }
        AcceptRecord[] acceptHeaders = optionalHeader.get();
        Matcher matcher = EXT_PATTERN.matcher(url);
        for (AcceptRecord acceptHeader : acceptHeaders) {
            Optional<byte[]> filePath = tryFindAcceptType(acceptHeader.type(), matcher);
            if (filePath.isEmpty()) {
                continue;
            }
            return filePath;
        }
        return Optional.empty();
    }

    private Optional<byte []> tryFindAcceptType(HTTPContentType contentType, Matcher matcher) throws IOException {
        String acceptUrl = matcher.replaceFirst(contentType.detail);
        Optional<byte[]> filePath = loadResourceAsBytes(acceptUrl);
        if (filePath.isPresent()) {
            return filePath;
        }
        for (String alias : contentType.alias) {
            acceptUrl = matcher.replaceFirst(alias);
            filePath = loadResourceAsBytes(acceptUrl);
            if (filePath.isPresent()) {
                return filePath;
            }
        }
        return Optional.empty();
    }

    private static Optional<byte[]> loadResourceAsBytes(String acceptUrl) throws IOException {
        URL resource = classLoader.getResource(acceptUrl);
        if (resource == null) {
            return Optional.empty();
        }
        String filePath = resource.getFile();
        return Optional.of(Files.readAllBytes(new File(filePath).toPath()));
    }
}

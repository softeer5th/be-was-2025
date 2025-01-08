package resolver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.RequestHandler;
import webserver.enumeration.HTTPContentType;
import webserver.enumeration.HTTPStatusCode;
import webserver.message.HTTPRequest;
import webserver.message.HTTPResponse;
import webserver.message.header.records.AcceptRecord;

import javax.swing.text.html.Option;
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
    public void resolve(HTTPRequest request, HTTPResponse.Builder response) {
        try {
            Optional<byte []> optionalFile = tryFindFile(request, response);
            if (optionalFile.isEmpty()) {
                response.statusCode(HTTPStatusCode.NOT_FOUND);
                return;
            }
            response.body(optionalFile.get());
            response.statusCode(HTTPStatusCode.OK);
        } catch (IOException ioException) {
            logger.error(ioException.getMessage());
            response.statusCode(HTTPStatusCode.SERVER_ERROR);
        }
    }

    private Optional<byte []> tryFindFile(HTTPRequest request, HTTPResponse.Builder response) throws IOException {
        String url = request.getUri();
        url = url.startsWith("/") ? url.replaceFirst("/", "static/"): url;
        Optional<byte []> acceptTypedFile = tryAcceptTypedFile(request, response, url);
        if (acceptTypedFile.isPresent()) {
            return acceptTypedFile;
        }
        Optional<byte[]> namedFile = loadResourceAsBytes(url);
        if (namedFile.isPresent()) {
            response.contentTypeFromUri(url);
            return namedFile;
        }
        return Optional.empty();
    }

    private Optional<byte []> tryAcceptTypedFile(HTTPRequest request, HTTPResponse.Builder response, String url)
            throws IOException {
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
            response.contentType(acceptHeader.type());
            return filePath;
        }
        return Optional.empty();
    }

    private Optional<byte []> tryFindAcceptType(HTTPContentType contentType, Matcher matcher)
            throws IOException {
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

package webserver.resolver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.RequestHandler;
import webserver.enumeration.HTTPContentType;
import webserver.enumeration.HTTPStatusCode;
import webserver.exception.HTTPException;
import webserver.message.HTTPRequest;
import webserver.message.HTTPResponse;
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

    private static final Pattern EXTENSION_PATTERN = Pattern.compile("(\\.\\w+)?$");

    private StaticResourceResolver() {}

    public static StaticResourceResolver getInstance() {
        return instance;
    }

    @Override
    public void resolve(HTTPRequest request, HTTPResponse.Builder response) {
        try {
            String staticUrl = request.getUri().replaceFirst("/", "static/");
            Optional<byte []> optionalFile = tryFindFile(request, response, staticUrl);
            if (optionalFile.isPresent()) {
                response.body(optionalFile.get());
            } else {
                String index = staticUrl.endsWith("/") ? staticUrl + "index.html" : staticUrl + "/index.html";
                Optional<byte []> directoryIndex = tryFindFile(request, response, index);
                if (directoryIndex.isEmpty()) {
                    response.statusCode(HTTPStatusCode.NOT_FOUND);
                    return;
                }
                response.body(directoryIndex.get());
            }
            response.statusCode(HTTPStatusCode.OK);
        } catch (IOException ioException) {
            throw new HTTPException.Builder().causedBy(SequentialResolver.class)
                            .internalServerError(ioException.getMessage());
        }
    }

    private Optional<byte []> tryFindFile(HTTPRequest request, HTTPResponse.Builder response, String staticUrl)
            throws IOException {
        Optional<byte []> acceptTypedFile = tryAcceptTypedFile(request, response, staticUrl);
        if (acceptTypedFile.isPresent()) {
            return acceptTypedFile;
        }
        Optional<byte[]> namedFile = loadResourceAsBytes(staticUrl);
        if (namedFile.isPresent()) {
            response.contentTypeFromUri(staticUrl);
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
        Matcher matcher = EXTENSION_PATTERN.matcher(url);
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
        String acceptUrl = matcher.replaceFirst("." + contentType.detail);
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
        File file = new File(filePath);
        if (!file.exists() || !file.isFile()) {
            return Optional.empty();
        }
        return Optional.of(Files.readAllBytes(new File(filePath).toPath()));
    }
}

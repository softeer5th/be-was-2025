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
import webserver.reader.StaticFileReader;

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
        String url = request.getUri();
        Optional<byte[]> optionalFile = tryFindFile(request, response, url);
        if (optionalFile.isEmpty()) {
            String index = url.endsWith("/") ? url + "index.html" : url + "/index.html";
            optionalFile = tryFindFile(request, response, index);
        }
        if (optionalFile.isEmpty()) {
            throw new HTTPException.Builder()
                    .causedBy(StaticResourceResolver.class)
                    .notFound(request.getUri());
        }
        response.body(optionalFile.get());
        response.statusCode(HTTPStatusCode.OK);
    }

    private Optional<byte[]> tryFindFile(HTTPRequest request, HTTPResponse.Builder response, String url) {
        Optional<byte[]> acceptTypedFile = tryAcceptTypedFile(request, response, url);
        if (acceptTypedFile.isEmpty()) {
            acceptTypedFile = StaticFileReader.getStaticFileBytes(url);
            acceptTypedFile.ifPresent(staticFile -> response.contentTypeFromUri(url));
        }
        return acceptTypedFile;
    }

    private Optional<byte[]> tryAcceptTypedFile(HTTPRequest request, HTTPResponse.Builder response, String url) {
        Optional<AcceptRecord[]> optionalHeader = request.getHeader("accept", AcceptRecord[].class);
        if (optionalHeader.isEmpty()) {
            return Optional.empty();
        }
        AcceptRecord[] acceptHeaders = optionalHeader.get();
        Matcher matcher = EXTENSION_PATTERN.matcher(url);
        Optional<byte[]> optionalFile = Optional.empty();
        for (AcceptRecord acceptHeader : acceptHeaders) {
            optionalFile = tryFindAcceptType(acceptHeader.type(), matcher);
            if (optionalFile.isPresent()) {
                response.contentType(acceptHeader.type());
                break;
            }
        }
        return optionalFile;
    }

    private Optional<byte[]> tryFindAcceptType(HTTPContentType contentType, Matcher matcher) {
        String acceptUrl = matcher.replaceFirst("." + contentType.detail);
        Optional<byte[]> filePath = StaticFileReader.getStaticFileBytes(acceptUrl);
        if (filePath.isEmpty()) {
            for (String alias : contentType.alias) {
                acceptUrl = matcher.replaceFirst("." + alias);
                filePath = StaticFileReader.getStaticFileBytes(acceptUrl);
                if (filePath.isPresent()) {
                    break;
                }
            }
        }
        return filePath;
    }
}

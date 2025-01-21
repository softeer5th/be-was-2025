package webserver;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HeterogeneousContainer;
import webserver.enumeration.HTTPMethod;
import webserver.message.HTTPRequest;
import webserver.message.body.BodyParser;
import webserver.message.body.BodyParserFactory;
import webserver.message.header.CookieParser;
import webserver.message.header.HeaderParseManager;
import webserver.message.header.records.ContentTypeRecord;
import webserver.reader.ByteStreamReader;

import java.io.*;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class HTTPMessageParser {
    public static class ParseException extends RuntimeException {
        public ParseException(String message) {
            super(message);
        }
    }
    private static final Pattern PARAMETER_PATTERN = Pattern.compile("(?<key>([^=&]*))=(?<value>([^&]*))");
    private static final HTTPMessageParser instance = new HTTPMessageParser();
    private final static Logger logger = LoggerFactory.getLogger(HTTPMessageParser.class);

    private HTTPMessageParser() {}
    public static HTTPMessageParser getInstance() {
        return instance;
    }

    public HTTPRequest parse(InputStream inputStream) throws IOException {
        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
        ByteStreamReader reader = new ByteStreamReader(bufferedInputStream);
        StringBuilder sb = new StringBuilder();
        HTTPRequest.Builder builder = new HTTPRequest.Builder();
        Map<String, String> headers = new LinkedHashMap<>();
        parseFirstLine(reader, sb, builder);
        readHeader(reader, sb, headers);
        HeterogeneousContainer parsedHeaders = HeaderParseManager.getInstance().parse(headers);
        builder.setHeaders(parsedHeaders);
        Map<String, String> cookieHeaders = parseCookie(headers);
        builder.cookies(cookieHeaders);
        logger.debug("Request Header : {}", sb.toString());
        parseBody(bufferedInputStream, parsedHeaders, builder);
        return builder.build();
    }

    private Map<String, String> parseCookie(Map<String, String> headers) {
        if (!headers.containsKey("cookie")) {
            return new HashMap<>();
        }
        String cookie = headers.get("cookie");
        return CookieParser.parse(cookie);
    }

    private void parseBody(
            BufferedInputStream inputStream,
            HeterogeneousContainer headers,
            HTTPRequest.Builder request) throws IOException {
        String method = request.getMethod();
        switch (method) {
            case "POST":
            case "PUT":
            case "PATCH":
                ContentTypeRecord meta = headers.get("content-type", ContentTypeRecord.class)
                        .orElseThrow(() -> new ParseException("Content-type header not set"));
                BodyParser parser = BodyParserFactory.createFor(meta.contentType().detail);
                HeterogeneousContainer body = parser.parse(headers, inputStream);
                request.body(body);
        }
    }

    private String readLineWithLog(ByteStreamReader reader, StringBuilder sb) throws IOException {
        String str = reader.readLine();
        sb.append(str);
        sb.append("\n");
        return str;
    }

    private void parseFirstLine(ByteStreamReader reader, StringBuilder logBuilder, HTTPRequest.Builder requestBuilder)
            throws IOException {
        String str = readLineWithLog(reader, logBuilder);
        if (str == null || str.isBlank()) {
            throw new ParseException("First line is Empty.");
        }
        String [] splited = str.split(" ");
        if (splited.length != 3) {
            throw new ParseException("Not valid first line: " + str);
        }
        requestBuilder.method(splited[0]);
        parseUrl(requestBuilder, splited[1]);
        requestBuilder.version(splited[2]);
    }

    private void parseUrl(HTTPRequest.Builder requestBuilder, String url) throws UnsupportedEncodingException {
        String [] splited = url.split("\\?");
        requestBuilder.uri(splited[0]);
        if (splited.length > 1) {
            HeterogeneousContainer parameters = new HeterogeneousContainer(new LinkedHashMap<>());
            String [] params = splited[1].split("&");
            for (String param : params) {
                Matcher matcher = PARAMETER_PATTERN.matcher(param);
                if (matcher.find()) {
                    String key = URLDecoder.decode(matcher.group("key"), "UTF-8");
                    String value = URLDecoder.decode(matcher.group("value"), "UTF-8");
                    parameters.put(key, value);
                }
            }
            requestBuilder.setParameters(parameters);
        }
    }

    private void readHeader(ByteStreamReader reader, StringBuilder logBuilder, Map<String, String> headers)
            throws IOException {
        String line = readLineWithLog(reader, logBuilder);
        while (line != null && !line.isBlank()) {
            String [] splited = line.split(":", 2);
            if (splited.length != 2) {
                throw new ParseException("Not valid request header. : " + line);
            }
            splited[0] = splited[0].trim().toLowerCase();
            headers.put(splited[0], splited[1]);
            line = readLineWithLog(reader, logBuilder);
        }
    }
}

package webserver;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HeterogeneousContainer;
import webserver.message.HTTPRequest;
import webserver.message.header.HeaderParseManager;

import java.io.*;
import java.net.URLDecoder;
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
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder sb = new StringBuilder();
        HTTPRequest.Builder builder = new HTTPRequest.Builder();
        Map<String, String> headers = new LinkedHashMap<>();
        parseFirstLine(reader, sb, builder);
        readHeader(reader, sb, headers);
        HeterogeneousContainer parsedHeaders = HeaderParseManager.getInstance().parse(headers);
        builder.setHeaders(parsedHeaders);
        logger.debug("Request Header : {}", sb.toString());
        return builder.build();
    }

    private String readLineWithLog(BufferedReader reader, StringBuilder sb) throws IOException {
        String str = reader.readLine();
        sb.append(str);
        sb.append("\n");
        return str;
    }

    private void parseFirstLine(BufferedReader reader, StringBuilder logBuilder, HTTPRequest.Builder requestBuilder)
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
                    System.out.printf("%s = %s\n", key, value);
                }
            }
            requestBuilder.setParameters(parameters);
        }
    }

    private void readHeader(BufferedReader reader, StringBuilder logBuilder, Map<String, String> headers)
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

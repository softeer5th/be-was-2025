package webserver;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.message.HTTPRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.Map;


public class HTTPMessageParser {
    public static class ParseException extends RuntimeException {
        public ParseException(String message) {
            super(message);
        }
    }
    private static final HTTPMessageParser instance = new HTTPMessageParser();
    private final static Logger logger = LoggerFactory.getLogger(HTTPMessageParser.class);

    private HTTPMessageParser() {}
    public static HTTPMessageParser getInstance() {
        return instance;
    }

    public HTTPRequest parse(InputStream inputStream) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder sb = new StringBuilder();
        HTTPRequest.Builder builder = new HTTPRequest.Builder();
        Map<String, String> headers = new LinkedHashMap<>();
        try {
            parseFirstLine(reader, sb, builder);
            readHeader(reader, sb, headers);
        } catch (IOException ioException) {
            logger.error(ioException.getMessage());
        }
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
            throw new ParseException("Not valid request header.");
        }
        String [] splited = str.split(" ");
        if (splited.length != 3) {
            throw new ParseException("Not valid request header.");
        }
        requestBuilder.method(splited[0]);
        requestBuilder.uri(splited[1]);
        requestBuilder.version(splited[2]);
    }

    private void readHeader(BufferedReader reader, StringBuilder logBuilder, Map<String, String> headers)
            throws IOException {
        String line = readLineWithLog(reader, logBuilder);
        while (line != null && !line.isBlank()) {
            String [] splited = line.split(":");
            if (splited.length != 2) {
                throw new ParseException("Not valid request header.");
            }
            splited[0] = splited[0].trim().toLowerCase();
            headers.put(splited[0], splited[1]);
            line = readLineWithLog(reader, logBuilder);
        }
    }
}

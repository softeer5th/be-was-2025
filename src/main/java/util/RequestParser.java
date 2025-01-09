package util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.exception.InvalidRequestLineSyntaxException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class RequestParser {
    private static final Logger logger = LoggerFactory.getLogger(RequestParser.class);
    private static final RequestParser instance = new RequestParser();

    public RequestParser() {}

    public static RequestParser getInstance() {
        return instance;
    }

    public HttpRequest parse(InputStream in) throws IOException, InvalidRequestLineSyntaxException{
        List<String> headers = logAndReturnHeaders(in);

        String[] requestLine = resolveRequestLine(headers.get(0));

        return new HttpRequest(requestLine[0], requestLine[1], requestLine[2], headers.subList(1, headers.size()));
    }

    private List<String> logAndReturnHeaders(InputStream in) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));

        List<String> headers = new ArrayList<>();

        String line = reader.readLine();

        while(!line.isEmpty()) {
            logger.debug(line);
            headers.add(line);
            line = reader.readLine();
        }

        return headers;
    }

    private String[] resolveRequestLine(String requestLine) {
        String[] tokens =  requestLine.split(" ");

        if (tokens.length != 3) {
            throw new InvalidRequestLineSyntaxException("");
        }

        return tokens;
    }
}
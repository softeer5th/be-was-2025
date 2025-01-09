package util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.exception.InvalidRequestLineSyntaxException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestParser {
    private static final Logger logger = LoggerFactory.getLogger(RequestParser.class);

    private List<String> headers;
    private String method;
    private String target;
    private final Map<String,String> queries = new HashMap<>();
    private String version;

    public RequestParser() {}

    public void parse(InputStream in) throws IOException, InvalidRequestLineSyntaxException{
        headers = logAndReturnHeaders(in);

        String[] requestLine = resolveRequestLine(headers.get(0));
        method = requestLine[0];
        version = requestLine[2];

        String[] requestTarget = resolveRequestTarget(requestLine[1]);

        target = requestTarget[0];
        if (requestTarget.length > 1) {
            String[] queryArray = resolveQuery(requestTarget[1]);
            for (String s : queryArray) {
                String[] items = s.split("=");
                String key = items[0];
                String value = items.length > 1 ? items[1] : null;
                queries.put(key, value);
            }
        }
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

    private String[] resolveRequestTarget(String target) {
        return target.split("\\?");
    }

    private String[] resolveQuery(String query) {
        return query.split("&");
    }

    public List<String> getHeaders() {
        return headers;
    }

    public String getMethod() {
        return method;
    }

    public String getTarget() {
        return target;
    }

    public Map<String, String> getQueries() {
        return queries;
    }

    public String getVersion() {
        return version;
    }
}
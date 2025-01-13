package util;

import http.HttpRequest;
import http.HttpRequestHandler;
import http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.exception.InvalidRequestLineSyntaxException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class RequestParser {
    private static final Logger logger = LoggerFactory.getLogger(RequestParser.class);

    public RequestParser() {
    }

    public void parse(InputStream in, DataOutputStream dos) throws IOException, InvalidRequestLineSyntaxException{
        List<String> headers = logAndReturnHeaders(in);

        String[] requestLine = resolveRequestLine(headers.get(0));

        HttpRequest httpRequest =  new HttpRequest(requestLine[0], requestLine[1], requestLine[2], headers.subList(1, headers.size()));
        HttpResponse httpResponse = new HttpResponse(dos);
        HttpRequestHandler requestHandler = new HttpRequestHandler();

        requestHandler.handleRequest(httpRequest, httpResponse);
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
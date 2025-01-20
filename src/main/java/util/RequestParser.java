package util;

import http.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.exception.InvalidRequestLineSyntaxException;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class RequestParser {
    private static final Logger logger = LoggerFactory.getLogger(RequestParser.class);

    public static HttpRequest parse(InputStream in) throws IOException {
        List<String> request = logAndReturnRequest(in);

        String[] requestLine = resolveRequestLine(request.get(0));

        return new HttpRequest(requestLine[0], requestLine[1], requestLine[2], request.subList(1, request.size()));
    }

    private static List<String> logAndReturnRequest(InputStream in) throws IOException {
        List<String> request = new ArrayList<>();

        DataInputStream dis = new DataInputStream(in);
        StringBuilder sb = new StringBuilder();
        int i;
        while((i = dis.read()) != -1) {
            char c = (char) i;
            sb.append(c);
            if (dis.available() == 0) break;
        }

        sb.trimToSize();
        String[] tokens = sb.toString().split("\r\n");
        for (String token : tokens) {
            logger.debug("request: {}", token);
            request.add(token);
        }
        return request;
    }

    private static String[] resolveRequestLine(String requestLine) {
        String[] tokens =  requestLine.split(" ");

        if (tokens.length != 3) {
            throw new InvalidRequestLineSyntaxException("");
        }

        return tokens;
    }
}

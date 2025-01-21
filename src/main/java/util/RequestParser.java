package util;

import http.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.exception.InvalidRequestLineSyntaxException;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public static Map<String, String> parseBody(HttpRequest request) throws UnsupportedEncodingException {
        Map<String, String> map = new HashMap<>();
        String body = new String(request.getBody());
        body = URLDecoder.decode(body, "utf-8");
        String[] tokens = body.split("&");
        for(String token: tokens) {
            String[] items = token.split("=");
            String key = items[0].trim();
            String value = items.length > 1 ? items[1].trim() : null;
            map.put(key, value);
        }
        return map;
    }
}

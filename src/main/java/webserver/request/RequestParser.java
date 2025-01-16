package webserver.request;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.cookie.Cookie;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class RequestParser {
    private static final Logger logger = LoggerFactory.getLogger(RequestParser.class);
    public static Request parse(InputStream in) throws IOException {
        Request request = new Request();
        List<String> headers = new ArrayList<>();
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String startLine = br.readLine();
        String line = br.readLine();
        while (!line.isEmpty()) {
            headers.add(line);
            line = br.readLine();
        }
        setStartLine(request, startLine);
        setHeaders(request, headers);
        setCookie(request);
        String contentLengthHeader = request.getHeader("CONTENT-LENGTH");


        if (contentLengthHeader != null) {
            int contentLength = Integer.parseInt(contentLengthHeader);
            char[] bodyChars = new char[contentLength];
            int read = br.read(bodyChars, 0, contentLength);
            if (read > 0) {
                String body = new String(bodyChars);
                request.setBody(body);
            }
        }

        return request;
    }

    private static void setStartLine(Request request, String line) {
        request.setRequestLine(line);
        String[] tokens = line.split(" ");
        request.setMethod(tokens[0]);
        request.setUrl(tokens[1]);
        String[] parts = tokens[1].split("\\?");
        if(parts.length > 1){
            request.setUrl(parts[0]);
            request.setParameter(parts[1]);
        }
        else{
            request.setContentType(parts[0]);
        }
    }

    private static void setHeaders(Request request, List<String> headers) {
        for(String header : headers){
            try {
                String[] tokens = header.split(":", 2);
                String key = tokens[0].trim().toUpperCase();
                String value = tokens[1].trim();
                request.addHeader(key, value);
            } catch (ArrayIndexOutOfBoundsException e) {logger.error("Invalid Header");}
        }
    }

    private static void setCookie(Request request){
        String cookieString = request.getHeader("COOKIE");
        if (cookieString != null) {
            Cookie cookie = new Cookie(cookieString);
            request.setCookie(cookie);
        }
    }
}

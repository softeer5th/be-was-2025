package request;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static util.Utils.*;

public class HTTPRequestParser {
    private static final Logger logger = LoggerFactory.getLogger(HTTPRequestParser.class);
    private final static HTTPRequestParser instance = new HTTPRequestParser();

    public static HTTPRequestParser getInstance() {
        return instance;
    }

    public HTTPRequest parse(InputStream in) {
        HTTPRequest request = new HTTPRequest();
        String[] readLine = parseRequest(in);

        if(!isValidHeader(readLine[0].split("\\s+")) || !isValidHttpMethod(readLine[0].split("\\s+")[0])){
            throw new IllegalArgumentException("Invalid HTTP request");
        }
        logHttpRequestHeader(readLine);
        parseRequestLine(readLine, request);
        return request;
    }

    private void logHttpRequestHeader(String[] httpRequestHeader){
        StringBuilder httpRequestLogMessage = new StringBuilder("HTTP Request Header:\n");
        for (String line : httpRequestHeader) {
            httpRequestLogMessage.append(line).append("\n");
        }
        logger.debug(httpRequestLogMessage.toString());
    }

    private String[] parseRequest(InputStream in) {
        String[] result = null;
        try {result = readInputToArray(in);}
        catch (IOException e) {
            logger.error(e.getMessage());
            throw new IllegalArgumentException("Invalid HTTP request");
        }
        return result;
    }

    private void parseRequestLine(String[] readLine, HTTPRequest request) {
        setRequestLine(readLine[0].split("\\s+"), request);
        setHeaders(readLine, request);
        setBodyParameters(readLine, request);
    }

    private void setHeaders(String[] readLine, HTTPRequest request) {
        Map<String, String> headers = new HashMap<>();
        int i = 1;

        while (i < readLine.length && !readLine[i].isEmpty()) {
            String[] headerParts = readLine[i].split(":\\s+", 2);
            if (headerParts.length == 2) {
                headers.put(headerParts[0], headerParts[1]);
            }
            i++;
        }
        request.setHeaders(headers);
    }

    private void setBodyParameters(String[] readLine, HTTPRequest request) {
        Map<String, String> bodyParameters = new HashMap<>();
        boolean isBodyStart = false;

        for (String line : readLine) {
            if (isBodyStart && !line.isEmpty()) {
                String[] keyValuePairs = line.split("&");
                for (String pair : keyValuePairs) {
                    String[] keyValue = pair.split("=");
                    if (keyValue.length == 2) {
                        bodyParameters.put(keyValue[0], keyValue[1]);
                    }
                }
                break;
            }
            if (line.isEmpty()) {
                isBodyStart = true;
            }
        }
        request.setBodyParameters(bodyParameters);
    }


    private void setRequestLine(String[] requestLine, HTTPRequest request) {
        request.setHttpMethod(requestLine[0]);
        String[] uriParts = requestLine[1].split("\\?");
        request.setUri(uriParts[0]);
        if(requestLine[1].split("&").length > 1){
            setQueryString(uriParts[1], request);
        }
        request.setHttpVersion(requestLine[2]);
    }

    private void setQueryString(String queryString, HTTPRequest request) {
        String[] queryParts = queryString.split("&");
        for (String part : queryParts) {
            String[] keyValue = part.split("=");
            if (keyValue.length == 2) {
                request.setQueryString(keyValue[0], keyValue[1]);
            }
        }
    }


}

package webserver.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.http.HttpRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class HttpRequestParser {
    private static final Logger logger = LoggerFactory.getLogger(HttpRequestParser.class);

    public static HttpRequest parse(InputStream inputStream) throws IOException {
        return parseRequest(inputStream);
    }

    private static HttpRequest parseRequest(InputStream inputStream) throws IOException {

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        String line;
        StringBuilder stringBuilder = new StringBuilder();

        while ((line = reader.readLine()) != null) {
            if (line.isEmpty()) break;

            stringBuilder.append(line).append("\r\n");
        }

        HttpRequest request = new HttpRequest();

        logger.debug(stringBuilder.toString());

        String requestString  = stringBuilder.toString();
        String[] lines = requestString.strip().split("\n");

        String method = lines[0].split(" ")[0].trim();
        String url = lines[0].split(" ")[1].trim();
        String[] urlElements = url.split("\\?");
        String path = urlElements[0].trim();

        if (urlElements.length > 1) {
           for(String queryString : urlElements[1].split("&")) {
               String[] queryElements = queryString.split("=");
               String key = URLDecoder.decode(queryElements[0].trim(), StandardCharsets.UTF_8);
               String value = URLDecoder.decode(queryElements[1].trim(), StandardCharsets.UTF_8);
               request.setQuery(key, value);
           }
        }

        String version = lines[0].split(" ")[2].trim();

        request.setMethod(method);
        request.setPath(path);
        request.setVersion(version);


        for (int i = 2 ; i < lines.length; i++) {
            String[] elements = lines[i].split(":");
            String name = elements[0].trim();
            String value = elements[1].trim();
            request.setHeader(name, value);
        }

        return request;
    }

}

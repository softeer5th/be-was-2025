package util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;

public class HttpRequestParser {
    private static final Logger logger = LoggerFactory.getLogger(HttpRequestParser.class);

    private HttpRequestParser() {}

    public static String parseRequest(BufferedReader reader) throws IOException {
        String startLine = reader.readLine(); // HTTP Request의 첫 줄을 읽고 출력
        logger.debug(startLine);

        String line;
        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            logger.debug(line);
        }
        return startLine;
    }
}

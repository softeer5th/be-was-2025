package util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;

public abstract class RequestParser {
    private static final Logger logger = LoggerFactory.getLogger(RequestParser.class);

    public static RequestInfo parse(InputStream inputStream) throws IOException {
        BufferedReader br = new BufferedReader(new java.io.InputStreamReader(inputStream));

        String requests = br.readLine();
        // http method와 path를 파싱한다.
        String[] requestInfo = requests.split(" ");

        HttpMethod method = HttpMethod.match(requestInfo[0]);
        String url = requestInfo[1];
        logger.debug("Request mehtod = {}, url = {}", method, url);

        // request의 내용을 로깅한다.
        while (!(requests = br.readLine()).isEmpty()) {
            String[] requestContents = requests.split(":");
            logger.debug("{} = {}", requestContents[0], requestContents[1]);
        }

        return new RequestInfo(method, url);
    }
}

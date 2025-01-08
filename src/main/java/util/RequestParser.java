package util;

import enums.HttpMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import request.RequestInfo;

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
        if(requestInfo.length != 3){
            throw new IllegalArgumentException("Invalid request format");
        }

        HttpMethod method = HttpMethod.match(requestInfo[0]);
        String url = requestInfo[1];
        logger.debug("Request mehtod = {}, url = {}", method, url);

        // request의 내용을 로깅한다.
        while (!(requests = br.readLine()).isEmpty()) {
            String[] nameAndValue = requests.split(":", 2);
            logger.debug("{} = {}", nameAndValue[0], nameAndValue[1]);
        }

        return new RequestInfo(method, url);
    }
}

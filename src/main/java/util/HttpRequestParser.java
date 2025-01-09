package util;

import enums.HttpMethod;
import exception.ClientErrorException;
import exception.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import request.HttpRequestInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;

public abstract class HttpRequestParser {
    private static final Logger logger = LoggerFactory.getLogger(HttpRequestParser.class);
    private static final String REQUEST_LINE_SEPARATOR = " ";

    public static HttpRequestInfo parse(InputStream inputStream) throws IOException {
        BufferedReader br = new BufferedReader(new java.io.InputStreamReader(inputStream));

        String requests = br.readLine();

        // 1. 요청이 빈 줄인 경우, 이를 무시하고 다시 읽어들임
        while (requests != null && requests.trim().isEmpty()) {
            requests = br.readLine();
        }

        // 2. 요청이 null이거나 잘못된 형식일 경우 예외 처리
        if (requests == null || requests.trim().isEmpty()) {
            throw new ClientErrorException(ErrorCode.INVALID_HTTP_REQUEST);
        }

        // 3. HTTP 메서드와 URL을 파싱

        String[] requestInfo = requests.toLowerCase().split(REQUEST_LINE_SEPARATOR);

        if (requestInfo.length != 3) {
            throw new ClientErrorException(ErrorCode.INVALID_HTTP_REQUEST);
        }

        HttpMethod method = HttpMethod.match(requestInfo[0]);
        String url = requestInfo[1];
        logger.debug("Request mehtod = {}, url = {}", method, url);

        // request의 내용을 로깅한다.
        while (!(requests = br.readLine()).isEmpty()) {
            String[] nameAndValue = requests.split(":", 2);
            logger.debug("{} = {}", nameAndValue[0], nameAndValue[1]);
        }

        return new HttpRequestInfo(method, url);
    }
}

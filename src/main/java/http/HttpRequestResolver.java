package http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;

public class HttpRequestResolver {
    private static final Logger logger = LoggerFactory.getLogger(HttpRequestResolver.class);
    private static final HttpRequestResolver INSTANCE = new HttpRequestResolver();

    public static HttpRequestResolver getInstance(){
        return INSTANCE;
    }

    public HttpRequest parseHttpRequest(BufferedReader br) throws IOException {
        HttpRequest httpRequest = new HttpRequest();

        String line = br.readLine();
        logger.debug("request line: {}", line);

        parseHttpRequestLine(line, httpRequest);

        // 요청 헤더 읽기
        while((line = br.readLine()) != null){
            if(line.isBlank()){
                break;
            }
            logger.debug("header: {}", line);
        }

        return httpRequest;
    }

    private void parseHttpRequestLine(String requestLine, HttpRequest httpRequest){
        String[] requestLineParts = requestLine.split(" ");

        httpRequest.setMethod(requestLineParts[0]);
        httpRequest.setPath(requestLineParts[1]);
        httpRequest.setProtocol(requestLineParts[2]);
    }
}

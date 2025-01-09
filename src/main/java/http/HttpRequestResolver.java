package http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

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
        String[] urlParts = requestLineParts[1].split("\\?");

        httpRequest.setMethod(requestLineParts[0]);
        httpRequest.setPath(urlParts[0]);
        httpRequest.setProtocol(requestLineParts[2]);

        parseQueryParameter(urlParts[1], httpRequest);
    }

    private void parseQueryParameter(String queryString, HttpRequest httpRequest){
        String[] queryParams = queryString.split("&");

        for (String queryParam : queryParams){
            String[] queryParamPart = queryParam.split("=");
            // 한글이 전달되는 경우 url 인코딩 값이 넘어온다. 디코딩을 해서 저장한다.
            httpRequest.putQueryParam(queryParamPart[0], URLDecoder.decode(queryParamPart[1], StandardCharsets.UTF_8));
        }
    }
}

package http;

import http.enums.HttpMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class HttpRequestResolver {
    private static final Logger logger = LoggerFactory.getLogger(HttpRequestResolver.class);
    private static final HttpRequestResolver INSTANCE = new HttpRequestResolver();

    public static HttpRequestResolver getInstance(){
        return INSTANCE;
    }

    public HttpRequest parseHttpRequest(BufferedReader br) throws IOException {
        HttpRequest httpRequest = new HttpRequest();

        parseHttpRequestLine(br, httpRequest);

        parseHttpRequestHeader(br, httpRequest);

        parseHttpRequestBody(br, httpRequest);

        return httpRequest;
    }

    private void parseHttpRequestLine(BufferedReader br, HttpRequest httpRequest) throws IOException {
        String requestLine = br.readLine();

        if(requestLine == null || requestLine.isEmpty()){
            throw new IOException("request line is Empty");
        }
        logger.debug("request line: {}", requestLine);

        String[] requestLineParts = requestLine.split("\\s+");
        String[] urlParts = requestLineParts[1].split("\\?");

        httpRequest.setMethod(requestLineParts[0]);
        httpRequest.setPath(urlParts[0]);
        httpRequest.setProtocol(requestLineParts[2]);

        if(urlParts.length == 2){
            parseQueryParameter(urlParts[1], httpRequest);
        }
    }

    private void parseQueryParameter(String queryString, HttpRequest httpRequest){
        String[] queryParams = queryString.split("&");

        for (String queryParam : queryParams){
            String[] queryParamPart = queryParam.split("=");
            // 한글이 전달되는 경우 url 인코딩 값이 넘어온다. 디코딩을 해서 저장한다.
            httpRequest.addQueryParam(queryParamPart[0], URLDecoder.decode(queryParamPart[1], StandardCharsets.UTF_8));
        }
    }

    private void parseHttpRequestHeader(BufferedReader br, HttpRequest httpRequest) throws IOException {
        StringBuilder sb = new StringBuilder();
        String line;

        while((line = br.readLine()) != null && !line.isEmpty()){
            sb.append(line).append("\n");
            String[] lineParts = line.split(":\\s*");
            httpRequest.addHeader(lineParts[0].toUpperCase(), lineParts[1].toUpperCase());
        }

        logger.debug("header: \n{}", sb.toString());
    }

    private void parseHttpRequestBody(BufferedReader br, HttpRequest httpRequest) throws IOException {
        String contentLength = httpRequest.getHeader("CONTENT-LENGTH");

        if(contentLength != null){
            char[] buf = new char[Integer.parseInt(contentLength)];
            br.read(buf, 0, buf.length);

            // URL 디코딩 처리
            String decodedBody = URLDecoder.decode(new String(buf), StandardCharsets.UTF_8);

            logger.debug("body: \n{}", decodedBody);
            httpRequest.setBody(decodedBody.toCharArray());
        }
    }
}

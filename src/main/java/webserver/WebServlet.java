package webserver;

import handler.RequestHandler;
import handler.mapper.RequestHandlerMapper;
import http.HttpRequestResolver;
import http.HttpResponseResolver;
import http.enums.HttpMethod;
import http.HttpRequest;
import http.enums.HttpStatus;
import util.FileUtil;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class WebServlet {
    private static final WebServlet INSTANCE = new WebServlet();
    private final RequestHandlerMapper requestHandlerMapper;
    private final HttpRequestResolver httpRequestResolver = HttpRequestResolver.getInstance();
    private final HttpResponseResolver httpResponseResolver = HttpResponseResolver.getInstance();

    public static WebServlet getInstance(){
        return INSTANCE;
    }

    public WebServlet(){
        requestHandlerMapper = new RequestHandlerMapper();
    }

    public void process(InputStream is, OutputStream os) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        DataOutputStream dos = new DataOutputStream(os);

        HttpRequest httpRequest = httpRequestResolver.parseHttpRequest(br);

        File resultFile = FileUtil.findFileByPath(httpRequest.getPath());

        if(resultFile != null){
            byte[] fileData = FileUtil.readFileToByteArray(resultFile);
            httpResponseResolver.sendResponse(dos, HttpStatus.OK, resultFile.getPath(), fileData);
            return;
        }
        // 경로에서 api 제거
        String parsedPath = removeApiPrefixFromPath(httpRequest.getPath());

        if(requestHandlerMapper.hasRequestHandlerByPath(parsedPath)){
            RequestHandler requestHandler = requestHandlerMapper.mapRequestHandler(parsedPath);
            requestHandler.handle(httpRequest);
            httpResponseResolver.sendRedirectResponse(dos, HttpStatus.MOVED_PERMANENTLY, "http://localhost:8080/");
            return;
        }

        httpResponseResolver.sendResponse(dos, HttpStatus.NOT_FOUND, httpRequest.getPath(), "Request Not Found".getBytes());
    }

    // 경로에서 api 제거
    private String removeApiPrefixFromPath(String path){
        return path.substring(4);
    }



}

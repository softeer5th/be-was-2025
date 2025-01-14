package webserver;

import exception.NotExistApiRequestException;
import handler.FileRequestHandler;
import handler.RequestHandler;
import handler.UserRequestHandler;
import http.HttpRequestResolver;
import http.HttpResponse;
import http.HttpResponseResolver;
import http.HttpRequest;
import http.enums.HttpStatus;
import http.enums.MimeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class WebServlet {
    private static final Logger logger = LoggerFactory.getLogger(WebServlet.class);
    private static final WebServlet INSTANCE = new WebServlet();
    private final Map<String, RequestHandler> apiRequestHandlerMap;
    private final FileRequestHandler fileRequestHandler;
    private final HttpRequestResolver httpRequestResolver = HttpRequestResolver.getInstance();
    private final HttpResponseResolver httpResponseResolver = HttpResponseResolver.getInstance();

    public static WebServlet getInstance(){
        return INSTANCE;
    }

    public WebServlet(){
        apiRequestHandlerMap = new HashMap<>();
        apiRequestHandlerMap.put("/create", new UserRequestHandler());
        fileRequestHandler = new FileRequestHandler();
    }

    public void process(InputStream is, OutputStream os) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        DataOutputStream dos = new DataOutputStream(os);

        HttpRequest httpRequest = httpRequestResolver.parseHttpRequest(br);

        try {
            if (fileRequestHandler.canHandle(httpRequest)) {
                HttpResponse httpResponse = fileRequestHandler.handle(httpRequest);
                httpResponseResolver.sendResponse(dos, httpResponse);
                return;
            }

            if (apiRequestHandlerMap.containsKey(httpRequest.getPath())) {
                RequestHandler requestHandler = apiRequestHandlerMap.get(httpRequest.getPath());

                if (requestHandler.canHandle(httpRequest)) {
                    HttpResponse httpResponse = requestHandler.handle(httpRequest);
                    httpResponseResolver.sendResponse(dos, httpResponse);
                }
            }
        }catch(NotExistApiRequestException e){
            logger.error(e.getMessage());
            byte[] errorData = "Request Not Found".getBytes();
            httpResponseResolver.sendResponse(dos,
                    new HttpResponse.Builder()
                            .httpStatus(HttpStatus.NOT_FOUND)
                            .contentType(MimeType.TEXT_PLAIN.getMimeType())
                            .body(errorData)
                            .build()
            );
        }
    }

}

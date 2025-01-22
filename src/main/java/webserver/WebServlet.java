package webserver;

import exception.NotFoundRequestHandlerException;
import handler.request.FileRequestHandler;
import handler.mapping.RequestHandlerMapping;
import http.request.HttpRequestResolver;
import http.response.HttpResponse;
import http.response.HttpResponseResolver;
import http.request.HttpRequest;
import http.enums.HttpStatus;
import http.enums.MimeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class WebServlet {
    private static final Logger logger = LoggerFactory.getLogger(WebServlet.class);
    private static final WebServlet INSTANCE = new WebServlet();
    private final RequestHandlerMapping apiRequestHandlerMapping;
    private final FileRequestHandler fileRequestHandler;
    private final HttpRequestResolver httpRequestResolver = HttpRequestResolver.getInstance();
    private final HttpResponseResolver httpResponseResolver = HttpResponseResolver.getInstance();

    public static WebServlet getInstance(){
        return INSTANCE;
    }

    public WebServlet(){
        apiRequestHandlerMapping = new RequestHandlerMapping();
        apiRequestHandlerMapping.init();
        fileRequestHandler = new FileRequestHandler();
    }

    public void process(InputStream is, OutputStream os) throws IOException {
        DataOutputStream dos = new DataOutputStream(os);

        HttpRequest httpRequest = httpRequestResolver.parseHttpRequest(is);

        try {
            HttpResponse httpResponse = apiRequestHandlerMapping.getHandler(httpRequest)
                    // api request handler가 먼저 존재하는지 확인한다
                    .map(apiRequestHandler -> apiRequestHandler.handle(httpRequest))
                    // 파일 request handler 로 처리
                    .orElseGet(() -> {
                        if (fileRequestHandler.canHandle(httpRequest)) {
                            return fileRequestHandler.handle(httpRequest);
                        }
                        throw new NotFoundRequestHandlerException("적절한 요청 핸들러가 없습니다.");
                    });

            httpResponseResolver.sendResponse(dos, httpResponse);
        }catch(NotFoundRequestHandlerException e){
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

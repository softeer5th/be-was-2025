package webserver;

import java.io.*;
import java.net.Socket;

import Response.HTTPResponse;
import Response.HTTPResponseHandler;
import constant.HTTPCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import request.HTTPRequest;
import request.HTTPRequestParser;

import static util.Utils.flushResponse;


public class RequestHandler implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);
    public static final HTTPResponseHandler httpResponseHandler = new HTTPResponseHandler();
    private static final DynamicURIHandler dynamicURIHandler = new DynamicURIHandler();
    private static final StaticURIHandler staticURIHandler = new StaticURIHandler();
    private static final HTTPRequestParser httpRequestParser = HTTPRequestParser.getInstance();

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

        public void run() {
            logger.debug("New Client Connect! Connected IP : {}, Port : {}, Thread : {}", connection.getInetAddress(),
                    connection.getPort(), Thread.currentThread().getId());

            try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
                DataOutputStream dos = new DataOutputStream(out);
                HTTPRequest httpRequest = httpRequestParser.parse(in);
                HTTPResponse httpResponse;

                if(dynamicURIHandler.supports(httpRequest)){
                    logger.debug("Can handle dynamic URI request : " + httpRequest.getUri());
                    httpResponse = dynamicURIHandler.handle(httpRequest);
                }

                else if(staticURIHandler.supports(httpRequest)){
                    logger.debug("Can handle static URI request : " + httpRequest.getUri());
                    httpResponse = staticURIHandler.handle(httpRequest);
                }
                else{
                    httpResponse = HTTPResponse.createFailResponse(httpRequest.getHttpVersion(), HTTPCode.NOT_FOUND);
                }

                flushResponse(httpResponse, dos);

            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        }


}

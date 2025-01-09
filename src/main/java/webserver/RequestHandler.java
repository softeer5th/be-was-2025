package webserver;

import java.io.*;
import java.net.Socket;
import java.util.Set;

import Response.HTTPResponseHandler;
import constant.HTTPCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static util.Utils.isValidHttpMethod;
import static util.Utils.readInputToArray;

public class RequestHandler implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);
    public static final HTTPResponseHandler httpResponseHandler = new HTTPResponseHandler();
    private static final URIHandler uriHandler = new URIHandler();

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

        public void run() {
            logger.debug("New Client Connect! Connected IP : {}, Port : {}, Thread : {}", connection.getInetAddress(),
                    connection.getPort(), Thread.currentThread().getId());

            try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
                DataOutputStream dos = new DataOutputStream(out);

                String [] httpRequestHeader = readInputToArray(in);
                logHttpRequestHeader(httpRequestHeader);

                String httpMethod = httpRequestHeader[0].split(" ")[0];
                String resourceName = httpRequestHeader[0].split(" ")[1];

                if(isValidHttpMethod(httpMethod)) {
                    httpResponseHandler.responseFailHandler(dos, HTTPCode.METHOD_NOT_ALLOWED);
                    return;
                }

                if(uriHandler.handleDynamicRequest(httpMethod, resourceName, dos)){
                    return;
                }

                if(uriHandler.handleStaticRequest(resourceName, dos)){
                    return;
                }

                httpResponseHandler.responseFailHandler(dos, HTTPCode.NOT_FOUND);

            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        }

    private void logHttpRequestHeader(String[] httpRequestHeader){
        StringBuilder httpRequestLogMessage = new StringBuilder("HTTP Request Header:\n");
        for (String line : httpRequestHeader) {
            httpRequestLogMessage.append(line).append("\n");
        }
        logger.debug(httpRequestLogMessage.toString());
    }

}

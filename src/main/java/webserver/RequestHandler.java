package webserver;

import java.io.*;
import java.net.Socket;

import Response.HTTPResponseHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static util.Utils.*;

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

                if(!isValidHeader(httpRequestHeader[0].split("\\s+"), dos)){
                    return;
                }

                String httpMethod = httpRequestHeader[0].split("\\s+")[0];
                String resourceName = httpRequestHeader[0].split("\\s+")[1];

                if(!isValidHttpMethod(httpMethod, dos)) {
                    return;
                }

                if(uriHandler.handleDynamicRequest(httpMethod, resourceName, dos)){
                    return;
                }

                uriHandler.handleStaticRequest(resourceName, dos);

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

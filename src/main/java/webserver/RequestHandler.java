package webserver;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import Response.HTTPResponseHandler;
import constant.HTTPCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static util.Utils.fileToByteArray;
import static util.Utils.readInputToArray;

public class RequestHandler implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);
    private final String STATIC_FILE_DIRECTORY_PATH = "src/main/resources/static";
    private static final HTTPResponseHandler httpResponseHandler = new HTTPResponseHandler();

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

                Set<String> httpMethods = Set.of("GET", "POST", "PUT", "DELETE", "HEAD", "OPTIONS", "TRACE", "CONNECT", "PATCH");

                if(!httpMethods.contains(httpMethod)) {
                    httpResponseHandler.responseFailHandler(dos, HTTPCode.METHOD_NOT_ALLOWED);
                    return;
                }


                File file = new File(STATIC_FILE_DIRECTORY_PATH, resourceName);
                if (!file.exists()) {
                    httpResponseHandler.responseFailHandler(dos, HTTPCode.NOT_FOUND);
                }
                byte[] body = fileToByteArray(file);
                httpResponseHandler.responseSuccessHandler(dos, body.length, resourceName, body);

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

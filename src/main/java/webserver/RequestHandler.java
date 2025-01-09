package webserver;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import resolver.ResourceResolver;
import resolver.StaticResourceResolver;
import webserver.message.HTTPRequest;
import webserver.message.HTTPResponse;

public class RequestHandler implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        logger.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            HTTPMessageParser parser = HTTPMessageParser.getInstance();
            HTTPRequest request = parser.parse(in);
            HTTPResponse.Builder responseBuilder = new HTTPResponse.Builder();
            DataOutputStream dos = new DataOutputStream(out);
            ResourceResolver resolver = StaticResourceResolver.getInstance();
            resolver.resolve(request, responseBuilder);
            HTTPResponse response = responseBuilder.build();
            ResponseWriter.write(dos, request, response);
            dos.flush();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
}

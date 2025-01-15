package webserver;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.HTTPMessageParser.ParseException;
import webserver.enumeration.HTTPStatusCode;
import webserver.exception.HTTPException;
import webserver.resolver.ResourceResolver;
import webserver.message.HTTPRequest;
import webserver.message.HTTPResponse;

public class RequestHandler implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;
    private ResourceResolver resolver;

    public RequestHandler(Socket connectionSocket, ResourceResolver resolver) {
        this.connection = connectionSocket;
        this.resolver = resolver;
    }

    public void run() {
        logger.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            HTTPMessageParser parser = HTTPMessageParser.getInstance();
            HTTPRequest request = parser.parse(in);
            HTTPResponse.Builder responseBuilder = new HTTPResponse.Builder();
            DataOutputStream dos = new DataOutputStream(out);
            resolver.resolve(request, responseBuilder);
            HTTPResponse response = responseBuilder.build();
            ResponseWriter.write(dos, request, response);
            dos.flush();
        } catch (IOException e) {
            throw new HTTPException.Builder().causedBy(RequestHandler.class)
                    .internalServerError(e.getMessage());
        } catch (ParseException e) {
            throw new HTTPException.Builder().causedBy(HTTPMessageParser.class)
                    .badRequest(e.getMessage());
        }
    }
}

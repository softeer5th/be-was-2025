package util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.exception.InvalidRequestLineSyntaxException;

import java.io.*;

public class HttpRequestHandler {
    private static final Logger logger = LoggerFactory.getLogger(HttpRequestHandler.class);
    private final DataOutputStream dos;

    public HttpRequestHandler(DataOutputStream dos) {
        this.dos = dos;
    }

    public void handleRequest(InputStream in) {
        try {
            HttpRequest httpRequest = RequestParser.getInstance().parse(in);

            String target = httpRequest.getTarget();

            File file = FileUtils.findFile(target);

            byte[] body = createBody(file);

            String extension = file.getName().substring(file.getName().lastIndexOf(".") + 1);

            String mimeType = MimeType.valueOf(extension.toUpperCase()).getMimeType();

            HttpResponseHandler.responseHeader(dos, body.length, mimeType, HttpStatus.OK);
            HttpResponseHandler.responseBody(dos, body);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }   catch (InvalidRequestLineSyntaxException e) {
            byte[] body = e.getMessage().getBytes();
            HttpResponseHandler.responseHeader(dos, body.length, "text/plain", e.httpStatus);
            HttpResponseHandler.responseBody(dos, body);
        }
    }

    private byte[] createBody(File file) throws IOException {
        InputStream is = new FileInputStream(file);
        byte[] body = is.readAllBytes();
        is.close();

        return body;
    }
}

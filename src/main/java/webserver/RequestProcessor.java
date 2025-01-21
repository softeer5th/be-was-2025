package webserver;

import http.*;
import http.constant.HttpHeader;
import http.constant.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.MimeType;
import util.RequestParser;
import util.exception.InvalidRequestLineSyntaxException;

import java.io.*;

public class RequestProcessor {
    private static final Logger logger = LoggerFactory.getLogger(RequestProcessor.class);
    public RequestProcessor() {
    }

    public void process(InputStream in, DataOutputStream dos) throws IOException {
        try{
            HttpRequest httpRequest = RequestParser.parse(in);

            HttpResponse httpResponse = new HttpResponse(dos);

            HttpRequestDispatcher httpRequestDispatcher = new HttpRequestDispatcher(httpRequest, httpResponse);
            httpRequestDispatcher.dispatch();
        } catch (InvalidRequestLineSyntaxException e) {
            errorResponse(dos, HttpStatus.BAD_REQUEST, e);
        } catch (IOException e) {
            errorResponse(dos, HttpStatus.INTERNAL_SERVER_ERROR, e);
        }
    }



    private void errorResponse(DataOutputStream dos, HttpStatus httpStatus, Exception e) throws IOException {
        logger.error(e.getMessage());
        byte[] body = e.getMessage().getBytes();
        dos.writeBytes(String.format("%s %d %s", HttpHeader.PROTOCOL.value(), httpStatus.getStatusCode(), httpStatus.getReasonPhrase()));
        dos.writeBytes(String.format("%s: %s", HttpHeader.CONTENT_TYPE.value(), MimeType.TXT.getMimeType()));
        dos.writeBytes(String.format("%s: %d", HttpHeader.CONTENT_LENGTH, body.length));
        dos.writeBytes("\r\n");
        dos.write(body, 0, body.length);
        dos.flush();
    }
}
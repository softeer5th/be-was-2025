package webserver;

import http.*;
import http.constant.HttpHeader;
import http.constant.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.MimeType;
import util.exception.InvalidRequestLineSyntaxException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class RequestParser {
    private static final Logger logger = LoggerFactory.getLogger(RequestParser.class);

    public RequestParser() {
    }

    public void parse(InputStream in, DataOutputStream dos) throws IOException {
        try{
            List<String> request = logAndReturnRequest(in);

            String[] requestLine = resolveRequestLine(request.get(0));

            HttpRequest httpRequest =  new HttpRequest(requestLine[0], requestLine[1], requestLine[2], request.subList(1, request.size()));
            HttpResponse httpResponse = new HttpResponse(dos);

            HttpRequestDispatcher httpRequestDispatcher = new HttpRequestDispatcher(httpRequest, httpResponse);
            httpRequestDispatcher.dispatch();
        } catch (InvalidRequestLineSyntaxException e) {
            errorResponse(dos, HttpStatus.BAD_REQUEST, e);
        } catch (IOException e) {
            errorResponse(dos, HttpStatus.INTERNAL_SERVER_ERROR, e);
        }
    }

    private List<String> logAndReturnRequest(InputStream in) throws IOException {
        List<String> request = new ArrayList<>();

        DataInputStream dis = new DataInputStream(in);
        StringBuilder sb = new StringBuilder();
        int i;
        while((i = dis.read()) != -1) {
            char c = (char) i;
            sb.append(c);
            if (dis.available() == 0) break;
        }

        sb.trimToSize();
        String[] tokens = sb.toString().split("\r\n");
        for (String token : tokens) {
            logger.debug("request: {}", token);
            request.add(token);
        }
        return request;
    }

    private String[] resolveRequestLine(String requestLine) {
        String[] tokens =  requestLine.split(" ");

        if (tokens.length != 3) {
            throw new InvalidRequestLineSyntaxException("");
        }

        return tokens;
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
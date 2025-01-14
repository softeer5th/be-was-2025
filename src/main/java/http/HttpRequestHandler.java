package http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.FileUtils;
import util.MimeType;
import util.PathPool;
import util.exception.InvalidRequestLineSyntaxException;
import util.exception.NoSuchPathException;
import util.exception.NotAllowedMethodException;

import java.io.*;
import java.lang.reflect.Method;

public class HttpRequestHandler {
    private static final Logger logger = LoggerFactory.getLogger(HttpRequestHandler.class);

    public HttpRequestHandler() {}

    public void handleRequest(HttpRequest httpRequest, HttpResponse httpResponse) {
        try {
            String path = httpRequest.getPath().toLowerCase();
            String method = httpRequest.getMethod().toLowerCase();

            if (PathPool.getInstance().isAvailable(method, path)) {
                Method classMethod = PathPool.getInstance().getMethod(method, path);
                classMethod.invoke(PathPool.getInstance().getClass(path), httpRequest, httpResponse);
                return;
            }

            File file = FileUtils.findFile(path);

            httpResponse.writeStatusLine(HttpStatus.OK);
            httpResponse.writeBody(file);
            httpResponse.send();
        } catch (IOException e) {
            logger.error(e.getMessage());
            byte[] body = e.getMessage().getBytes();
            httpResponse.writeStatusLine(HttpStatus.INTERNAL_SERVER_ERROR);
            httpResponse.writeBody(body, MimeType.TXT.getMimeType());
            httpResponse.send();
        } catch (InvalidRequestLineSyntaxException e) {
            byte[] body = e.getMessage().getBytes();
            httpResponse.writeStatusLine(e.httpStatus);
            httpResponse.writeBody(body, MimeType.TXT.getMimeType());
            httpResponse.send();
        } catch (NoSuchPathException e) {
            byte[] body = e.httpStatus.getReasonPhrase().getBytes();
            httpResponse.writeStatusLine(e.httpStatus);
            httpResponse.writeBody(body, MimeType.TXT.getMimeType());
            httpResponse.send();
        } catch (NotAllowedMethodException e) {
            byte[] body = e.httpStatus.getReasonPhrase().getBytes();
            httpResponse.writeStatusLine(e.httpStatus);
            httpResponse.writeBody(body, MimeType.TXT.getMimeType());
            httpResponse.send();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}

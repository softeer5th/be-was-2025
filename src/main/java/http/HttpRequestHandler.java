package http;

import db.Database;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.FileUtils;
import util.MimeType;
import util.PathPool;
import util.RequestParser;
import util.exception.InvalidRequestLineSyntaxException;
import util.exception.NoSuchPathException;

import java.io.*;
import java.util.Map;

public class HttpRequestHandler {
    private static final Logger logger = LoggerFactory.getLogger(HttpRequestHandler.class);
    private final DataOutputStream dos;

    public HttpRequestHandler(DataOutputStream dos) {
        this.dos = dos;
    }

    public void handleRequest(InputStream in) {
        try {
            HttpRequest httpRequest = RequestParser.getInstance().parse(in);

            String path = httpRequest.getPath();

            File file = FileUtils.findFile(path);

            if (!file.exists()) {
                if (PathPool.getInstance().get(path) == null) {
                    throw new NoSuchPathException();
                }

                createUser(httpRequest);
                return;
            }

            if (file.exists() && file.isDirectory()) {
                file = FileUtils.findFile(file);
            }

            byte[] body = createBody(file);

            String extension = file.getName().substring(file.getName().lastIndexOf(".") + 1);

            String mimeType = MimeType.valueOf(extension.toUpperCase()).getMimeType();

            HttpResponseHandler.responseHeader(dos, body.length, mimeType, HttpStatus.OK);
            HttpResponseHandler.responseBody(dos, body);
        } catch (IOException e) {
            logger.error(e.getMessage());
        } catch (InvalidRequestLineSyntaxException e) {
            byte[] body = e.getMessage().getBytes();
            HttpResponseHandler.responseHeader(dos, body.length, "text/plain", e.httpStatus);
            HttpResponseHandler.responseBody(dos, body);
        } catch (NoSuchPathException e) {
            byte[] body = e.httpStatus.getReasonPhrase().getBytes();
            HttpResponseHandler.responseHeader(dos, body.length, "text/plain", e.httpStatus);
            HttpResponseHandler.responseBody(dos, body);
        }
    }

    private void createUser(HttpRequest httpRequest) {
        String redirectPath = "/registration";
        Map<String, String> queries = httpRequest.getQueries();
        String userId = queries.get("userId");
        String username = queries.get("username");
        String password = queries.get("password");
        if (userId == null || username == null || password == null) {
            HttpResponseHandler.redirect(dos, redirectPath);
            return;
        }
        User user = new User(queries.get("userId"), queries.get("username"), queries.get("password"), null);
        redirectPath = "/main";
        Database.addUser(user);
        HttpResponseHandler.redirect(dos, redirectPath);
    }

    private byte[] createBody(File file) throws IOException {
        InputStream is = new FileInputStream(file);
        byte[] body = is.readAllBytes();
        is.close();

        return body;
    }
}

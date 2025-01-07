package webserver;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import http.HttpMethod;
import http.HttpRequest;
import http.HttpResponse;
import http.HttpStatus;

public class RequestHandler implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);
    private static final String STATIC_FILES_PATH = "src/main/resources/static";

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }
    public void run() {
        logger.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
            connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            handleRequest(in, new HttpResponse(out));
        } catch (IOException e) {
            logger.error("Error handling client connection: {}", e.getMessage());
        }
    }

    private void handleRequest(InputStream in, HttpResponse response) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            HttpRequest request = new HttpRequest(reader);

            if (HttpMethod.GET.equals(request.getMethod())) {
                serveStaticFile(request.getPath(), response);
            } else {
                response.send404();
            }
        } catch (IOException e) {
            logger.error("Failed to parse the request: {}", e.getMessage());
            response.send404();
        }
    }

    private void serveStaticFile(String url, HttpResponse response) throws IOException {
        File file = new File(STATIC_FILES_PATH + url);

        if (file.exists() && file.isFile()) {
            byte[] body = Files.readAllBytes(file.toPath());
            String contentType = getContentType(url);
            response.sendResponse(HttpStatus.OK, contentType, body);
        } else {
            response.send404();
        }
    }

    private String getContentType(String url) {
        if (url.endsWith(".html")) return "text/html";
        if (url.endsWith(".css")) return "text/css";
        if (url.endsWith(".js")) return "application/javascript";
        if (url.endsWith(".png")) return "image/png";
        if (url.endsWith(".jpg") || url.endsWith(".jpeg")) return "image/jpeg";
        if (url.endsWith(".svg")) return "image/svg+xml";
        if (url.endsWith(".ico")) return "image/x-icon";
        return "application/octet-stream";
    }
}

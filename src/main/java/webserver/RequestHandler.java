package webserver;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import http.HttpMethod;

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
            handleRequest(in, out);
        } catch (IOException e) {
            logger.error("Error handling client connection: {}", e.getMessage());
        }
    }

    private void handleRequest(InputStream in, OutputStream out) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String requestLine = reader.readLine();

        if (requestLine == null || requestLine.isEmpty()) {
            logger.error("Empty request received");
            response404(out);
            return;
        }

        logger.debug("Request Line: {}", requestLine);
        String[] requestParts = requestLine.split(" ");

        if (requestParts.length < 2) {
            logger.error("Malformed request line: {}", requestLine);
            response404(out);
            return;
        }

        String method = requestParts[0];
        String url = requestParts[1];

        logger.debug("Headers: {}", reader.lines().collect(Collectors.joining("\n")));

        if (HttpMethod.GET.name().equals(method)) {
            serveStaticFile(url, out);
        } else {
            response404(out);
        }
    }

    private void serveStaticFile(String url, OutputStream out) throws IOException {
        File file = new File(STATIC_FILES_PATH + url);

        if (file.exists() && file.isFile()) {
            byte[] body = Files.readAllBytes(file.toPath());
            DataOutputStream dos = new DataOutputStream(out);
            response200Header(dos, body.length, getContentType(url));
            responseBody(dos, body);
        } else {
            response404(out);
        }
    }

    private String getContentType(String url) {
        if (url.endsWith(".html")) return "text/html";
        if (url.endsWith(".css")) return "text/css";
        if (url.endsWith(".js")) return "application/javascript";
        if (url.endsWith(".png")) return "image/png";
        if (url.endsWith(".jpg") || url.endsWith(".jpeg")) return "image/jpeg";
        if (url.endsWith(".svg")) return "image/svg+xml";
        return "application/octet-stream";
    }

    private void response200Header(DataOutputStream dos, int length, String contentType) throws IOException {
        dos.writeBytes("HTTP/1.1 200 OK\r\n");
        dos.writeBytes("Content-Type: " + contentType + "; charset=utf-8\r\n");
        dos.writeBytes("Content-Length: " + length + "\r\n");
        dos.writeBytes("\r\n");
    }


    private void response404(OutputStream out) throws IOException {
        String response = "HTTP/1.1 404 Not Found\r\n" +
            "Content-Type: text/html; charset=utf-8\r\n\r\n" +
            "<h1>404 Not Found</h1>";
        out.write(response.getBytes());
        out.flush();
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
}

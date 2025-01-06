package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHandler implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    @Override
    public void run() {
        logger.debug("New Client Connect! IP : {}, Port : {}", connection.getInetAddress(), connection.getPort());
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            RequestData requestData = parseRequest(in);

            byte[] responseBody = findStaticResource(requestData.path());
            DataOutputStream dos = new DataOutputStream(out);

            if (responseBody == null) {
                byte[] notFoundBody = "<h1>404 File Not Found</h1>".getBytes();
                response404Header(dos, notFoundBody.length);
                responseBody(dos, notFoundBody);
            } else {
                response200Header(dos, responseBody.length);
                responseBody(dos, responseBody);
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private RequestData parseRequest(InputStream in) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
        StringBuilder requestHeader = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null && !line.isEmpty()) {
            requestHeader.append(line).append("\n");
        }
        logger.debug("HTTP Request Header:\n{}", requestHeader);

        String[] firstLineTokens = requestHeader.toString().split("\n")[0].split(" ");
        String httpMethod = firstLineTokens[0];
        String path = firstLineTokens[1];

        StringBuilder body = new StringBuilder();
        while (br.ready()) {
            body.append((char) br.read());
        }
        logger.debug("HTTP Request Body:\n{}", body);

        return new RequestData(httpMethod, path, body.toString());
    }

    private byte[] findStaticResource(String path) {
        try {
            if ("/".equals(path)) {
                path = "/index.html";
            }
            java.nio.file.Path filePath = Paths.get("src/main/resources/static" + path);
            if (!Files.exists(filePath)) {
                return null;
            }
            return Files.readAllBytes(filePath);
        } catch (IOException e) {
            logger.error("Error reading static resource: {}", e.getMessage());
            return null;
        }
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private void response404Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 404 Not Found\r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
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
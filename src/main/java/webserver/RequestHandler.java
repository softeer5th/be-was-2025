package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
            DataOutputStream dos = new DataOutputStream(out);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line = br.readLine();
            String[] token = line.split(" ");

            // Request의 HTTP 헤더 출력
            StringBuilder requestHeader = new StringBuilder();
            requestHeader.append("Request Header: \n");
            requestHeader.append(line + "\n");
            while (!"".equals(line)) {
                requestHeader.append((line = br.readLine()) + "\n");
            }
            logger.debug(requestHeader.toString());

            // Request의 uri를 추출 후 해당하는 파일 탐색
            String uri = token[1];

            if (uri.equals("/")) {
                byte[] body = "<h2>Hello World</h2>".getBytes();
                response200Header(dos, body.length, "text/html");
                responseBody(dos, body);
            }
            else {
                File file = new File("src/main/resources/static" + uri);
                if (file.exists()) {
                    byte[] body = Files.readAllBytes(file.toPath());
                    String contentType = getContentType(uri);

                    response200Header(dos, body.length, contentType);
                    responseBody(dos, body);
                } else {
                    logger.error("{}file not found", uri);
                    byte[] body = "<h2> HTTP 400 Bad Request</h2>".getBytes();

                    response400Header(dos, body.length);
                    responseBody(dos, body);
                }
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    // Request uri에 따라 Content-Type을 지정하는 메서드
    private String getContentType(String uri) {
        if (uri.endsWith(".html")) {
            return "text/html";
        } else if (uri.endsWith(".css")) {
            return "text/css";
        } else if (uri.endsWith(".svg")) {
            return "image/svg+xml";
        } else if (uri.endsWith(".png")) {
            return "image/png";
        } else if (uri.endsWith(".jpg") || uri.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (uri.endsWith(".ico")) {
            return "image/vnd.microsoft.icon";
        } else if (uri.endsWith(".js")) {
            return "application/javascript";
        }
        else {
            return "application/octet-stream";
        }
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent, String contentType) throws IOException {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: " + contentType + ";charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private void response400Header(DataOutputStream dos, int lengthOfBodyContent) throws IOException {
        try {
            dos.writeBytes("HTTP/1.1 400 Bad Request\r\n");
            dos.writeBytes("Content-Type: text/html\r\n");
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

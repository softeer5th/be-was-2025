package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHandler implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);
    private static final String resourcePath = "src/main/resources/";

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
            // 회원가입 요청에 대한 처리
            else if (uri.equals("/registration")) {
                File file = new File(resourcePath + "static/registration/index.html");
                if (file.exists()) {
                    byte[] body = Files.readAllBytes(file.toPath());
                    response200Header(dos, body.length, "text/html");
                    responseBody(dos, body);
                } else {
                    logger.error("{}File not found", uri);
                    byte[] body = "<h2> HTTP 404 Not Found</h2>".getBytes();

                    response404Header(dos, body.length);
                    responseBody(dos, body);
                }
            }
            else {
                File file = new File(resourcePath + "static" + uri);
                if (file.exists()) {
                    byte[] body = Files.readAllBytes(file.toPath());
                    String contentType = ContentTypeMapper.getContentType(uri);

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

    private void response404Header(DataOutputStream dos, int lengthOfBodyContent) throws IOException {
        try {
            dos.writeBytes("HTTP/1.1 404 Not Found\r\n");
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

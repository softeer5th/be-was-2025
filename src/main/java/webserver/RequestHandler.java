package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtil;

public class RequestHandler implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);

    private static final String RESOURCE_PATH = "./src/main/resources/static";

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        logger.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
            BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8")); // InputStream => InputStreamReader => BufferedReader
            String startLine = br.readLine(); // HTTP Request의 첫 줄을 읽고 출력
            logger.debug(startLine);
            String headers = br.readLine(); // 그 다음 Request들을 읽고 출력
            while (headers != null) {
                logger.debug(headers);
                headers = br.readLine();
            }

            DataOutputStream dos = new DataOutputStream(out);
            String url = HttpRequestUtil.getUrl(startLine); // 분할한 토큰들 중 URL을 추출

            if (url == null) {
                return;
            }

            String path = RESOURCE_PATH + url;
            if (HttpRequestUtil.isDirectory(url)) {
                if (!url.endsWith("/")) path += "/";
                path += "index.html";
            }

            byte[] body = Files.readAllBytes(new File(path).toPath()); // 해당 파일의 경로를 byte로 전달
            response200Header(dos, body.length);
            responseBody(dos, body);
        } catch (IOException e) {
            logger.error(e.getMessage());
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

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
}

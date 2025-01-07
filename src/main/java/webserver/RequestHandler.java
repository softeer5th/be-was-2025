package webserver;

import java.io.*;
import java.net.Socket;

import model.Mime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.StaticFileProvider;

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
            BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            DataOutputStream dos = new DataOutputStream(out);
            byte[] body = null;
            String path = null;

            String line = br.readLine();
            logger.debug("request line : {}", line);

            if ((path = parseRequestPath(line)) != null) {
                File resultFile = StaticFileProvider.findStaticFileByPath(path);

                if (resultFile == null) {
                    throw new RuntimeException("해당 경로에 해당하는 파일이 없습니다.");
                }
                body = StaticFileProvider.readStaticFileToByteArray(resultFile);
            }

            while (!line.equals("")) {
                line = br.readLine();
                logger.debug("header:  {}", line);
            }

            String extension = extractFileExtension(path);

            String mimeType = Mime.getMimeType(extension);

            if(mimeType == null){
                throw new RuntimeException("파일 확장자에 해당하는 MIME 타입이 없습니다.");
            }

            response200Header(dos, mimeType, body.length);
            responseBody(dos, body);
        } catch (IOException | RuntimeException e) {
            logger.error(e.getMessage());
        }
    }

    private String parseRequestPath(String requestFirstLine){
        String[] requestParts = requestFirstLine.split(" ");
        return requestParts[1];
    }

    private String extractFileExtension(String path){
        String[] pathParts = path.split("\\.");
        return pathParts[pathParts.length - 1];
    }

    private void response200Header(DataOutputStream dos, String mimeType, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes(String.format("Content-Type: %s;charset=utf-8\r\n", mimeType));
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

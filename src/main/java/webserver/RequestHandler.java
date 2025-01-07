package webserver;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import model.ContentTypeMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHandler implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);
    private static final String RESOURCES_PATH = "./src/main/resources/static";
    private static final ContentTypeMapper CONTENT_TYPE_MAPPER = new ContentTypeMapper();
    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        logger.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
            DataOutputStream dos = new DataOutputStream(out);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            List<String> lines = new ArrayList<>();
            String line;
            while (!(line = br.readLine()).isEmpty()) {
                lines.add(line);
            }
            logRequestDetails(lines);
            String[] tokens = lines.get(0).split(" ");
            String filePath = tokens[1];
            String fileType = tokens[1].split("\\.")[1];

            File file = new File(RESOURCES_PATH + filePath);
            FileInputStream fileInputStream = new FileInputStream(file);
            byte[] body = new byte[(int) file.length()];
            fileInputStream.read(body);

            response200Header(dos, body.length, fileType);
            responseBody(dos, body);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private void logRequestDetails(List<String> lines) {
        StringBuilder logMessageBuilder = new StringBuilder();
        logMessageBuilder.append("\n{\n");
        for (String eachLine : lines) {
            logMessageBuilder.append(eachLine).append('\n');
        }
        logMessageBuilder.append("}\n");
        logger.debug(logMessageBuilder.toString());
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent, String fileType) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: " + CONTENT_TYPE_MAPPER.getContentType(fileType) + ";charset=utf-8\r\n");
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

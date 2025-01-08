package webserver;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import Entity.QueryParameters;
import model.ContentTypeMapper;
import model.User;
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

//            System.out.println("filePath = " + filePath);
            String fileExtension = "html";
            if (!filePath.contains("?")) {
                fileExtension = tokens[1].split("\\.")[1];
            }

            // url에 "?" 구분자가 존재하는 경우 회원가입 로직 수행. -> 수정 필요.
            if (filePath.contains("?")) {
                QueryParameters queryParameters = new QueryParameters(filePath.split("\\?")[1]);
//                System.out.println("queryss = " + filePath.split("\\?")[1]);
                User.validateUserParameters(queryParameters);

                // User 객체 생성
                User user = new User(queryParameters.get("userId"),
                        queryParameters.get("password"),
                        queryParameters.get("name"),
                        queryParameters.get("email"));
                logger.debug("user = {}", user);
            }






            File file = new File(RESOURCES_PATH + filePath);
            if (!CONTENT_TYPE_MAPPER.isValidExtension(fileExtension) || !file.exists()) {
                String notFoundPage = "<html><body><h1 style=\"text-align: center\">404 Not Found</h1></body></html>";
                byte[] bodyBytes = notFoundPage.getBytes();
                response404Header(dos, bodyBytes.length);
                dos.write(bodyBytes);
                dos.flush();
                return;
            }
            FileInputStream fileInputStream = new FileInputStream(file);
            byte[] body = new byte[(int) file.length()];
            fileInputStream.read(body);

            response200Header(dos, body.length, fileExtension);
            responseBody(dos, body);
        } catch (Exception e) {
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

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent, String fileExtension) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: " + CONTENT_TYPE_MAPPER.getContentType(fileExtension) + ";charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private void response404Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 404 Not Found \r\n");
            dos.writeBytes("Content-Type: text/html; charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        }
        catch (IOException e) {
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

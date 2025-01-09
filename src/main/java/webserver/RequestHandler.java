package webserver;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import Entity.QueryParameters;
import db.Database;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import request.HttpResponse;
import request.HttpStatus;
import util.ContentTypeUtil;

public class RequestHandler implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);
    private static final String RESOURCES_PATH = "./src/main/resources/static";
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

            String fileExtension = "html";
            if (!filePath.contains("?")) {
                fileExtension = tokens[1].split("\\.")[1];
            }

            // url에 "?" 구분자가 존재하는 경우 회원가입 로직 수행. -> 수정 필요.
            if (tokens[1].contains("/create") && filePath.contains("?")) {
                QueryParameters queryParameters = new QueryParameters(filePath.split("\\?")[1]);
                User.validateUserParameters(queryParameters);

                // User 객체 생성
                User user = new User(queryParameters.get("userId"),
                        queryParameters.get("password"),
                        queryParameters.get("name"),
                        queryParameters.get("email"));
                logger.debug("user = {}", user);
                Database.addUser(user);
                filePath = "/main/index.html";
            }

            File file = new File(RESOURCES_PATH + filePath);
            if (!ContentTypeUtil.isValidExtension(fileExtension) || !file.exists()) {
                response404(dos);
            }
            FileInputStream fileInputStream = new FileInputStream(file);
            byte[] body = new byte[(int) file.length()];
            fileInputStream.read(body);
            HttpResponse httpResponse = new HttpResponse(HttpStatus.OK, dos, body, ContentTypeUtil.getContentType(fileExtension));
            httpResponse.respond();
        }
        catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    private void response404(DataOutputStream dos) {
        String notFoundPage = "<html><body><h1 style=\"text-align: center\">404 Not Found</h1></body></html>";
        byte[] body = notFoundPage.getBytes();
        try {
            HttpResponse httpResponse = new HttpResponse(HttpStatus.NOT_FOUND, dos, body, "text/html");
            httpResponse.respond();
        }
        catch (Exception exception) {
            logger.error(exception.getMessage());
        }
    }

    private void logRequestDetails(List<String> headers) {
        StringBuilder logMessageBuilder = new StringBuilder();
        logMessageBuilder.append("\n{\n");
        for (String eachHeader : headers) {
            logMessageBuilder.append(eachHeader).append('\n');
        }
        logMessageBuilder.append("}\n");
        logger.debug(logMessageBuilder.toString());
    }
}

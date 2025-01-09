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
import http.HttpResponse;
import http.HttpStatus;
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
            String requestPath = tokens[1];

            String fileExtension = "html";
            if (!requestPath.contains("?")) {
                fileExtension = tokens[1].split("\\.")[1];
            }

            if (tokens[1].contains("/create") && requestPath.contains("?")) {
                creatUser(requestPath, dos);
            }

            File file = new File(RESOURCES_PATH + requestPath);
            if (!ContentTypeUtil.isValidExtension(fileExtension) || !file.exists()) {
                HttpResponse.respond404(dos);
                return;
            }
            byte[] body = readFile(file);
            HttpResponse httpResponse = new HttpResponse(HttpStatus.OK, dos, body, ContentTypeUtil.getContentType(fileExtension));
            httpResponse.respond();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    private void creatUser(String queries, DataOutputStream dos) throws Exception {
        QueryParameters queryParameters = new QueryParameters(queries.split("\\?")[1]);
        User.validateUserParameters(queryParameters);
        User user = new User(queryParameters.get("userId"),
                queryParameters.get("password"),
                queryParameters.get("name"),
                queryParameters.get("email"));
        logger.debug("user = {}", user);
        Database.addUser(user);
        String mainPagePath = RESOURCES_PATH + "/main/index.html";
        File file = new File(mainPagePath);
        byte[] body = readFile(file);
        HttpResponse httpResponse = new HttpResponse(HttpStatus.OK, dos ,body, "text/html");
        httpResponse.respond();
    }

    private byte[] readFile(File file) throws Exception {
        FileInputStream fileInputStream = new FileInputStream(file);
        byte[] fileBytes= new byte[(int) file.length()];
        fileInputStream.read(fileBytes);
        return fileBytes;
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

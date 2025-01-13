package webserver;

import java.io.*;
import java.net.Socket;
import java.util.List;

import Entity.QueryParameters;
import db.Database;
import http.HttpMethod;
import http.HttpRequest;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import http.HttpResponse;
import http.HttpStatus;
import util.ContentTypeUtil;
import util.ParsingUtil;

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
            DataOutputStream dos = new DataOutputStream(out);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            handleRequest(br, dos);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    private void handleRequest(BufferedReader br, DataOutputStream dos) throws Exception {
        List<String> headerLines = ParsingUtil.parseRequestHeader(br);
        HttpRequest httpRequest = new HttpRequest(headerLines);
        if (httpRequest.getHttpMethod() == HttpMethod.POST) {
            int contentLength = httpRequest.getContentLength();
            char[] requestBody = new char[contentLength];
            br.read(requestBody, 0, contentLength);
            httpRequest.setBody(new String(requestBody));
        }
        httpRequest.log(logger);

        String requestPath = httpRequest.getRequestPath();
        if (httpRequest.getHttpMethod() == HttpMethod.GET) {
            String fileExtension = requestPath.split("\\.")[1];
            File file = new File(RESOURCES_PATH + requestPath);
            if (!ContentTypeUtil.isValidExtension(fileExtension) || !file.exists()) {
                HttpResponse.respond404(dos);
                return;
            }
            byte[] body = readFile(file);
            HttpResponse httpResponse = new HttpResponse(HttpStatus.OK, dos, body, ContentTypeUtil.getContentType(fileExtension));
            httpResponse.respond();
        }
        else if (httpRequest.getHttpMethod() == HttpMethod.POST) {
            if (requestPath.startsWith("/user/create")) {
                creatUser(httpRequest.getBody(), dos);
                return;
            }
        }
        else {
            logger.error("Invalid Http Method");
        }
    }

    private void creatUser(String requestBody, DataOutputStream dos) throws Exception {
        QueryParameters queryParameters = new QueryParameters(requestBody);
        User.validateUserParameters(queryParameters);
        User user = new User(queryParameters.get("userId"),
                queryParameters.get("password"),
                queryParameters.get("name"),
                queryParameters.get("email"));
        logger.debug("user = {}", user);
        Database.addUser(user);
        String mainPagePath = RESOURCES_PATH + "/index.html";
        File file = new File(mainPagePath);
        byte[] body = readFile(file);
        HttpResponse httpResponse = new HttpResponse(HttpStatus.CREATED, dos ,body, "text/html");
        httpResponse.respond();
    }

    private byte[] readFile(File file) throws Exception {
        FileInputStream fileInputStream = new FileInputStream(file);
        byte[] fileBytes= new byte[(int) file.length()];
        fileInputStream.read(fileBytes);
        return fileBytes;
    }
}

package webserver;

import Entity.QueryParameters;
import db.Database;
import http.HttpMethod;
import http.HttpRequest;
import http.HttpResponse;
import http.HttpStatus;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.ContentTypeUtil;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class RequestRouter {
    private final Map<HttpMethod, BiConsumer<HttpRequest, DataOutputStream>> getHandler = new HashMap<>();
    private final Map<String, BiConsumer<HttpRequest, DataOutputStream>> postHandlers = new HashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);
    private static final String RESOURCES_PATH = "./src/main/resources/static";

    public RequestRouter() {
        init();
    }

    public void route(HttpRequest httpRequest, DataOutputStream dos) throws IOException {
        HttpMethod method = httpRequest.getHttpMethod();
        String path = httpRequest.getRequestPath();

        if (method == HttpMethod.GET) {
            BiConsumer<HttpRequest, DataOutputStream> handler = getHandler.get(HttpMethod.GET);
            if (handler != null) {
                handler.accept(httpRequest, dos);
                return;
            }
        } else if (method == HttpMethod.POST) {
            BiConsumer<HttpRequest, DataOutputStream> handler = postHandlers.get(path);
            if (handler != null) {
                handler.accept(httpRequest, dos);
                return;
            }
        }

        HttpResponse.respond404(dos);
    }

    private void init() {
        this.addGetHandler((request, dos) -> {
            try {
                String fileExtension = request.getRequestPath().split("\\.")[1];
                File file = new File(RESOURCES_PATH + request.getRequestPath());
                if (!ContentTypeUtil.isValidExtension(fileExtension) || !file.exists()) {
                    HttpResponse.respond404(dos);
                    return;
                }
                byte[] body = readFile(file);
                HttpResponse httpResponse = new HttpResponse(HttpStatus.OK, dos, body, ContentTypeUtil.getContentType(fileExtension));
                httpResponse.addHeader("Content-Type", ContentTypeUtil.getContentType(fileExtension));
                httpResponse.addHeader("Content-Length", String.valueOf(body.length));
                httpResponse.respond();
            } catch (IOException e) {
                logger.error(Arrays.toString(e.getStackTrace()));
                throw new ExceptionInInitializerError("Router Registration Error");
            }
        });

        // 정규표현식으로 변경할것.
        this.addPostHandler("/user/create", (request, dos) -> {
            try {
                creatUser(request.getBody(), dos);
            } catch (Exception e) {
                throw new ExceptionInInitializerError("/user/create handler registration error" + e);
            }
        });
    }

    private void addGetHandler(BiConsumer<HttpRequest, DataOutputStream> handler) {
        getHandler.put(HttpMethod.GET, handler);
    }

    private void addPostHandler(String path, BiConsumer<HttpRequest, DataOutputStream> handler) {
        postHandlers.put(path, handler);
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
        HttpResponse httpResponse = new HttpResponse(HttpStatus.CREATED, dos, body, "text/html");
        httpResponse.respond();
    }

    private byte[] readFile(File file) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(file);
        byte[] fileBytes = new byte[(int) file.length()];
        fileInputStream.read(fileBytes);
        return fileBytes;
    }
}


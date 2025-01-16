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

    public void route(HttpRequest httpRequest, DataOutputStream dos) throws IOException{
        HttpMethod method = httpRequest.getHttpMethod();
        String path = httpRequest.getRequestPath();

        if (method == HttpMethod.GET) {
            BiConsumer<HttpRequest, DataOutputStream> handler = getHandler.get(HttpMethod.GET);
            if (handler != null) {
                handler.accept(httpRequest, dos);
                return;
            }
        }
        if (method == HttpMethod.POST) {
            BiConsumer<HttpRequest, DataOutputStream> handler = postHandlers.get(path);
            if (handler != null) {
                handler.accept(httpRequest, dos);
                return;
            }
        }
        HttpResponse.respond404(dos);
    }

    private void init() {
        // GET request -> 정적 파일 반환
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
                logger.error("Get Request Error, " + e.getMessage());
            }
        });

        // /user/creat 경로로 POST 요청시 -> 회원가입 이후 index/html 리다이랙션
        this.addPostHandler("/user/create", (request, dos) -> {
            creatUser(request.getBody());
            try {
                HttpResponse.respond302("http://localhost:8080/index.html", dos);
            } catch (IOException e) {
                logger.error("Redirection Error, " + e.getMessage());
            }
        });

        /*
         * /user/signIn 경로로 POST 요청시 -> 로그인 로직 수행
         * 유저 일치 확인 : userId, password
         * 로그인 실패 -> loginFailed 페이지 반환
         * 로그인 성공 -> index.html 리다이랙트, 쿠키 발급
         */
        this.addPostHandler(SIGNIN_PATH, (request, dos) -> {
            QueryParameters queryParameters = new QueryParameters(request.getBody());
            try {
                User.validateSignInUserParameters(queryParameters);
                User user = signIn(queryParameters.get("userId"), queryParameters.get("password"));
                // 쿠키 발급
                String sessionId = UUID.randomUUID().toString();
                HttpResponse httpResponse = new HttpResponse(HttpStatus.FOUND, dos, null, null);
                httpResponse.addHeader(SET_COOKIE, "sid=" + sessionId + "; Path=/");
                httpResponse.addHeader(LOCATION, LOGINED_MAIN_PAGE);
                httpResponse.respond();
                userSessions.put(sessionId, user);
            } catch (MissingUserInfoException | AuthenticationException e) {
                // 로그인 실패
                logger.debug(e.getMessage());
                try {
                    HttpResponse.respond302(LOGIN_FAILED_PAGE, dos);
                } catch (IOException ex) {
                    logger.error("SignIn Redirection Error" + ex.getMessage());
                }
            } catch (IOException e) {
                logger.error("SignIn Error" + e.getMessage());
            }
        });
    }

    private User signIn(String userId, String password) throws AuthenticationException {
        Optional<User> userOptional = Database.findUserById(userId);
        if (userOptional.isEmpty()) {
            throw new AuthenticationException("User id is incorrect");
        }
        User user = userOptional.get();
        if (!user.getPassword().equals(password)) {
            throw new AuthenticationException("password is incorrect");
        }
        return user;
    }

    private void addGetHandler(BiConsumer<HttpRequest, DataOutputStream> handler) {
        getHandler.put(HttpMethod.GET, handler);
    }

    private void addPostHandler(String path, BiConsumer<HttpRequest, DataOutputStream> handler) {
        postHandlers.put(path, handler);
    }

    private void creatUser(String requestBody) throws Exception {
        QueryParameters queryParameters = new QueryParameters(requestBody);
        User.validateSignUpUserParameters(queryParameters);
        User user = new User(queryParameters.get("userId"),
                queryParameters.get("password"),
                queryParameters.get("name"),
                queryParameters.get("email"));
        logger.debug("user = {}", user);
        User.validateSignUpUserIdDuplication(user);
        Database.addUser(user);
    }

    private byte[] readFile(File file) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(file);
        byte[] fileBytes = new byte[(int) file.length()];
        fileInputStream.read(fileBytes);
        return fileBytes;
    }
}


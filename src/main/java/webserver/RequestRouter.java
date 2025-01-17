package webserver;

import Entity.QueryParameters;
import db.Database;
import exception.MissingUserInfoException;
import handler.GetHandler;
import http.HttpMethod;
import http.HttpRequest;
import http.HttpResponse;
import http.HttpStatus;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.ContentTypeUtil;

import javax.security.sasl.AuthenticationException;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiConsumer;

public class RequestRouter {
    private final Map<HttpMethod, BiConsumer<HttpRequest, DataOutputStream>> getHandler = new HashMap<>();
    private final Map<String, BiConsumer<HttpRequest, DataOutputStream>> postHandlers = new HashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);
    private static final String RESOURCES_PATH = "./src/main/resources/static";
    private static final String LOGIN_FAILED_PAGE = "http://localhost:8080/login/login_failed.html";
    private static final String SIGNUP_FAILED_PAGE = "http://localhost:8080/registration/registration_failed.html";
    private static final String LOGINED_MAIN_PAGE = "http://localhost:8080/main/index.html";
    private static final String MAIN_PAGE = "http://localhost:8080/index.html";
    private static final String SIGNIN_PATH = "/user/signIn";
    private static final String SIGNUP_PATH = "/user/create";
    private static final String LOGOUT_PATH = "/user/logout";
    private static final String USER_INFO_PATH = "/user/info";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String SET_COOKIE = "Set-Cookie";
    private static final String LOCATION = "location";
    private final Map<String, User> userSessions;

    public RequestRouter() {
        init();
        userSessions = new HashMap<>();
    }

    private void init() {
        this.addGetHandler(GetHandler::handleGetRequest);
        this.addPostHandler(SIGNUP_PATH, this::handleSignUp);
        this.addPostHandler(SIGNIN_PATH, this::handleSignIn);
        this.addPostHandler(LOGOUT_PATH, this::handleLogout);
        this.addPostHandler(USER_INFO_PATH, this::handleUserInfo);
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

    private void addGetHandler(BiConsumer<HttpRequest, DataOutputStream> handler) {
        getHandler.put(HttpMethod.GET, handler);
    }

    private void addPostHandler(String path, BiConsumer<HttpRequest, DataOutputStream> handler) {
        postHandlers.put(path, handler);
    }

    /*
     * GET request -> 정적 파일 반환
     */
//    private void handleGetRequest(HttpRequest request, DataOutputStream dos) {
//        try {
//            String fileExtension = request.getRequestPath().split("\\.")[1];
//            File file = new File(RESOURCES_PATH + request.getRequestPath());
//            if (!ContentTypeUtil.isValidExtension(fileExtension) || !file.exists()) {
//                HttpResponse.respond404(dos);
//                return;
//            }
//            byte[] body = readFile(file);
//            HttpResponse httpResponse = new HttpResponse(HttpStatus.OK, dos, body, ContentTypeUtil.getContentType(fileExtension));
//            httpResponse.addHeader(CONTENT_TYPE, ContentTypeUtil.getContentType(fileExtension));
//            httpResponse.addHeader(CONTENT_LENGTH, String.valueOf(body.length));
//            httpResponse.respond();
//        } catch (IOException e) {
//            logger.error("Get Request Error, " + e.getMessage());
//        }
//    }

//    private void handleSignUp(HttpRequest request, DataOutputStream dos) {
//        try {
//            creatUser(request.getBody());
//            HttpResponse.respond302(MAIN_PAGE, dos);
//        } catch (Exception e) {
//            logger.debug("Signup failed, " + e.getMessage());
//            try {
//                HttpResponse.respond302(SIGNUP_FAILED_PAGE, dos);
//            } catch (IOException ex) {
//                logger.error("Redirection Error, " + ex.getMessage());
//            }
//        }
//    }

    /*
     * 유저 일치 확인 : userId, password
     * 로그인 실패 -> loginFailed 페이지 반환
     * 로그인 성공 -> index.html 리다이랙트, 쿠키 발급
     */
    private void handleSignIn(HttpRequest request, DataOutputStream dos) {
        QueryParameters queryParameters = new QueryParameters(request.getBody());
        try {
            User.validateSignInUserParameters(queryParameters);
            User user = signIn(queryParameters.get("userId"), queryParameters.get("password"));
            // 쿠키 발급
            String sessionId = UUID.randomUUID().toString();
            HttpResponse httpResponse = new HttpResponse(HttpStatus.FOUND, dos, null, null);
            httpResponse.addHeader(SET_COOKIE, "sid=" + sessionId + "; Path=/");
            httpResponse.addHeader(LOCATION, MAIN_PAGE);
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
    }

    /*
     * 로그아웃
     * sid=null 의 새로운 쿠키 발급, session 정보 저장
     */
    private void handleLogout(HttpRequest request, DataOutputStream dos) {
        String sid = request.getCookieSid();
        if (sid != null) {
            userSessions.remove(sid);
        }
        try {
            HttpResponse httpResponse = new HttpResponse(HttpStatus.FOUND, dos, null, null);
            httpResponse.addHeader(SET_COOKIE, "sid=" + null + "; Path=/");
            httpResponse.addHeader("location", MAIN_PAGE);
            httpResponse.respond();
        } catch (IOException e) {
            logger.error("Logout Redirection Error" + e.getMessage());
        }
    }

    /*
     * 쿠키의 sid로 로그인 여부를 판단 후 userName 반환
     */
    private void handleUserInfo(HttpRequest request, DataOutputStream dos) {
        String sid = request.getCookieSid();
        byte[] body;
        if (sid != null && userSessions.containsKey(sid)) {
            body = ("userName=" + userSessions.get(sid).getName()).getBytes();
        } else {
            body = ("userName=null").getBytes();
        }
        HttpResponse httpResponse = new HttpResponse(HttpStatus.OK, dos, body, "text/html");
        httpResponse.addHeader(CONTENT_TYPE, "text/html");
        httpResponse.addHeader(CONTENT_LENGTH, String.valueOf(body.length));
        try {
            httpResponse.respond();
        } catch (IOException e) {
            logger.error("/user/info response error, {}", e.getMessage());
        }
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
//
//    private byte[] readFile(File file) throws IOException {
//        FileInputStream fileInputStream = new FileInputStream(file);
//        byte[] fileBytes = new byte[(int) file.length()];
//        fileInputStream.read(fileBytes);
//        return fileBytes;
//    }
}


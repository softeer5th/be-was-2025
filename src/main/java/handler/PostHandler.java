package handler;

import Entity.QueryParameters;
import db.Database;
import http.*;
import model.Post;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.SessionUtil;

import javax.security.sasl.AuthenticationException;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class PostHandler {
    private static final Logger logger = LoggerFactory.getLogger(PostHandler.class);
    private static final String LOGIN_FAILED_PAGE = "http://localhost:8080/login/login_failed.html";
    private static final String LOGIN_PAGE = "http://localhost:8080/login/index.html";
    private static final String SIGNUP_FAILED_PAGE = "http://localhost:8080/registration/registration_failed.html";
    private static final String MAIN_PAGE = "http://localhost:8080/index.html";
    private static final String UPDATE_FAILED_PAGE = "http://localhost:8080/mypage/update_failed.html";
    private static final String LOCATION = "location";

    public static void handleSignUp(HttpRequest request, DataOutputStream dos) {
        try {
            creatUser(request.getBody());
            HttpResponse.respond302(MAIN_PAGE, dos);
        } catch (Exception e) {
            logger.debug("Signup failed, " + e.getMessage());
            try {
                HttpResponse.respond302(SIGNUP_FAILED_PAGE, dos);
            } catch (IOException ex) {
                logger.error("Redirection Error, " + ex.getMessage());
            }
        }
    }

    /*
     * 유저 일치 확인 : userId, password
     * 로그인 실패 -> loginFailed 페이지 반환
     * 로그인 성공 -> index.html 리다이랙트, 쿠키 발급
     */
    public static void handleSignIn(HttpRequest request, DataOutputStream dos) {
        QueryParameters queryParameters = new QueryParameters(request.getBody());
        try {
            User.validateSignInUserParameters(queryParameters);
            User user = signIn(queryParameters.get("userId"), queryParameters.get("password"));
            // 쿠키 발급
            String sessionId = UUID.randomUUID().toString();
            HttpResponse httpResponse = new HttpResponse(HttpStatus.FOUND, dos, null, null);
            httpResponse.addHeader(HttpHeader.SET_COOKIE.getHeaderName(), String.format("sid=%s; Path=/", sessionId));
            httpResponse.addHeader(LOCATION, MAIN_PAGE);
            httpResponse.respond();
            SessionUtil.getUserSessions().put(sessionId, user);
        } catch (Exception e) {
            // 로그인 실패
            logger.debug(e.getMessage());
            try {
                HttpResponse.respond302(LOGIN_FAILED_PAGE, dos);
            } catch (IOException ex) {
                logger.error("SignIn Redirection Error" + ex.getMessage());
            }
        }
    }

    /*
     * 로그아웃
     * sid=null 의 새로운 쿠키 발급, session 정보 저장
     */
    public static void handleLogout(HttpRequest request, DataOutputStream dos) {
        String sid = request.getCookieSid();
        if (sid != null) {
            SessionUtil.getUserSessions().remove(sid);
        }
        try {
            HttpResponse httpResponse = new HttpResponse(HttpStatus.FOUND, dos, null, null);
            httpResponse.addHeader(HttpHeader.SET_COOKIE.getHeaderName(), "sid=; Path=/");
            httpResponse.addHeader(HttpHeader.LOCATION.getHeaderName(), MAIN_PAGE);
            httpResponse.respond();
        } catch (IOException e) {
            logger.error("Logout Redirection Error" + e.getMessage());
        }
    }

    /*
     * 쿠키의 sid로 로그인 여부를 판단 후 userName 반환
     */
    public static void handleUserInfo(HttpRequest request, DataOutputStream dos) {
        if (!isUserLoggedIn(request)) {
            try {
                HttpResponse.respond302(LOGIN_PAGE, dos);
            } catch (IOException e) {
                logger.error("{}, Redirection Error, {}", "handleUserInfo", e.getMessage());
            }
            return;
        }
        String sid = request.getCookieSid();
        byte[] body = ("userName=" + SessionUtil.getUserSessions().get(sid).getName()).getBytes();
        HttpResponse httpResponse = new HttpResponse(HttpStatus.OK, dos, body, "text/html");
        httpResponse.addHeader(HttpHeader.CONTENT_TYPE.getHeaderName(), "text/html");
        httpResponse.addHeader(HttpHeader.CONTENT_LENGTH.getHeaderName(), String.valueOf(body.length));
        try {
            httpResponse.respond();
        } catch (IOException e) {
            logger.error("/user/info response error, {}", e.getMessage());
        }
    }


    public static void handleCreatePost(HttpRequest request, DataOutputStream dos) {
        try {
            if (!isUserLoggedIn(request)) {
                HttpResponse.respond302(LOGIN_PAGE, dos);
            }
            String boundary = request.getWebKitFormBoundary();
            if (boundary == null) {
                HttpResponse.respond400(dos);
            }
            User user = getUserBySid(request.getCookieSid());
            createPost(request.getBody(), boundary, user.getUserId());
            HttpResponse.respond302(MAIN_PAGE + "?postId=" + Database.getLastPostId(), dos);
        } catch (Exception e) {
            logger.error("handleCreatePost error, {}", e.getMessage());
        }
    }

    public static void handleUpdateUserInfo(HttpRequest request, DataOutputStream dos) {
        try {
            // 로그인되어 있지 않은 경우 -> 로그인페이지
            if (!isUserLoggedIn(request)) {
                HttpResponse.respond302(LOGIN_PAGE, dos);
                return;
            }
            QueryParameters queryParameters = new QueryParameters(request.getBody());
            if (!queryParameters.get("password").equals(queryParameters.get("passwordConfirm"))) {
                throw new IllegalArgumentException("password and passwordConfirm does not matched");
            }
            String sid = request.getCookieSid();
            User user = SessionUtil.getUserSessions().get(sid);
            user.setName(queryParameters.get("userName"));
            user.setPassword(queryParameters.get("password"));
            Database.updateUser(user);
            HttpResponse.respond302(MAIN_PAGE, dos);
        } catch (Exception e) {
            logger.debug("User info update failed, {}", e.getMessage());
            redirectUpdateFailedPage(dos, e.getMessage());
        }
    }

    private static void redirectUpdateFailedPage(DataOutputStream dos, String errorMessage) {
        try {
            HttpResponse.respond302(PostHandler.UPDATE_FAILED_PAGE, dos);
        } catch (IOException ex) {
            logger.error("Redirection Error: {}, {}, {}", PostHandler.UPDATE_FAILED_PAGE, errorMessage, ex.getMessage());
        }
    }

    private static void createPost(String body, String boundary, String userId) {
        Map<String, String> parsedData = new HashMap<>();
        String[] parts = body.split("--" + boundary);
        for (String part : parts) {
            if (part.trim().isEmpty() || part.equals("--")) continue;
            String[] sections = part.split("\r\n\r\n", 2);
            if (sections.length == 2) {
                String headers = sections[0];
                String content = sections[1].trim();

                // name="~~" 추출
                // name=" 의 시작 인덱스.
                int nameStartIndex = headers.indexOf("name=\"") + 6;
                if (nameStartIndex > 4) { // 'name='가 존재할 경우
                    int nameEndIndex = headers.indexOf("\"", nameStartIndex);
                    if (nameEndIndex > nameStartIndex) {
                        String key = headers.substring(nameStartIndex, nameEndIndex);
                        parsedData.put(key, content);
                    }
                }
            }
        }
        Post post = new Post(Database.getLastPostId() + 1,
                parsedData.get("title"),
                parsedData.get("content"),
                userId);
        logger.debug("new post info : post_id = {},  title = {}, content = {}, userid = {}",
                Database.getLastPostId(), parsedData.get("title"), parsedData.get("content"), userId);
        Database.addPost(post);
    }

    private static User getUserBySid(String sid) {
        return SessionUtil.getUserSessions().get(sid);
    }

    private static boolean isUserLoggedIn(HttpRequest request) {
        String sid = request.getCookieSid();
        if (sid == null || !SessionUtil.getUserSessions().containsKey(sid)) {
            return false;
        }
        return true;
    }

    private static User signIn(String userId, String password) throws AuthenticationException {
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

    private static void creatUser(String requestBody) {
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

}

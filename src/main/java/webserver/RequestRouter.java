package webserver;

import Entity.QueryParameters;
import db.Database;
import exception.MissingUserInfoException;
import http.*;
import model.Post;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.ContentTypeUtil;

import javax.security.sasl.AuthenticationException;
import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

public class RequestRouter {
    private static final String LOGOUT_BUTTON_INFO = "<form action=\"/user/logout\" method=\"POST\" style=\"display: inline;\">\n" +
            "              <button type=\"submit\" id=\"logout-btn\" class=\"btn btn_ghost btn_size_s\">\n" +
            "                로그아웃\n" +
            "              </button>\n" +
            "            </form>";
    public static final String REGISTRATION_BUTTON_INFO = "<a class=\"btn btn_ghost btn_size_s\" href=\"/registration/index.html\">\n" +
            "              회원 가입\n" +
            "            </a>";
    private final Map<HttpMethod, BiConsumer<HttpRequest, DataOutputStream>> getHandler = new HashMap<>();
    private final Map<String, BiConsumer<HttpRequest, DataOutputStream>> postHandlers = new HashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(RequestRouter.class);
    private static final String RESOURCES_PATH = "./src/main/resources/static";
    private static final String LOGIN_FAILED_PAGE = "http://localhost:8080/login/login_failed.html";
    private static final String LOGIN_PAGE = "http://localhost:8080/login/index.html";
    private static final String SIGNUP_FAILED_PAGE = "http://localhost:8080/registration/registration_failed.html";
    private static final String MAIN_PAGE = "http://localhost:8080/index.html";
    private static final String MAIN_PAGE_REDIRECT_PAGE = "http://localhost:8080/index.html?postId=1";
    private static final String SIGNIN_PATH = "/user/signIn";
    private static final String SIGNUP_PATH = "/user/create";
    private static final String LOGOUT_PATH = "/user/logout";
    private static final String USER_INFO_PATH = "/user/info";
    private static final String CREAT_POST_PATH = "/post/create";
    private static final String USER_INFO_UPDATE_PATH = "/user/update";
    private static final String LOCATION = "location";
    private static final Map<String, User> userSessions = new ConcurrentHashMap<>();

    public RequestRouter() {
        init();
    }

    private void init() {
        this.addGetHandler(GetHandler::handleGetRequest);
        this.addPostHandler(SIGNUP_PATH, this::handleSignUp);
        this.addPostHandler(SIGNIN_PATH, this::handleSignIn);
        this.addPostHandler(LOGOUT_PATH, this::handleLogout);
        this.addPostHandler(USER_INFO_PATH, this::handleUserInfo);
        this.addPostHandler(CREAT_POST_PATH, this::handleCreatePost);
        this.addPostHandler(USER_INFO_UPDATE_PATH, this::handleUpdateUserInfo);
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
    private void handleGetRequest(HttpRequest request, DataOutputStream dos) {
        try {
            String fileExtension = request.getRequestPath().split("\\.")[1].split("\\?")[0];

            File file = new File(RESOURCES_PATH + request.getRequestPath());
            if (!ContentTypeUtil.isValidExtension(fileExtension) || !file.exists()) {
                HttpResponse.respond404(dos);
                return;
            }

            String fileContent = readFileAsString(file);
            String sid = request.getCookieSid();

            // index.html -> index.html?postId=1 으로 리다이렉션
            if (request.getRequestPath().equals("/index.html") && request.getQueryParameters() == null) {
                HttpResponse.respond302(MAIN_PAGE_REDIRECT_PAGE, dos);
                return;
            }

            // 현재 uri의 postId 꺼내서 조회. -> 없으면 404
            if (request.getRequestPath().equals("/index.html")) {
                Post post = Database.findByPostId(Integer.parseInt(request.getQueryParameters().get("postId")));
                if (post == null) {
                    HttpResponse.respond404(dos);
                    return;
                }
                // 로그인 되어 있는 경우
                if (sid != null && userSessions.containsKey(sid)) {
                    fileContent = fileContent.replace("{firstButtonRequestPath}", "/mypage/index.html");
                    fileContent = fileContent.replace("{firstButtonName}", userSessions.get(sid).getName());
                    fileContent = fileContent.replace("{secondButtonInfo}", LOGOUT_BUTTON_INFO);
                }
                else {
                    fileContent = fileContent.replace("{firstButtonRequestPath}", "/login/index.html");
                    fileContent = fileContent.replace("{firstButtonName}", "로그인");
                    fileContent = fileContent.replace("{secondButtonInfo}", REGISTRATION_BUTTON_INFO);
                }
                Optional<User> optionalUser = Database.findUserById(post.getUserId());
                User author = optionalUser.get();
                fileContent = fileContent.replace("{title}", post.getTitle());
                fileContent = fileContent.replace("{userName}", author.getName());
                fileContent = fileContent.replace("{content}", post.getContent());
            }

            // 로그인 정보가 없는 경우 글쓰기 페이지 -> 로그인 페이지 리다이렉트
            if (request.getRequestPath().equals("/article/index.html") && !isUserLoggedIn(request)) {
                HttpResponse.respond302(LOGIN_PAGE, dos);
            }

            // 로그인 정보가 없는 경우 마이페이지 -> 로그인 페이지 리다이렉트
            if (request.getRequestPath().equals("/mypage/index.html") && !isUserLoggedIn(request)) {
                HttpResponse.respond302(LOGIN_PAGE, dos);
            }



            byte[] body = fileContent.getBytes();
            HttpResponse httpResponse = new HttpResponse(HttpStatus.OK, dos, body, ContentTypeUtil.getContentType(fileExtension));
            httpResponse.addHeader(HttpHeader.CONTENT_TYPE.getHeaderName(), ContentTypeUtil.getContentType(fileExtension));
            httpResponse.addHeader(HttpHeader.CONTENT_LENGTH.getHeaderName(), String.valueOf(body.length));
            httpResponse.respond();
        } catch (Exception e) {
            logger.error("Get Request Error, " + e.getMessage());
        }
    }

    private String readFileAsString(File file) throws IOException {
        StringBuilder contentBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                contentBuilder.append(line).append("\n");
            }
        }
        return contentBuilder.toString();
    }

    private void handleCreatePost(HttpRequest request, DataOutputStream dos) {
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

    private void createPost(String body, String boundary, String userId) {
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


    private void handleSignUp(HttpRequest request, DataOutputStream dos) {
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
    private void handleSignIn(HttpRequest request, DataOutputStream dos) {
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

    private User getUserBySid(String sid) {
        return userSessions.get(sid);
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
    private void handleUserInfo(HttpRequest request, DataOutputStream dos) {
        if (!isUserLoggedIn(request)) {
            try {
                HttpResponse.respond302(LOGIN_PAGE, dos);
            } catch (IOException e) {
                logger.error("{}, Redirection Error, {}", "handleUserInfo", e.getMessage());
            }
            return;
        }
        String sid = request.getCookieSid();
        byte[] body = ("userName=" + userSessions.get(sid).getName()).getBytes();
        HttpResponse httpResponse = new HttpResponse(HttpStatus.OK, dos, body, "text/html");
        httpResponse.addHeader(HttpHeader.CONTENT_TYPE.getHeaderName(), "text/html");
        httpResponse.addHeader(HttpHeader.CONTENT_LENGTH.getHeaderName(), String.valueOf(body.length));
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

    private void creatUser(String requestBody) {
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
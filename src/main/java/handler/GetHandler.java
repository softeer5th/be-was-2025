package handler;

import Entity.QueryParameters;
import db.Database;
import http.*;
import model.Post;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.ContentTypeUtil;
import util.SessionUtil;

import java.io.*;

public class GetHandler {
    private static final String LOGOUT_BUTTON_INFO = """
            <form action="/user/logout" method="POST" style="display: inline;">
                <button type="submit" id="logout-btn" class="btn btn_ghost btn_size_s">
                    로그아웃
                </button>
            </form>
            """;
    public static final String REGISTRATION_BUTTON_INFO = """
            <a class="btn btn_ghost btn_size_s" href="/registration/index.html">
                회원 가입
            </a>
            """;
    private static final Logger logger = LoggerFactory.getLogger(GetHandler.class);
    private static final String RESOURCES_PATH = "./src/main/resources/static";
    private static final String LOGIN_PAGE = "http://localhost:8080/login/index.html";
    private static final String MAIN_PAGE_REDIRECT_PAGE = "http://localhost:8080/index.html?postId=1";

    public static void handleGetRequest(HttpRequest request, DataOutputStream dos) {
        try {
            String fileExtension = extractFileExtension(request.getRequestPath());
            File file = new File(RESOURCES_PATH + request.getRequestPath());

            if (!isValidRequest(file, fileExtension)) {
                HttpResponse.respond404(dos);
                return;
            }

            if (shouldRedirectToMainPage(request)) {
                HttpResponse.respond302(MAIN_PAGE_REDIRECT_PAGE, dos);
                return;
            }

            String fileContent = readFileAsString(file);

            if (request.getRequestPath().equals("/index.html")) {
                fileContent = handleIndexPage(request, fileContent);
            } else if (request.getRequestPath().equals("/article/index.html")) {
                if (!isUserLoggedIn(request)) {
                    HttpResponse.respond302(LOGIN_PAGE, dos);
                    return;
                }
            } else if (request.getRequestPath().equals("/mypage/index.html")) {
                fileContent = handleMyPage(request, fileContent);
            }

            sendResponse(fileContent, fileExtension, dos);
        } catch (Exception e) {
            handleError(e, dos);
        }
    }

    private static String extractFileExtension(String requestPath) {
        return requestPath.split("\\.")[1].split("\\?")[0];
    }

    private static boolean isValidRequest(File file, String fileExtension) {
        return ContentTypeUtil.isValidExtension(fileExtension) && file.exists();
    }

    private static boolean shouldRedirectToMainPage(HttpRequest request) {
        return request.getRequestPath().equals("/index.html") && request.getQueryParameters() == null;
    }

    private static String handleIndexPage(HttpRequest request, String fileContent) {
        String sid = request.getCookieSid();
        QueryParameters queryParameters = request.getQueryParameters();

        Post post = Database.findByPostId(Integer.parseInt(queryParameters.get("postId")));
        if (post == null) {
            throw new IllegalArgumentException("Post not found");
        }

        if (sid != null && SessionUtil.getUserSessions().containsKey(sid)) {
            User user = SessionUtil.getUserSessions().get(sid);
            fileContent = fileContent.replace("{firstButtonRequestPath}", "/mypage/index.html")
                    .replace("{firstButtonName}", user.getName())
                    .replace("{secondButtonInfo}", LOGOUT_BUTTON_INFO);
        } else {
            fileContent = fileContent.replace("{firstButtonRequestPath}", "/login/index.html")
                    .replace("{firstButtonName}", "로그인")
                    .replace("{secondButtonInfo}", REGISTRATION_BUTTON_INFO);
        }

        User author = Database.findUserById(post.getUserId()).orElseThrow(() -> new IllegalArgumentException("User not found"));
        return fileContent.replace("{title}", post.getTitle())
                .replace("{userName}", author.getName())
                .replace("{content}", post.getContent());
    }

    private static String handleMyPage(HttpRequest request, String fileContent) {
        if (!isUserLoggedIn(request)) {
            throw new IllegalArgumentException("User not logged in");
        }
        User user = SessionUtil.getUserSessions().get(request.getCookieSid());
        return fileContent.replace("{userName}", user.getName());
    }

    private static void sendResponse(String fileContent, String fileExtension, DataOutputStream dos) throws IOException {
        byte[] body = fileContent.getBytes();
        HttpResponse httpResponse = new HttpResponse(HttpStatus.OK, dos, body, ContentTypeUtil.getContentType(fileExtension));
        httpResponse.addHeader(HttpHeader.CONTENT_TYPE.getHeaderName(), ContentTypeUtil.getContentType(fileExtension));
        httpResponse.addHeader(HttpHeader.CONTENT_LENGTH.getHeaderName(), String.valueOf(body.length));
        httpResponse.respond();
    }

    private static void handleError(Exception e, DataOutputStream dos) {
        logger.error("Get Request Error, " + e.getMessage());
        try {
            HttpResponse.respond404(dos);
        } catch (IOException ex) {
            logger.error("404 Response Error, {}", e.getMessage());
        }
    }

    public static String readFileAsString(File file) throws IOException {
        StringBuilder contentBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                contentBuilder.append(line).append("\n");
            }
        }
        return contentBuilder.toString();
    }

    private static boolean isUserLoggedIn(HttpRequest request) {
        String sid = request.getCookieSid();
        if (sid == null || !SessionUtil.getUserSessions().containsKey(sid)) {
            return false;
        }
        return true;
    }
}

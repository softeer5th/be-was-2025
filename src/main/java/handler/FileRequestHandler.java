package handler;

import db.SessionDataManager;
import db.UserDataManager;
import exception.BaseException;
import exception.FileErrorCode;
import http.HttpRequestInfo;
import http.HttpStatus;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.FileUtil;
import http.HttpResponse;

import java.nio.charset.StandardCharsets;
import java.util.Set;

public class FileRequestHandler implements Handler {
    private static final Logger logger = LoggerFactory.getLogger(FileRequestHandler.class);

    private static final String STATIC_FILE_PATH = "src/main/resources/static";

    private static final Set<String> RESTRICTED_PAGES = Set.of("/mypage", "/article");

    private final UserDataManager userDataManager;
    private final SessionDataManager sessionDataManager;

    public FileRequestHandler(UserDataManager userDataManager, SessionDataManager sessionDataManager) {
        this.userDataManager = userDataManager;
        this.sessionDataManager = sessionDataManager;
    }

    @Override
    public HttpResponse handle(HttpRequestInfo request) {
        String path = request.getPath();
        User user = getAuthenticatedUser(request);

        if (RESTRICTED_PAGES.contains(path) && user == null) {
            logger.error("Unauthorized access to: {}", path);
            throw new BaseException(FileErrorCode.FORBIDDEN_ACCESS);
        }

        // 디렉토리 요청이면 "/index.html" 추가
        if (!path.contains(".")) {
            path += "/index.html";
        }

        String fileExtension = FileUtil.getContentType(path);
        String content = FileUtil.readHtmlFileAsString(STATIC_FILE_PATH + path);

        // 로그인 상태라면 UI 수정
        if (user != null) {
            logger.debug("User logged in: {}", user);
            content = content.replace("<!--header_menu-->", generateLoggedInUserHeader(user.getNickname()));
        } else {
            content = content.replace("<!--header_menu-->", generateGuestUserMenu());
        }

        byte[] responseBody = content.getBytes(StandardCharsets.UTF_8);

        HttpResponse response = new HttpResponse();
        response.setStatus(HttpStatus.OK);
        response.setContentType(fileExtension);
        response.setBody(responseBody);

        return response;
    }

    private User getAuthenticatedUser(HttpRequestInfo request) {
        String sid = "";
        if (request.getCookie("sid") != null) {
            logger.debug("Found cookie: {}", request.getCookie("sid"));
            sid = request.getCookie("sid").getValue();
            if (sid.isEmpty()) return null;
        }

        String userId = sessionDataManager.findUserIdBySessionID(sid);
        if (userId == null) {
            logger.error("Login user not found");
            return null;
        }

        return userDataManager.findUserById(userId);
    }

    private StringBuilder generateLoggedInUserHeader(String nickname) {
        StringBuilder sb = new StringBuilder();

        sb.append("<ul class=\"header__menu\">")
                .append("<li class=\"header__menu__item\">")
                .append("<a class=\"btn btn_ghost btn_size_s\" href=\"/mypage\">안녕하세요, ").append(nickname).append("님</a>")
                .append("</li>")
                .append("<li class=\"header__menu__item\">")
                .append("<form action=\"/users/logout\" method=\"POST\">")
                .append("<button type=\"submit\" class=\"btn btn_ghost btn_size_s\">로그아웃</button>")
                .append("</form>")
                .append("</li>")
                .append("</ul>");

        return sb;
    }

    private StringBuilder generateGuestUserMenu() {
        StringBuilder sb = new StringBuilder();

        sb.append("<ul class=\"header__menu\">")
                .append("<li class=\"header__menu__item\">")
                .append("<a class=\"btn btn_contained btn_size_s\" href=\"/login\">로그인</a>")
                .append("</li>")
                .append("<li class=\"header__menu__item\">")
                .append("<a class=\"btn btn_ghost btn_size_s\" href=\"/registration\">회원 가입</a>")
                .append("</li>")
                .append("</ul>");

        return sb;
    }

}
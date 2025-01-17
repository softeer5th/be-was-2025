package handler;

import db.Database;
import db.SessionManager;
import exception.BaseException;
import exception.FileErrorCode;
import http.HttpRequestInfo;
import http.HttpStatus;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.FileUtil;
import http.HttpResponse;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Set;

public class FileRequestHandler implements Handler {
    private static final Logger logger = LoggerFactory.getLogger(FileRequestHandler.class);

    private static final String STATIC_FILE_PATH = "src/main/resources/static";

    private static final Set<String> RESTRICTED_PAGES = Set.of("/mypage");

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
        StringBuilder contentBuilder = new StringBuilder(content);

        // 로그인 상태라면 UI 수정
        if (user != null) {
            replaceLoginUI(contentBuilder, user.getNickname());
        }

        byte[] responseBody = contentBuilder.toString().getBytes(StandardCharsets.UTF_8);

        HttpResponse response = new HttpResponse();
        response.setStatus(HttpStatus.OK);
        response.setContentType(fileExtension);
        response.setBody(responseBody);

        return response;
    }

    private User getAuthenticatedUser(HttpRequestInfo request) {
        String sid = request.getSid();
        if (sid == null) return null;

        String userId = SessionManager.findUserBySessionID(sid);
        if (userId == null) return null;

        return Database.findUserById(userId);
    }

    private void replaceLoginUI(StringBuilder content, String nickname) {
        logger.debug("Executing replaceLoginUI");

        int startIndex = content.indexOf("<li class=\"header__menu__item\">");
        if (startIndex != -1) {
            int endIndex = content.indexOf("</ul>", startIndex);
            if (endIndex != -1) {
                StringBuilder newUI = new StringBuilder();
                newUI.append("<li class=\"header__menu__item\">")
                        .append("<a class=\"user-name\" href=\"/mypage\">사용자 : ").append(nickname).append("</a>")
                        .append("</li>")
                        .append("<li class=\"header__menu__item\">")
                        .append("<form action=\"/users/logout\" method=\"POST\">")
                        .append("<button type=\"submit\" class=\"btn btn_ghost btn_size_s\">로그아웃</button>")
                        .append("</form>")
                        .append("</li>");

                content.replace(startIndex, endIndex, newUI.toString());
            }
        }
    }
}
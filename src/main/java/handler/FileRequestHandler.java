package handler;

import db.Database;
import db.SessionManager;
import http.HttpRequestInfo;
import http.HttpStatus;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.FileUtil;
import http.HttpResponse;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class FileRequestHandler implements Handler {
    private static final Logger logger = LoggerFactory.getLogger(FileRequestHandler.class);

    private static final String STATIC_FILE_PATH = "src/main/resources/static";

    @Override
    public HttpResponse handle(HttpRequestInfo request) {
        String path = request.getPath();

        // 디렉토리 요청이면 "/index.html" 추가
        if (!path.contains(".")) {
            path += "/index.html";
        }

        String fileExtension = FileUtil.getContentType(path);
        String content = FileUtil.readHtmlFileAsString(STATIC_FILE_PATH + path);
        StringBuilder contentBuilder = new StringBuilder(content);

        updateLoginUIIfAuthenticated(request, contentBuilder);

        byte[] responseBody = contentBuilder.toString().getBytes(StandardCharsets.UTF_8);

        HttpResponse response = new HttpResponse();
        response.setStatus(HttpStatus.OK);
        response.setContentType(fileExtension);
        response.setBody(responseBody);

        return response;
    }

    private void updateLoginUIIfAuthenticated(HttpRequestInfo request, StringBuilder content) {
        String sid = request.getSid();
        if (sid == null) {
            logger.debug("sid is null");
            return; // 로그인되지 않은 경우 기본 UI 유지
        }

        String userId = SessionManager.findUserBySessionID(sid);
        if (userId == null) {
            logger.debug("userId is null");
            return; // 유효하지 않은 세션 → 기본 UI 유지
        }

        User user = Database.findUserById(userId);
        if (user == null) {
            logger.debug("user is null");
            return; // 사용자 정보 없음 → 기본 UI 유지
        }

        replaceLoginUI(content, user.getNickname());
    }

    private void replaceLoginUI(StringBuilder content, String nickname) {
        logger.debug("replaceLoginUtil 실행");

        String decodedNickname = URLDecoder.decode(nickname, StandardCharsets.UTF_8);

        int startIndex = content.indexOf("<li class=\"header__menu__item\">");
        logger.debug("StartIndex: {} ", startIndex);
        if (startIndex != -1) {
            int endIndex = content.indexOf("</ul>", startIndex);
            if (endIndex != -1) {
                StringBuilder newUI = new StringBuilder();
                newUI.append("<li class=\"header__menu__item\">")
                        .append("<a class=\"user-name\" href=\"/mypage\">").append(decodedNickname).append("</a>")
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

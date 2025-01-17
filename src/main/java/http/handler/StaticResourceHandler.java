package http.handler;

import com.sun.jdi.request.InvalidRequestStateException;
import db.Database;
import db.SessionDB;
import http.enums.ErrorMessage;
import http.enums.HttpResponseStatus;
import http.request.HttpRequest;
import http.request.TargetInfo;
import http.response.HttpResponse;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.FileUtil;
import util.HttpRequestUtil;
import util.JwtUtil;

public class StaticResourceHandler implements Handler {
    private final String staticResourcePath;
    private static final String LOGIN_PAGE_PATH = "/login/index.html";

    private static final Logger logger = LoggerFactory.getLogger(StaticResourceHandler.class);

    private static StaticResourceHandler instance;

    private StaticResourceHandler(String staticResourcePath) {
        this.staticResourcePath = staticResourcePath;
    }

    public static StaticResourceHandler getInstance(String staticResourcePath) {
        if (instance == null) {
            synchronized (StaticResourceHandler.class) {
                if (instance == null) {
                    instance = new StaticResourceHandler(staticResourcePath);
                }
            }
        }
        return instance;
    }

    @Override
    public HttpResponse handle(HttpRequest request) {
        TargetInfo target = request.getTarget();
        String path = staticResourcePath + target.getPath();
        HttpResponse response;
        HttpResponse.Builder builder = new HttpResponse.Builder();

        path = HttpRequestUtil.buildPath(path);
        String type = HttpRequestUtil.getType(path); // 파일 유형 별로 Content-Type 할당

        String body; // 해당 파일의 경로를 byte로 전달
        try {
            if (path.equals(staticResourcePath + "/mypage/index.html")) {
                String sid = HttpRequestUtil.getCookieValueByKey(request, "sid");
                String userId = JwtUtil.getIdFromToken(sid);
                User user = SessionDB.getUser(sid);
                Database.findUserById(userId);
                if (!userId.equals(user.getUserId())) throw new InvalidRequestStateException("로그인 되지 않은 사용자입니다.");
            }
            byte[] file = FileUtil.fileToByteArray(path);
            if (file != null) {
                body = new String(file);
                response = builder
                        .successResponse(HttpResponseStatus.OK, type, body)
                        .build();
            } else {
                response = builder
                        .errorResponse(HttpResponseStatus.NOT_FOUND, ErrorMessage.NOT_FOUND_PATH_AND_FILE)
                        .build();
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            response = builder
                    .redirectResponse(HttpResponseStatus.FOUND, LOGIN_PAGE_PATH)
                    .build();
        }
        return response;
    }
}

package webserver.handler;

import db.Database;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.enums.PageMappingPath;
import webserver.exception.BadRequest;
import webserver.exception.Conflict;
import webserver.request.HttpRequest;
import webserver.response.HttpResponse;

import java.util.Map;

// 회원가입 요청 처리
public class RegistrationHandler implements HttpHandler {

    private static final Logger log = LoggerFactory.getLogger(RegistrationHandler.class);

    @Override
    public HttpResponse handlePost(HttpRequest request) {
        Map<String, String> body = request.getBodyAsMap().orElseThrow(() -> new BadRequest("Invalid Request Body"));
        log.debug("body: {}", body);
        User user = mapToUser(body);
        // 중복 사용자 검사
        if (Database.findUserById(user.getUserId()) != null) {
            throw new Conflict("User Id Already Exists");
        }

        // 데이터베이스에 사용자 추가
        Database.addUser(user);
        return HttpResponse.redirect(PageMappingPath.INDEX.path);
    }

    // Map을 User 객체로 변환
    private User mapToUser(Map<String, String> query) {
        String userId = query.get("userId");
        String password = query.get("password");
        String name = query.get("name");
        String email = query.get("email");
        return new User(userId, password, name, email);
    }
}

package webserver.handler;

import db.Database;
import model.User;
import webserver.request.HttpRequest;
import webserver.response.HttpResponse;

import java.util.Map;

// 회원가입 요청 처리
public class RegistrationHandler implements HttpHandler {

    @Override
    public HttpResponse handleGet(HttpRequest request) {
        Map<String, String> query = request.getRequestTarget().getQuery();

        User user = mapToUser(query);
        // 데이터베이스에 사용자 추가
        Database.addUser(user);
        return HttpResponse.redirect("/login");
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

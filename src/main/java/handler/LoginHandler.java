package handler;

import db.Database;
import model.User;
import webserver.exception.BadRequest;
import webserver.handler.HttpHandler;
import webserver.request.HttpRequest;
import webserver.response.HttpResponse;
import webserver.session.HttpSession;

import java.util.Map;
import java.util.Optional;

import static webserver.enums.PageMappingPath.INDEX;

public class LoginHandler implements HttpHandler {
    private final Database database;

    public LoginHandler(Database database) {
        this.database = database;
    }

    @Override
    public HttpResponse handleGet(HttpRequest request) {
        return HttpResponse.render("/login/index.html");
    }

    @Override
    public HttpResponse handlePost(HttpRequest request) {
        Map<String, String> body = request.getBodyAsMap().orElseThrow(() -> new BadRequest("Invalid Request Body"));
        String userId = body.get("userId");
        String password = body.get("password");
        Optional<User> user = database.findUserById(userId);
        if (user.filter(u ->
                u.isPasswordCorrect(password)).isEmpty()) {
            return HttpResponse.redirect("/user/login_failed.html");
        }
        HttpSession session = request.getSession();
        session.set(HttpSession.USER_KEY, user.get());
        return HttpResponse.redirect(INDEX.path);
    }
}

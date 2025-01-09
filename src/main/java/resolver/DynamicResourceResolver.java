package resolver;

import db.Database;
import model.User;
import webserver.enumeration.HTTPContentType;
import webserver.enumeration.HTTPStatusCode;
import webserver.message.HTTPRequest;
import webserver.message.HTTPResponse;

public class DynamicResourceResolver implements ResourceResolver {
    private static final ResourceResolver nextResolver = StaticResourceResolver.getInstance();
    private static final DynamicResourceResolver instance = new DynamicResourceResolver();
    public static DynamicResourceResolver getInstance() {
        return instance;
    }
    @Override
    public void resolve(HTTPRequest request, HTTPResponse.Builder response) {
        try {
            resolveRegistration(request, response);
        } catch (IllegalArgumentException e) {
            nextResolver.resolve(request, response);
        }
    }

    private void resolveRegistration(HTTPRequest request, HTTPResponse.Builder response) {
        if (!request.getUri().equals("/user/create")) {
            throw new IllegalArgumentException("Invalid request");
        }
        String userId = request.getParameter("userId", String.class)
                .orElseThrow(() -> new IllegalArgumentException("No user id"));
        String nickname = request.getParameter("nickname", String.class)
                .orElseThrow(() -> new IllegalArgumentException("No nickname"));
        String password = request.getParameter("password", String.class)
                .orElseThrow(() -> new IllegalArgumentException("No password"));
        Database.addUser(new User(userId, password, nickname, "email"));
        response.statusCode(HTTPStatusCode.CREATED);
        response.body("CREATED".getBytes());
        response.contentType(HTTPContentType.TEXT_PLAIN);
    }
}

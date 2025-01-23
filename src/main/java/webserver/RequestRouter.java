package webserver;

import handler.GetHandler;
import handler.PostHandler;
import http.*;

import java.io.*;
import java.util.*;
import java.util.function.BiConsumer;

public class RequestRouter {
    private final Map<HttpMethod, BiConsumer<HttpRequest, DataOutputStream>> getHandler = new HashMap<>();
    private final Map<String, BiConsumer<HttpRequest, DataOutputStream>> postHandlers = new HashMap<>();
    private static final String SIGNIN_PATH = "/user/signIn";
    private static final String SIGNUP_PATH = "/user/create";
    private static final String LOGOUT_PATH = "/user/logout";
    private static final String USER_INFO_PATH = "/user/info";
    private static final String CREAT_POST_PATH = "/post/create";
    private static final String USER_INFO_UPDATE_PATH = "/user/update";

    public RequestRouter() {
        init();
    }

    private void init() {
        this.addGetHandler(GetHandler::handleGetRequest);
        this.addPostHandler(SIGNUP_PATH, PostHandler::handleSignUp);
        this.addPostHandler(SIGNIN_PATH, PostHandler::handleSignIn);
        this.addPostHandler(LOGOUT_PATH, PostHandler::handleLogout);
        this.addPostHandler(USER_INFO_PATH, PostHandler::handleUserInfo);
        this.addPostHandler(CREAT_POST_PATH, PostHandler::handleCreatePost);
        this.addPostHandler(USER_INFO_UPDATE_PATH, PostHandler::handleUpdateUserInfo);
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
}
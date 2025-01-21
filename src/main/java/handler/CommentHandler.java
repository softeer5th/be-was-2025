package handler;

import enums.FileContentType;
import enums.HttpHeader;
import enums.HttpStatus;
import exception.ClientErrorException;
import exception.ErrorCode;
import manager.CommentManager;
import manager.UserManager;
import model.User;
import request.HttpRequestInfo;
import response.HttpResponse;
import util.CookieParser;
import util.HttpRequestParser;

import java.util.Map;

import static enums.HttpMethod.POST;
import static exception.ErrorCode.INVALID_AUTHORITY;
import static exception.ErrorCode.NOT_ALLOWED_PATH;

public class CommentHandler implements Handler {
    private final CommentManager commentManager;
    private final UserManager userManager;

    public CommentHandler() {
        this.commentManager = CommentManager.getInstance();
        this.userManager = UserManager.getInstance();
    }

    @Override
    public HttpResponse handle(HttpRequestInfo request) {
        HttpResponse response = new HttpResponse();
        if (request.getPath().startsWith("/comment/write") && request.getMethod() == POST) {
            final String[] split = request.getPath().split("/");
            if (split.length != 4)
                throw new ClientErrorException(NOT_ALLOWED_PATH);
            int postId = Integer.parseInt(split[3]);
            final Map<String, String> map = HttpRequestParser.parseParamString((String) request.getBody());
            final String comment = map.get("comment");

            final String cookie = request.getHeaderValue(HttpHeader.COOKIE.getName());
            final User author = userManager.getUserFromSession(CookieParser.parseCookie(cookie))
                    .orElseThrow(() -> new ClientErrorException(INVALID_AUTHORITY));

            commentManager.save(postId, comment, author.getName());

            response.setResponse(HttpStatus.FOUND, FileContentType.HTML_UTF_8);
            response.setHeader(HttpHeader.LOCATION.getName(), "/");
        } else {
            throw new ClientErrorException(ErrorCode.NOT_ALLOWED_PATH);
        }
        return response;
    }
}

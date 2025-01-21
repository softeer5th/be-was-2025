package handler;

import enums.FileContentType;
import enums.HttpHeader;
import enums.HttpStatus;
import exception.ClientErrorException;
import exception.ErrorCode;
import manager.BoardManager;
import manager.UserManager;
import model.User;
import request.HttpRequestInfo;
import response.HttpResponse;
import util.CookieParser;
import util.HttpRequestParser;

import static enums.HttpMethod.POST;
import static enums.HttpMethod.validPostMethod;
import static exception.ErrorCode.*;

public class BoardHandler implements Handler {
    private final BoardManager boardManager;
    private final UserManager userManager;

    public BoardHandler() {
        this.boardManager = BoardManager.getInstance();
        this.userManager = UserManager.getInstance();
    }

    @Override
    public HttpResponse handle(HttpRequestInfo request) {
        HttpResponse response = new HttpResponse();
        if (request.getPath().equals("/board") && request.getMethod() == POST) {
            final String contents = HttpRequestParser.parseMultipartFormText(request.getHeaderValue("content-type"),(String) request.getBody());

            final String cookie = request.getHeaderValue(HttpHeader.COOKIE.getName());
            final User author = userManager.getUserFromSession(CookieParser.parseCookie(cookie))
                    .orElseThrow(() -> new ClientErrorException(INVALID_AUTHORITY));
            boardManager.save(contents, author.getName());
            response.setResponse(HttpStatus.FOUND, FileContentType.HTML_UTF_8);
            response.setHeader(HttpHeader.LOCATION.getName(), "/");
        } else if (request.getPath().startsWith("/board/like")) {
            validPostMethod(request.getMethod());
            final String[] split = request.getPath().split("/");
            if (split.length != 4)
                throw new ClientErrorException(NOT_ALLOWED_PATH);
            int postId = Integer.parseInt(split[3]);

            final String cookie = request.getHeaderValue(HttpHeader.COOKIE.getName());
            User user = userManager.getUserFromSession(CookieParser.parseCookie(cookie))
                    .orElseThrow(() -> new ClientErrorException(INVALID_AUTHORITY));


            boardManager.likePost(postId, user.getId());

            response.setResponse(HttpStatus.FOUND, FileContentType.HTML_UTF_8);
            response.setHeader(HttpHeader.LOCATION.getName(), "/");
        }  else if (request.getPath().startsWith("/board/mark")) {
            validPostMethod(request.getMethod());
            final String[] split = request.getPath().split("/");
            if (split.length != 4)
                throw new ClientErrorException(NOT_ALLOWED_PATH);
            int postId = Integer.parseInt(split[3]);

            final String cookie = request.getHeaderValue(HttpHeader.COOKIE.getName());
            User user = userManager.getUserFromSession(CookieParser.parseCookie(cookie))
                    .orElseThrow(() -> new ClientErrorException(INVALID_AUTHORITY));


            boardManager.bookmarkPost(postId, user.getId());

            response.setResponse(HttpStatus.FOUND, FileContentType.HTML_UTF_8);
            response.setHeader(HttpHeader.LOCATION.getName(), "/");
        } else {
            throw new ClientErrorException(ErrorCode.NOT_ALLOWED_PATH);
        }
        return response;
    }
}

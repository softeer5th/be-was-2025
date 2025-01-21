package handler;

import enums.FileContentType;
import enums.HttpHeader;
import enums.HttpStatus;
import exception.ClientErrorException;
import exception.ErrorCode;
import manager.BoardManager;
import manager.UserManager;
import request.HttpRequestInfo;
import response.HttpResponse;
import util.CookieParser;
import util.HttpRequestParser;

import static enums.HttpMethod.POST;
import static exception.ErrorCode.INVALID_AUTHORITY;

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
            final String contents = HttpRequestParser.parseMultipartFormText((String) request.getBody());
            final String cookie = request.getHeaderValue(HttpHeader.COOKIE.getName());
            final String author = userManager.getNameFromSession(CookieParser.parseCookie(cookie))
                    .orElseThrow(() -> new ClientErrorException(INVALID_AUTHORITY));
            boardManager.save(contents,author);
            response.setResponse(HttpStatus.FOUND, FileContentType.HTML_UTF_8);
            response.setHeader(HttpHeader.LOCATION.getName(), "/");
        }
        else{
            throw new ClientErrorException(ErrorCode.NOT_ALLOWED_PATH);
        }
        return response;
    }
}

package handler;

import enums.FileContentType;
import enums.HttpHeader;
import enums.HttpStatus;
import exception.ClientErrorException;
import exception.ErrorCode;
import manager.BoardManager;
import request.HttpRequestInfo;
import response.HttpResponse;
import util.HttpRequestParser;

import static enums.HttpMethod.POST;

public class BoardHandler implements Handler {
    private final BoardManager boardManager;

    public BoardHandler() {
        this.boardManager = BoardManager.getInstance();
    }

    @Override
    public HttpResponse handle(HttpRequestInfo request) {
        HttpResponse response = new HttpResponse();
        if (request.getPath().equals("/board") && request.getMethod() == POST) {
            final String contents = HttpRequestParser.parseMultipartFormText((String) request.getBody());
            boardManager.save(contents);
            response.setResponse(HttpStatus.FOUND, FileContentType.HTML_UTF_8);
            response.setHeader(HttpHeader.LOCATION.getName(), "/");
        }
        else{
            throw new ClientErrorException(ErrorCode.NOT_ALLOWED_PATH);
        }
        return response;
    }
}

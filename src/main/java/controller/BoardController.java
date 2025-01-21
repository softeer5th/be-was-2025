package controller;

import db.BoardDao;
import db.UserDao;
import model.Board;
import tag.HeaderMenu;
import wasframework.HttpSession;
import wasframework.Mapping;
import wasframework.PathVariable;
import webserver.httpserver.HttpMethod;
import webserver.httpserver.HttpRequest;
import webserver.httpserver.HttpResponse;
import webserver.httpserver.header.Cookie;

import java.io.File;
import java.io.IOException;

import static tag.BoardBody.renderBoard;
import static tag.HeaderMenu.renderHeaderMenu;
import static utils.FileUtils.getFile;
import static utils.FileUtils.getFileAsString;
import static wasframework.HttpSession.SESSION_ID;

public class BoardController {

    @Mapping(path = "/board/{id}", method = HttpMethod.GET)
    public void boardPage(@PathVariable("id") long id, HttpRequest request, HttpResponse response) throws IOException {
        response.setHeader("Content-Type", "text/html; charset=utf-8");
        BoardDao boardDao = BoardDao.BOARDS;
        UserDao userDao = UserDao.USERS;
        if (boardDao.getBoardSize() < id) {
            File file = new File("src/main/resources/static/error/404.html");
            byte[] readFile = getFile(file);
            response.setBody(readFile);
            return;
        }

        Cookie cookie = request.getCookie();
        String sessionId = cookie.getCookie(SESSION_ID);

        String userId = HttpSession.get(sessionId);
        File file = new File("src/main/resources/static/board/index.html");
        String readFile = getFileAsString(file);

        Board findBoard = boardDao.findById(id).orElseThrow(IllegalArgumentException::new);
        Long boardId = findBoard.getBoardId();
        String contents = findBoard.getContents();
        String writer = findBoard.getWriter();
        String writerProfileImagePath = "userProfileImages/" + writer;
        String imagePath = findBoard.getImagePath();


        String rendered = renderHeaderMenu(readFile, sessionId, userId);
        rendered = renderBoard(rendered, boardId, contents, writer, writerProfileImagePath, imagePath);
        response.setBody(rendered.getBytes());
    }
}

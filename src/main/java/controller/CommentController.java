package controller;

import db.BoardDao;
import db.CommentDao;
import db.UserDao;
import exception.BadRequestException;
import model.Board;
import model.Comment;
import model.User;
import wasframework.HttpSession;
import wasframework.Mapping;
import wasframework.PathVariable;
import webserver.httpserver.HttpMethod;
import webserver.httpserver.HttpRequest;
import webserver.httpserver.HttpResponse;
import webserver.httpserver.header.Cookie;

import java.io.File;
import java.io.IOException;
import java.util.NoSuchElementException;

import static tag.HeaderMenu.renderHeaderMenu;
import static utils.FileUtils.getFileAsString;
import static wasframework.HttpSession.SESSION_ID;

public class CommentController {

    public static final String CONTENTS = "contents";

    @Mapping(path = "/comment/{id}", method = HttpMethod.GET)
    public void commentPage(@PathVariable("id") Long boardId, HttpRequest request, HttpResponse response) throws IOException {
        response.setHeader("Content-Type", "text/html; charset=utf-8");

        Cookie cookie = request.getCookie();
        String sessionId = cookie.getCookie(SESSION_ID);

        File file = new File("src/main/resources/static/comment/index.html");
        String readFile = getFileAsString(file);

        String userId = HttpSession.get(sessionId);
        String rendered = renderHeaderMenu(readFile, sessionId, userId).replace("${id}", String.valueOf(boardId));

        response.setBody(rendered.getBytes());
    }

    @Mapping(path = "/comment/{id}", method = HttpMethod.POST)
    public void writeComment(@PathVariable("id") Long boardId, HttpRequest request, HttpResponse response) {
        response.setHeader("Content-Type", "text/html; charset=utf-8");

        Cookie cookie = request.getCookie();
        String sessionId = cookie.getCookie(SESSION_ID);

        String userId = HttpSession.get(sessionId);
        if (sessionId == null || userId == null) {
            response.setLocation("/login");
            return;
        }

        CommentDao commentDao = CommentDao.COMMENTS;
        UserDao userDao = UserDao.USERS;

        String contents = request.getParameter(CONTENTS);
        User commenter = userDao.findById(userId).orElseThrow(() -> new BadRequestException("존재하지 않는 사용자입니다."));


        Comment comment = commentDao.save(new Comment(contents, commenter.getUserId(), commenter.getProfileImage(), boardId))
                .orElseThrow(NoSuchElementException::new);
        response.setLocation("/board/" + boardId);
    }
}

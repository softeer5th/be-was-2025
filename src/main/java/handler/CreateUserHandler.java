package handler;

import util.RequestParser;
import util.UserManeger;

import java.io.DataOutputStream;
import java.io.IOException;

public class CreateUserHandler implements Handler{
    private static final String login = "/login";
    private static final String registration = "/registration";
    @Override
    public void handle(DataOutputStream dos, RequestParser requestParser) throws IOException {
        try {
            UserManeger userManeger = new UserManeger();
            userManeger.addUser(requestParser.parameter);
            dos.writeBytes("HTTP/1.1 303 See Other \r\n");
            dos.writeBytes("Location: " + login + " \r\n");
            dos.writeBytes("\r\n");
            dos.flush();
        } catch (IllegalArgumentException e) {
            dos.writeBytes("HTTP/1.1 303 See Other \r\n");
            dos.writeBytes("Location: " + registration + " \r\n");
            dos.writeBytes("\r\n");
            dos.flush();
        }
    }
}

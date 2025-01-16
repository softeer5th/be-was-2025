package handler;


import webserver.request.Request;

import java.io.DataOutputStream;
import java.io.IOException;

public interface Handler {
    default void handle(DataOutputStream dos, Request request) throws IOException {
    }
}

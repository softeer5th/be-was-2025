package handler;


import webserver.request.Request;
import webserver.response.Response;

import java.io.DataOutputStream;
import java.io.IOException;

public interface Handler {
    default Response handle(Request request) {
        return null;
    }
}

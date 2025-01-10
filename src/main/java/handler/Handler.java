package handler;


import util.RequestParser;

import java.io.DataOutputStream;
import java.io.IOException;

public interface Handler {
    default void handle(DataOutputStream dos, RequestParser requestParser) throws IOException {
    }
}

package webserver;

import util.RequestInfo;

import java.io.DataOutputStream;
import java.io.IOException;

public interface Handler {
    void handle(RequestInfo request, DataOutputStream dataOutputStream) throws IOException;
}

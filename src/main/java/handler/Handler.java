package handler;

import util.HttpResponse;
import util.RequestInfo;

public interface Handler {
    HttpResponse handle(RequestInfo request);
}

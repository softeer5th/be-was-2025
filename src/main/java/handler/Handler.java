package handler;


import webserver.request.Request;
import webserver.response.Response;


public abstract class Handler {
    protected String sessionId;

    public abstract Response handle(Request request);

    public void setSessionId(String sessionId){
        this.sessionId = sessionId;
    }
}

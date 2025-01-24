package webserver.http;

import webserver.http.cookie.Cookie;
import webserver.session.HttpSession;
import webserver.session.SessionManager;

import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    private HttpResponse response;
    private String method;
    private String path;
    private final Map<String, String> params = new HashMap<>();
    private String version;
    private final Map<String, String> headers = new HashMap<>();
    private byte[] body;
    private String sessionId;
    private final Map<String, Cookie> cookies = new HashMap<>();

    public byte[] getBody() { return body; }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getVersion() {
        return version;
    }

    public String getHeader(String name) {
        return headers.get(name.toLowerCase());
    }

    public String getParameter(String name) {return params.get(name);}

    public Cookie getCookie(String name) { return cookies.get(name.toLowerCase()); }

    public Cookie[] getCookies() { return (Cookie[]) cookies.values().toArray(); }

    public void setResponse(HttpResponse response) { this.response = response; }

    public void setMethod(String method) { this.method = method; }

    public void setPath(String path) { this.path = path; }

    public void setVersion(String version) { this.version = version; }

    public void setHeader(String name, String value) { this.headers.put(name.toLowerCase(), value.toLowerCase()); }

    public void setParameter(String name, String value) { this.params.put(name, value); }

    public void setBody(byte[] body) { this.body = body; }

    public void addCookie(Cookie cookie) { this.cookies.put(cookie.getName().toLowerCase(), cookie); }

    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public HttpSession getSession() { return getSession(true); }

    public HttpSession getSession(boolean create) {
        SessionManager manager = SessionManager.getManager();
        HttpSession session = manager.getSession(sessionId);

        if(create) {
            if(session != null) {
                session.setNew(false);
            } else {
                session = manager.createNewSession();
                this.sessionId = session.getSessionId();
                Cookie cookie = new Cookie(HttpSession.SESSION_NAME, session.getSessionId());
                cookie.setPath("/");
                cookie.setDomain("localhost");
                response.addCookie(cookie);
            }
        }
        
        return session;
    }
}


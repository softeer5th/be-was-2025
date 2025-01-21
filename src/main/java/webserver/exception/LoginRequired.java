package webserver.exception;

public class LoginRequired extends RuntimeException {
    private final String redirectLocation;

    public LoginRequired(String redirectLocation) {
        super();
        this.redirectLocation = redirectLocation;
    }

    public String getRedirectLocation() {
        return redirectLocation;
    }
}

package util.enums;

public enum Page {
    MAIN_PAGE("/"),
    MAIN_LOGIN("/main"),
    LOGIN("/login"),
    REGISTRATION("/registration");

    private final String path;

    Page(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}

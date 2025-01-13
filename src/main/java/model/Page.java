package model;

public enum Page {
    MAIN_PAGE("/"),
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

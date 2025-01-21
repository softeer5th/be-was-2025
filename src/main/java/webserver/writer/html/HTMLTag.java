package webserver.writer.html;

public enum HTMLTag {
    P("p"),
    DIV("div"),
    PLAIN("");

    HTMLTag(String name) {
        this.name = name;
    }
    private final String name;

    @Override
    public String toString() {
        return name;
    }
}

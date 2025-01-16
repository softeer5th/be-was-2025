package util.enums;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum CookieName {
    SESSION_COOKIE("sid"),
    IDEA_COOKIE("Idea-69015726");

    private final String name;

    CookieName(String name){
        this.name = name;
    }

    private static final Map<String, CookieName> BY_NAME =
            Stream.of(values()).collect(Collectors.toMap(CookieName::getName, e -> e));

    public String getName(){
        return name;
    }

    public static String isValid(String name) {
        if (!BY_NAME.containsKey(name)) {
            throw new IllegalArgumentException("등록되지 않은 쿠키입니다: " + name);
        }
        return name;
    }
}

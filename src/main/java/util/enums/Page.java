package util.enums;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum Page {
    MAIN_PAGE("/"),
    MAIN_LOGIN("/main"),
    LOGIN("/login"),
    REGISTRATION("/registration"),
    MY_PAGE("/mypage"),
    ARTICLE("/article"),
    COMMENT("/comment");

    private final String path;

    Page(String path) {
        this.path = path;
    }

    private static final Set<String> PATH_REQUIRING_LOGIN =
            Stream.of(MAIN_LOGIN.getPath(), MY_PAGE.getPath(), ARTICLE.getPath(), COMMENT.getPath())
            .collect(Collectors.toSet());

    public String getPath() {
        return path;
    }

    public static boolean isRequiringLogin(String path){
        return PATH_REQUIRING_LOGIN.contains(path);
    }
}

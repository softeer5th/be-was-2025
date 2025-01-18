package webserver.enums;

// 페이지 매핑 경로
public enum PageMappingPath {
    INDEX("/"),
    ARTICLES("/article"),
    COMMENT("/comment"),
    LOGIN("/login"),
    LOGOUT("/logout"),
    MYPAGE("/mypage"),
    REGISTRATION("/registration");

    public final String path;

    PageMappingPath(String path) {
        this.path = path;
    }

}

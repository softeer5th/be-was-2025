package enums;

// 페이지 매핑 경로
public enum PageMappingPath {
    INDEX("/"),
    READ_ARTICLE("/article/{articleId}"),
    WRITE_ARTICLE("/article"),
    WRITE_COMMENT("/comment"),
    LOGIN("/login"),
    LOGOUT("/logout"),
    MYPAGE("/mypage"),
    REGISTRATION("/registration");

    public final String path;

    PageMappingPath(String path) {
        this.path = path;
    }

    public static String readArticlePath(Long articleId) {
        return READ_ARTICLE.path.replace("{articleId}", articleId.toString());
    }
}

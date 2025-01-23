package enums;

/**
 * 페이지 매핑 경로를 관리하는 enum
 */
public enum PageMappingPath {
    INDEX("/"),
    READ_ARTICLE("/article/{articleId}"),
    WRITE_ARTICLE("/article"),
    WRITE_COMMENT("/article/{articleId}/comment"),
    LOGIN("/login"),
    LOGOUT("/logout"),
    MYPAGE("/mypage"),
    REGISTRATION("/registration");

    public final String path;

    PageMappingPath(String path) {
        this.path = path;
    }

    /**
     * 게시글 조회 경로를 반환한다.
     *
     * @param articleId 게시글 아이디
     * @return 게시글 조회 경로
     */
    public static String readArticlePath(Long articleId) {
        return READ_ARTICLE.path.replace("{articleId}", articleId.toString());
    }

}

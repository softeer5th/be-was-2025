package tag;

public enum BoardBody {
    BOARD_BODY;

    /**
     * 게시글을 렌더링하는 메소드
     * @param readFile 현재 출력해야 하는 HTML 문자열
     * @param boardId 이전과 다음 게시글을 링크할 게시글 ID
     * @param contents 게시글 내용
     * @param writer 글 작성자
     * @param writerProfileImagePath 게시글 작성자의 프로필 사진 경로
     * @param imagePath 게시글의 사진 경로
     * @return 렌더링된 HTML 문자열
     */
    public static String renderBoard(String readFile, Long boardId, String contents, String writer, String writerProfileImagePath, String imagePath){
        String prevPage = boardId > 1 ? "/board/" + (boardId - 1) : "/";
        String nextPage = "/board/" + (boardId + 1);
        String renderedImagePath = imagePath != null ? "src=\"" + imagePath + "\"" : "";
        String renderedWriterProfileImagePath = "src=\"" + writerProfileImagePath+ "\"";
        return readFile.replace("${before}", prevPage)
                .replace("${after}", nextPage)
                .replace("${contents}", contents)
                .replace("${image}", renderedImagePath)
                .replace("${writer_profile}", renderedWriterProfileImagePath)
                .replace("${writer}", writer);
    }
}

package tag;

import model.Comment;

import java.util.List;

public enum BoardBody {
    BOARD_BODY;

    /**
     * 게시글을 렌더링하는 메소드
     *
     * @param readFile               현재 출력해야 하는 HTML 문자열
     * @param boardId                이전과 다음 게시글을 링크할 게시글 ID
     * @param contents               게시글 내용
     * @param writer                 글 작성자
     * @param writerProfileImagePath 게시글 작성자의 프로필 사진 경로
     * @param imagePath              게시글의 사진 경로
     * @return 렌더링된 HTML 문자열
     */
    public static String renderBoard(String readFile, Long boardId, String contents, String writer, String writerProfileImagePath, String imagePath, List<Comment> comments) {
        String prevPage = boardId > 1 ? "/board/" + (boardId - 1) : "/";
        String nextPage = "/board/" + (boardId + 1);
        String renderedImagePath = imagePath != null ? "src=\"" + imagePath + "\"" : "";
        String renderedWriterProfileImagePath = "src=\"" + writerProfileImagePath + "\"";

        StringBuilder sb = new StringBuilder();
        for (Comment comment : comments) {
            sb.append("""
                    <li class="comment__item">
                            <div class="comment__item__user">
                              <img class="comment__item__user__img" ${commenterProfile}/>
                              <p class="comment__item__user__nickname">${commenter}</p>
                            </div>
                            <p class="comment__item__article">
                              ${commentContents}
                            </p>
                          </li>
                    """.replace("${commenterProfile}", "src=\"" + comment.getCommenterProfile() + "\"").replace("${commenter}", comment.getCommenter()).replace("${commentContents}", comment.getContents()));
        }

        return readFile.replace("${before}", prevPage).replace("${after}", nextPage).replace("${contents}", contents).replace("${image}", renderedImagePath).replace("${writerProfile}", renderedWriterProfileImagePath).replace("${writer}", writer).replace("${comments}", sb.toString());

    }
}

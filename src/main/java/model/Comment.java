package model;

public class Comment {
    private Long commentId;
    private String contents;
    private String commenter;
    private String commenterProfile;
    private Long boardId;

    public Comment(String contents, String commenter, String commenterProfile, Long boardId) {
        this.contents = contents;
        this.commenter = commenter;
        this.commenterProfile = commenterProfile;
        this.boardId = boardId;
    }

    public Comment(Long commentId, String contents, String commenter, String commenterProfile, Long boardId) {
        this.commentId = commentId;
        this.contents = contents;
        this.commenter = commenter;
        this.commenterProfile = commenterProfile;
        this.boardId = boardId;
    }

    public Long getCommentId() {
        return commentId;
    }

    public String getContents() {
        return contents;
    }

    public String getCommenter() {
        return commenter;
    }

    public String getCommenterProfile() {
        return commenterProfile;
    }

    public Long getBoardId() {
        return boardId;
    }

    public void changeCommenterProfile(String newPath){
        commenterProfile = newPath;
    }
}

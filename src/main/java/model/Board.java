package model;

public class Board {
    private Long boardId;
    private String writer;
    private String subject;
    private String contents;
    private String imagePath;

    public Board(Long boardId, String writer, String subject, String contents, String imagePath) {
        this.boardId = boardId;
        this.writer = writer;
        this.subject = subject;
        this.contents = contents;
        this.imagePath = imagePath;
    }

    public Long getBoardId() {
        return boardId;
    }

    public String getWriter() {
        return writer;
    }

    public String getSubject() {
        return subject;
    }

    public String getContents() {
        return contents;
    }

    public String getImagePath() {
        return imagePath;
    }
}

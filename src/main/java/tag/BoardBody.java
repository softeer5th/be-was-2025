package tag;

public enum BoardBody {
    BOARD_BODY;

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

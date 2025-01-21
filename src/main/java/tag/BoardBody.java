package tag;

public enum BoardBody {
    BOARD_BODY;

    public static String renderBoard(String readFile, Long boardId, String contents, String writer, String writerProfileImagePath, String imagePath){
        String prevPage = boardId > 1 ? "/board/" + (boardId - 1) : "/";
        String nextPage = "/board/" + (boardId + 1);
        return readFile.replace("${before}", prevPage)
                .replace("${after}", nextPage)
                .replace("${contents}", contents)
                .replace("${image}", imagePath)
                .replace("${writer_profile}", writerProfileImagePath)
                .replace("${writer}", writer);
    }
}

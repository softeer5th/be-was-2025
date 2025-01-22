package manager;

import db.PostBookMarkDatabase;
import db.PostDatabase;
import db.PostLikeDatabase;
import exception.ClientErrorException;
import model.Post;

import static exception.ErrorCode.*;

/**
 * 게시판에서 포스트를 관리하는 클래스.
 * <p>
 * 이 클래스는 포스트 생성, 좋아요, 북마크 기능을 관리하며,
 * 각 작업에 대한 유효성 검사와 데이터베이스 상호작용을 수행합니다.
 * </p>
 */
public class BoardManager {
    private final PostDatabase postDatabase;
    private final PostLikeDatabase postLikeDatabase;
    private final PostBookMarkDatabase postBookMarkDatabase;
    private static BoardManager instance;

    /**
     * 생성자. 데이터베이스 인스턴스를 초기화합니다.
     */
    private BoardManager() {
        postDatabase = PostDatabase.getInstance();
        postLikeDatabase = PostLikeDatabase.getInstance();
        postBookMarkDatabase = PostBookMarkDatabase.getInstance();
    }

    /**
     * BoardManager의 단일 인스턴스를 반환합니다. 싱글턴 패턴을 사용합니다.
     *
     * @return BoardManager 인스턴스
     */
    public static BoardManager getInstance() {
        if (instance == null) {
            instance = new BoardManager();
        }
        return instance;
    }

    /**
     * 게시글을 저장합니다. 게시글의 내용과 작성자가 유효한지 검증합니다.
     *
     * @param content 게시글의 내용
     * @param author 게시글 작성자
     * @throws ClientErrorException 내용이 비어있거나 너무 긴 경우 예외 발생
     */
    public void save(String content, String author) {
        if (content.length() > 500)
            throw new ClientErrorException(EXCEED_POST_LENGTH);
        if (content.isEmpty())
            throw new ClientErrorException(MISSING_INPUT);
        Post post = new Post(content, author);
        postDatabase.addPost(post);
    }

    /**
     * 게시판의 페이지 수를 반환합니다.
     *
     * @return 총 페이지 수
     */
    public int getPageSize() {
        return postDatabase.getTotalPages();
    }

    /**
     * 지정된 페이지 번호에 해당하는 게시글을 반환합니다.
     *
     * @param page 페이지 번호
     * @return 해당 페이지의 게시글
     */
    public Post getPostByPage(int page) {
        return postDatabase.getPost(page);
    }

    /**
     * 게시글에 좋아요를 추가합니다. 이미 좋아요가 추가된 경우 예외를 발생시킵니다.
     *
     * @param postId 게시글 ID
     * @param userId 사용자 ID
     * @throws ClientErrorException 이미 좋아요가 추가된 경우 예외 발생
     */
    public void likePost(int postId, int userId) {
        if (postLikeDatabase.existsPostLike(postId, userId))
            throw new ClientErrorException(ALREADY_LIKE_POST);
        postLikeDatabase.addPostLike(postId, userId);
    }

    /**
     * 게시글에 북마크를 추가합니다. 이미 북마크가 추가된 경우 예외를 발생시킵니다.
     *
     * @param postId 게시글 ID
     * @param userId 사용자 ID
     * @throws ClientErrorException 이미 북마크가 추가된 경우 예외 발생
     */
    public void bookmarkPost(int postId, int userId) {
        if (postBookMarkDatabase.existsBookMark(postId, userId))
            throw new ClientErrorException(ALREADY_MARK_POST);
        postBookMarkDatabase.addBookMark(postId, userId);
    }

    /**
     * 특정 게시글에 대해 좋아요가 존재하는지 여부를 반환합니다.
     *
     * @param postId 게시글 ID
     * @param userId 사용자 ID
     * @return 좋아요 여부
     */
    public boolean existsPostLike(int postId, int userId) {
        return postLikeDatabase.existsPostLike(postId, userId);
    }

    /**
     * 특정 게시글에 대해 북마크가 존재하는지 여부를 반환합니다.
     *
     * @param postId 게시글 ID
     * @param userId 사용자 ID
     * @return 북마크 여부
     */
    public boolean existsPostBookMark(int postId, int userId) {
        return postBookMarkDatabase.existsBookMark(postId, userId);
    }
}

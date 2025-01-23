package manager;

import db.CommentDatabase;
import exception.ClientErrorException;
import model.Comment;
import model.User;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static exception.ErrorCode.EXCEED_POST_LENGTH;
import static exception.ErrorCode.MISSING_INPUT;

/**
 * 댓글 관리 클래스.
 * <p>
 * 이 클래스는 게시글에 대한 댓글을 저장하고, 댓글의 유효성 검사를 수행합니다.
 * 또한 게시글 ID에 해당하는 댓글들을 조회할 수 있는 기능을 제공합니다.
 * </p>
 */
public class CommentManager {
    private final CommentDatabase commentDatabase;
    private static CommentManager instance;

    /**
     * 생성자. 댓글 데이터베이스 인스턴스를 초기화합니다.
     */
    private CommentManager() {
        commentDatabase = CommentDatabase.getInstance();
    }

    /**
     * CommentManager의 단일 인스턴스를 반환합니다. 싱글턴 패턴을 사용합니다.
     *
     * @return CommentManager 인스턴스
     */
    public static CommentManager getInstance() {
        if (instance == null) {
            instance = new CommentManager();
        }
        return instance;
    }

    /**
     * 댓글을 저장합니다. 댓글 내용이 유효한지 검증합니다.
     *
     * @param postId 게시글 ID
     * @param content 댓글 내용
     * @param author 댓글 작성자
     * @throws ClientErrorException 댓글 내용이 비어있거나 너무 긴 경우 예외 발생
     */
    public void save(int postId, String content, User author) {
        String decodedContent = URLDecoder.decode(content, StandardCharsets.UTF_8);

        if (decodedContent.length() > 500)
            throw new ClientErrorException(EXCEED_POST_LENGTH);
        if (decodedContent.trim().isBlank())
            throw new ClientErrorException(MISSING_INPUT);

        Comment comment = new Comment(postId, decodedContent, author.getId());
        commentDatabase.addComment(comment);
    }

    /**
     * 특정 게시글에 대한 모든 댓글을 조회합니다.
     *
     * @param postId 게시글 ID
     * @return 해당 게시글에 달린 댓글 리스트
     */
    public List<Comment> getCommentsByPostId(int postId) {
        return commentDatabase.findAllByPostId(postId);
    }
}

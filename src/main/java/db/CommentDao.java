package db;

import model.Board;
import model.Comment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static db.DBUtils.close;
import static db.DBUtils.getConnection;

public enum CommentDao {
    COMMENTS;

    private static final Logger log = LoggerFactory.getLogger(CommentDao.class);

    /**
     * id와 매칭되는 게시글을 찾아주는 메소드
     * @param boardId 찾고자 하는 댓글을 가진 board ID
     * @return comment 리스트 객체 반환. 해당 ID가 존재하지 않을 경우, 빈 리스트 반환
     */
    public List<Comment> findAllByBoardId(Long boardId) {
        String sql = "select * from comments where board_id = ?";
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet resultSet = null;
        try{
            con = getConnection();
            pstmt = con.prepareStatement(sql);

            pstmt.setLong(1, boardId);
            resultSet = pstmt.executeQuery();

            List<Comment> comments = new ArrayList<>();
            while(resultSet.next()){
                Long findCommentId = resultSet.getLong("comment_id");
                String findContents = resultSet.getString("contents");
                String findCommenterProfilePath= resultSet.getString("commenter_profile_path");
                String findCommenter = resultSet.getString("commenter");
                comments.add(new Comment(findCommentId, findContents, findCommenter, findCommenterProfilePath, boardId));
            }
            return comments;
        } catch (SQLException e) {
            log.error("find 예외: ", e);
        }finally {
            close(resultSet, pstmt, con);
        }
        return List.of();
    }
    /**
     * 새 댓글을 저장하는 메소드
     * @param comment id값이 없는 새로 저장할 comment 객체
     * @return
     */
    public Optional<Comment> save(Comment comment){
        String sql = "insert into comments(contents, commenter, commenter_profile_path, board_id) values (?, ?, ?, ?)";
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try{
            con = getConnection();
            pstmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, comment.getContents());
            pstmt.setString(2, comment.getCommenter());
            pstmt.setString(3, comment.getCommenterProfile());
            pstmt.setLong(4, comment.getBoardId());
            pstmt.executeUpdate();
            rs = pstmt.getGeneratedKeys();
            long comentId = 0L;
            if(rs.next()){
                comentId = rs.getLong(1);
            }else{
                throw new SQLException("생성된 ID가 없음");
            }
            Comment newComment = new Comment(comentId, comment.getContents(), comment.getCommenter(), comment.getCommenterProfile(), comment.getBoardId());
            return Optional.of(newComment);
        } catch (SQLException e) {
            log.error("save 예외: ", e);
        }finally {
            close(rs, pstmt, con);
        }
        return Optional.empty();
    }
}
